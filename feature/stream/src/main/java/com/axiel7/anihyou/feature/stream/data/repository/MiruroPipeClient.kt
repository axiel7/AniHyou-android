package com.axiel7.anihyou.feature.stream.data.repository

import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream

private const val PIPE_URL = "https://www.miruro.tv/api/secure/pipe"
private const val PIPE_VERSION = "0.1.0"

/**
 * Replicates the Miruro API's `_encode_pipe_request` / `_decode_pipe_response` Python logic
 * entirely in Kotlin using only java.util.zip and android.util.Base64.
 *
 * Pipe request format (Python → Kotlin):
 *   base64.urlsafe_b64encode(json.dumps(payload).encode())
 *
 * Pipe response format:
 *   base64.urlsafe_b64decode(encoded_str) → gzip.decompress() → json.loads()
 */
internal class MiruroPipeClient(
    private val okHttpClient: OkHttpClient,
) {
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    /** Encode a JSON payload dict to the base64 pipe format. */
    private fun encodePipeRequest(
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
        return Base64.encodeToString(jsonBytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    /** Decode a base64+gzip pipe response to a raw JSON string. */
    private fun decodePipeResponse(encoded: String): String {
        val padded = encoded + "=".repeat((4 - encoded.length % 4) % 4)
        val compressed = Base64.decode(padded, Base64.URL_SAFE)
        return GZIPInputStream(ByteArrayInputStream(compressed)).bufferedReader(Charsets.UTF_8).readText()
    }

    /** Recursively walk a JSON string and decode any base64-encoded `id` fields. */
    private fun decodeBase64Ids(rawJson: String): String {
        // Fast path: replaces quoted base64 ids in the JSON text.
        // The Miruro API encodes episode IDs as base64; we decode them to plain strings.
        return rawJson.replace(Regex("\"id\"\\s*:\\s*\"([A-Za-z0-9+/=_-]{10,})\"")) { match ->
            val encoded = match.groupValues[1]
            val decoded = runCatching {
                val padded = encoded + "=".repeat((4 - encoded.length % 4) % 4)
                val bytes = Base64.decode(padded, Base64.URL_SAFE)
                val str = String(bytes, Charsets.UTF_8)
                // Only accept if it looks like a real id (contains ':')
                if (':' in str) str else null
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
        Base64.encodeToString(
            episodeId.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
}
