package com.axiel7.anihyou.utils

import java.net.URLDecoder
import java.net.URLEncoder

object MarkdownUtils {

    private const val ANIHYOU_SPOILER_SCHEME = "anihyouspoiler://"

    private val imageRegex = Regex("(img\\d*%*)\\((.*)\\)")
    private val spoilerRegex = Regex("~!(.*)!~")

    fun String.formatCompatibleMarkdown() = this
        .formatImageTags()
        .formatSpoilerTags()

    private fun String.formatImageTags() =
        replace(imageRegex, "\n![Loading image](\$2)\n")

    private fun String.formatSpoilerTags() =
        replace(spoilerRegex) {
            val spoilerEncoded = URLEncoder.encode(it.groupValues[1], "UTF-8")
            "\n[View spoiler]($ANIHYOU_SPOILER_SCHEME$spoilerEncoded)\n"
        }

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