package com.axiel7.anihyou.utils

object StringUtils {
    /**
     * Returns a string representation of the object.
     * Can be called with a null receiver, in which case it returns `null`.
     */
    fun Any?.toStringOrNull() = this.toString().let { if (it == "null") null else it }

    /**
     * Returns a string representation of the object.
     * Can be called with a null receiver, in which case it returns an empty String.
     */
    fun Any?.toStringOrEmpty() = this.toString().let { if (it == "null") "" else it }

    fun String.htmlStripped() = replace(Regex("<[^>]+>"), "")
}