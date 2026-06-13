package com.axiel7.anihyou

import com.axiel7.anihyou.feature.stream.data.repository.MiruroPipeClient
import okhttp3.OkHttpClient
import okio.ByteString.Companion.toByteString
import okio.ByteString.Companion.decodeBase64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

class StreamFeatureTest {

    private val client = MiruroPipeClient(OkHttpClient())

    private fun gzipCompress(data: String): ByteArray {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).use { it.write(data.toByteArray(Charsets.UTF_8)) }
        return bos.toByteArray()
    }

    @Test
    fun testEncodeEpisodeId() {
        val originalId = "anime:123"
        // base64 url safe for "anime:123" is YW5pbWU6MTIz
        val expected = "YW5pbWU6MTIz"
        val encoded = client.encodeEpisodeId(originalId)
        assertEquals(expected, encoded)
    }

    @Test
    fun testEncodePipeRequest() {
        val path = "/episodes"
        val query = mapOf("id" to "123", "provider" to "gogo")
        val encoded = client.encodePipeRequest(path, "GET", query)
        assertNotNull(encoded)

        // Decode it back and verify contents
        val jsonBytes = encoded.decodeBase64()?.utf8()
        assertNotNull(jsonBytes)
        
        assertTrue(jsonBytes!!.contains("\"path\":\"/episodes\""))
        assertTrue(jsonBytes.contains("\"method\":\"GET\""))
        assertTrue(jsonBytes.contains("\"query\":{"))
        assertTrue(jsonBytes.contains("\"id\":\"123\""))
        assertTrue(jsonBytes.contains("\"provider\":\"gogo\""))
        assertTrue(jsonBytes.contains("\"version\":\"0.1.0\""))
    }

    @Test
    fun testDecodePipeResponse() {
        val rawJson = "{\"status\":\"ok\",\"episodes\":[]}"
        val compressedBytes = gzipCompress(rawJson)
        val encoded = compressedBytes.toByteString(0, compressedBytes.size).base64Url()
        
        val decoded = client.decodePipeResponse(encoded)
        assertEquals(rawJson, decoded)
    }

    @Test
    fun testDecodeBase64Ids() {
        // "YW5pbWU6MTIz" -> "anime:123"
        // "YW5pbWU6NDU2" -> "anime:456"
        val rawJsonWithBase64 = """
            {
                "id": "YW5pbWU6MTIz",
                "other_field": "not_base64",
                "nested": {
                    "id": "YW5pbWU6NDU2"
                }
            }
        """.trimIndent()

        val expectedJson = """
            {
                "id":"anime:123",
                "other_field": "not_base64",
                "nested": {
                    "id":"anime:456"
                }
            }
        """.trimIndent()

        val decoded = client.decodeBase64Ids(rawJsonWithBase64)
        // Normalize whitespaces for comparison
        assertEquals(
            expectedJson.replace("\\s".toRegex(), ""),
            decoded.replace("\\s".toRegex(), "")
        )
    }
}
