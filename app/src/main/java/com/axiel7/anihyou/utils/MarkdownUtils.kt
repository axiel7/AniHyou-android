package com.axiel7.anihyou.utils

import java.net.URLDecoder
import java.net.URLEncoder

object MarkdownUtils {

    private const val ANIHYOU_IMAGE_SCHEME = "anihyouimage"
    private const val ANIHYOU_SPOILER_SCHEME = "anihyouspoiler://"

    private val imageRegex = Regex("(img\\d*%*)\\((.*)\\)")
    private val spoilerRegex = Regex("~!(.*)!~")
    private val centerRegex = Regex("~~~(.*)~~~", RegexOption.DOT_MATCHES_ALL)

    fun String.formatCompatibleMarkdown() = this
        .removeCenterMarkdown()
        .formatImageTags()
        .formatSpoilerTags()

    private fun String.formatImageTags() =
        replace(imageRegex, "\n[View image]($ANIHYOU_IMAGE_SCHEME\$2)\n")

    private fun String.formatSpoilerTags() =
        replace(spoilerRegex) {
            val spoilerEncoded = URLEncoder.encode(it.groupValues[1], "UTF-8")
            "\n[View spoiler]($ANIHYOU_SPOILER_SCHEME$spoilerEncoded)\n"
        }

    private fun String.removeCenterMarkdown() =
        replace(centerRegex, "\$1")

    fun String.onMarkdownLinkClicked(
        onImageClicked: (String) -> Unit,
        onSpoilerClicked: (String) -> Unit,
        onLinkClicked: (String) -> Unit,
    ) {
        when {
            startsWith(ANIHYOU_IMAGE_SCHEME) -> onImageClicked(removePrefix(ANIHYOU_IMAGE_SCHEME))
            startsWith(ANIHYOU_SPOILER_SCHEME) ->
                onSpoilerClicked(
                    URLDecoder.decode(removePrefix(ANIHYOU_SPOILER_SCHEME), "UTF-8")
                )

            else -> onLinkClicked(this)
        }
    }
}