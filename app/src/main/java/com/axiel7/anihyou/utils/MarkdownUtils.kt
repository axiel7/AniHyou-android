package com.axiel7.anihyou.utils

import java.net.URLDecoder
import java.net.URLEncoder

object MarkdownUtils {

    const val ANIHYOU_IMAGE_SCHEME = "anihyouimage"
    const val ANIHYOU_SPOILER_SCHEME = "anihyouspoiler://"

    fun String.formatImageTags() =
        replace(Regex("(img\\d*%*)\\((.*)\\)"), "\n[View image]($ANIHYOU_IMAGE_SCHEME\$2)\n")

    fun String.formatSpoilerTags() =
        replace(Regex("~!(.*)!~")) {
            val spoilerEncoded = URLEncoder.encode(it.groupValues[1], "UTF-8")
            "\n[View spoiler]($ANIHYOU_SPOILER_SCHEME$spoilerEncoded)\n"
        }

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