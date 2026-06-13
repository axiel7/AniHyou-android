package com.axiel7.anihyou.feature.stream.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.ByteString.Companion.decodeBase64
import okio.ByteString.Companion.toByteString
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

private const val PIPE_URL = "https://www.miruro.tv/api/secure/pipe"
private const val PIPE_VERSION = "0.1.0"

/**
 * Replicates the Miruro API's `_encode_pipe_request` / `_decode_pipe_response` Python logic
 * entirely in Kotlin using only java.util.zip and okio.ByteString.
 *
 * Pipe request format (Python → Kotlin):
 *   base64.urlsafe_b64encode(json.dumps(payload).encode())
 *
 * Pipe response format:
 *   base64.urlsafe_b64decode(encoded_str) → gzip.decompress() → json.loads()
 */
class MiruroPipeClient(
    private val okHttpClient: OkHttpClient,
) {
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    /** Encode a JSON payload dict to the base64 pipe format. */
    fun encodePipeRequest(
        path: String,
        method: String = "GET",
        query: Map<String, String> = emptyMap(),
    ): String {
        val queryObj = buildJsonObject { query.forEach { (k, v) -> put(k, v) } }
        val payload = buildJsonObject {
            put("path", path)
            put("method", method)
            put("query", queryObj)
            put("body", null as String?)
            put("version", PIPE_VERSION)
        }
        val jsonBytes = payload.toString().toByteArray(Charsets.UTF_8)
        return jsonBytes.toByteString().base64Url()
    }

    /** Decode a base64+gzip pipe response to a raw JSON string. */
    fun decodePipeResponse(encoded: String): String {
        val compressedBytes = encoded.decodeBase64()?.toByteArray() 
            ?: error("Invalid Base64 response")
        return GZIPInputStream(ByteArrayInputStream(compressedBytes)).bufferedReader(Charsets.UTF_8).readText()
    }

    /** Recursively walk a JSON string and decode any base64-encoded `id` fields. */
    fun decodeBase64Ids(rawJson: String): String {
        // Fast path: replaces quoted base64 ids in the JSON text.
        // The Miruro API encodes episode IDs as base64; we decode them to plain strings.
        return rawJson.replace(Regex("\"id\"\\s*:\\s*\"([A-Za-z0-9+/=_-]{10,})\"")) { match ->
            val encoded = match.groupValues[1]
            val decoded = runCatching {
                val bytes = encoded.decodeBase64()?.utf8()
                // Only accept if it looks like a real id (contains ':')
                if (bytes != null && ':' in bytes) bytes else null
            }.getOrNull()
            if (decoded != null) "\"id\":\"$decoded\"" else match.value
        }
    }

    /** Execute a GET request against the Miruro pipe endpoint. */
    suspend fun pipeGet(path: String, query: Map<String, String> = emptyMap()): String =
        withContext(Dispatchers.IO) {
            val encoded = encodePipeRequest(path, "GET", query)
            val request = Request.Builder()
                .url("$PIPE_URL?e=$encoded")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .addHeader("Referer", "https://www.miruro.tv/")
                .build()
            val response = okHttpClient.newCall(request).execute()
            val body = response.body?.string()?.trim()
                ?: error("Empty pipe response for path=$path")
            val decoded = decodePipeResponse(body)
            decodeBase64Ids(decoded)
        }

    /**
     * Encode a plain episodeId to base64 for the /sources query.
     * (Python: base64.urlsafe_b64encode(episodeId.encode()).decode().rstrip('='))
     */
    fun encodeEpisodeId(episodeId: String): String =
        episodeId.toByteArray(Charsets.UTF_8).toByteString().base64Url()
}

