package com.axiel7.anihyou.utils

import androidx.compose.ui.graphics.Color

object ColorUtils {

    fun colorFromHex(color: String?) = if (color != null) try {
        Color(android.graphics.Color.parseColor(color))
    } catch (e: IllegalArgumentException) {
        null
    } else null

    fun Int.hexToString() = String.format("#%06X", 0xFFFFFF and this)
}