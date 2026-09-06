package com.axiel7.anihyou.core.ui.utils

import com.axiel7.anihyou.core.ui.utils.MarkdownUtils.formatCompatibleMarkdown
import org.junit.Assert.assertEquals
import org.junit.Test
import java.net.URLEncoder

class MarkdownUtilsTest {

    @Test
    fun `formatCompatibleMarkdown should format image tags correctly`() {
        val input = "img(https://example.com/image.png)"
        val expected = "\n![View image](https://example.com/image.png)\n"
        assertEquals(expected, input.formatCompatibleMarkdown())
    }

    @Test
    fun `formatCompatibleMarkdown should format image tags with uppercase correctly`() {
        val input = "Img(https://example.com/image.png)"
        val expected = "\n![View image](https://example.com/image.png)\n"
        assertEquals(expected, input.formatCompatibleMarkdown())
    }

    @Test
    fun `formatCompatibleMarkdown should format image tags with dimensions correctly`() {
        val input = "img450(https://example.com/image.png)"
        val expected = "\n![View image](https://example.com/image.png)\n"
        assertEquals(expected, input.formatCompatibleMarkdown())
    }

    @Test
    fun `formatCompatibleMarkdown should format youtube tags correctly`() {
        val input = "youtube(https://www.youtube.com/watch?v=Z9trg537)"
        val expected = "\n[![YouTube Video](https://img.youtube.com/vi/Z9trg537/hqdefault.jpg)](https://www.youtube.com/watch?v=Z9trg537)\n"
        assertEquals(expected, input.formatCompatibleMarkdown())
    }

    @Test
    fun `formatCompatibleMarkdown should format youtube tags with youtu_be correctly`() {
        val input = "youtube(https://youtu.be/Z9trg537)"
        val expected = "\n[![YouTube Video](https://img.youtube.com/vi/Z9trg537/hqdefault.jpg)](https://www.youtube.com/watch?v=Z9trg537)\n"
        assertEquals(expected, input.formatCompatibleMarkdown())
    }

    @Test
    fun `formatCompatibleMarkdown should format youtube tags with uppercase correctly`() {
        val input = "Youtube(https://youtu.be/Z9trg537)"
        val expected = "\n[![YouTube Video](https://img.youtube.com/vi/Z9trg537/hqdefault.jpg)](https://www.youtube.com/watch?v=Z9trg537)\n"
        assertEquals(expected, input.formatCompatibleMarkdown())
    }

    @Test
    fun `formatCompatibleMarkdown should format spoiler tags correctly`() {
        val input = "~!This is a spoiler!~"
        val spoilerText = "This is a spoiler"
        val spoilerEncoded = URLEncoder.encode(spoilerText, "UTF-8")
        val expected = "\n[View spoiler](anihyouspoiler://$spoilerEncoded)\n"
        assertEquals(expected, input.formatCompatibleMarkdown())
    }

    @Test
    fun `formatCompatibleMarkdown should remove center markdown and keep content`() {
        val input = "~~~Centered text~~~"
        val expected = "Centered text"
        assertEquals(expected, input.formatCompatibleMarkdown())
    }

    @Test
    fun `formatCompatibleMarkdown should handle combined formatting`() {
        val input = "~~~img(https://example.com/image.png) ~!Spoiler content!~~~~"
        val spoilerEncoded = URLEncoder.encode("Spoiler content", "UTF-8")
        val expected = "\n![View image](https://example.com/image.png)\n \n[View spoiler](anihyouspoiler://$spoilerEncoded)\n"
        assertEquals(expected, input.formatCompatibleMarkdown())
    }
}
