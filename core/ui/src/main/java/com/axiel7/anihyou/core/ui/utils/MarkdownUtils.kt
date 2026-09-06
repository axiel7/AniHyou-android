package com.axiel7.anihyou.core.ui.utils

import java.net.URLDecoder
import java.net.URLEncoder

object MarkdownUtils {
    private const val ANIHYOU_SPOILER_SCHEME = "anihyouspoiler://"

    private val imageRegex = Regex("([iI]mg\\d*%*)\\((.*?)\\)")
    private val youtubeRegex = Regex("[yY]outube\\((.*?)\\)")
    private val spoilerRegex = Regex("~!(.*?)!~", RegexOption.DOT_MATCHES_ALL)

    fun String.formatCompatibleMarkdown() = this
        .removeCenterMarkdown()
        .formatImageTags()
        .formatYoutubeTags()
        .formatSpoilerTags()

    private fun String.formatImageTags() =
        replace(imageRegex, $$"\n![View image]($2)\n")

    private fun String.formatYoutubeTags() =
        replace(youtubeRegex) {
            val url = it.groupValues[1]
            val videoId = when {
                url.contains("youtu.be/") -> url.substringAfterLast("/").substringBefore("?")
                url.contains("watch?v=") -> url.substringAfter("watch?v=").substringBefore("&")
                url.contains("embed/") -> url.substringAfter("embed/").substringBefore("?")
                else -> url.substringAfterLast("/")
            }
            //TODO: change when this is fixed https://github.com/mikepenz/multiplatform-markdown-renderer/issues/635
            //"\n[![YouTube Video](https://img.youtube.com/vi/$videoId/hqdefault.jpg)](https://www.youtube.com/watch?v=$videoId)\n"
            "[$url]($url)"
        }

    private fun String.formatSpoilerTags() =
        replace(spoilerRegex) {
            val spoilerEncoded = URLEncoder.encode(it.groupValues[1], "UTF-8")
            "\n[View spoiler]($ANIHYOU_SPOILER_SCHEME$spoilerEncoded)\n"
        }

    private fun String.removeCenterMarkdown() =
        replace("~~~", "")

    fun String.onMarkdownLinkClicked(
        onSpoilerClicked: (String) -> Unit,
        onLinkClicked: (String) -> Unit,
    ) {
        when {
            startsWith(ANIHYOU_SPOILER_SCHEME) ->
                onSpoilerClicked(
                    URLDecoder.decode(removePrefix(ANIHYOU_SPOILER_SCHEME), "UTF-8")
                )

            else -> onLinkClicked(this)
        }
    }
}