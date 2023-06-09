package com.axiel7.anihyou.utils

object MarkdownUtils {

    fun String.formatImageTags() =
        replace(Regex("(img\\d*%*)\\((\\S*)\\)"), "[View image](anihyouimage\$2)\n")
}