package com.axiel7.anihyou.utils

import androidx.compose.ui.graphics.Color

object ColorUtils {

    fun colorFromHex(color: String?) = if (color != null)
        Color(android.graphics.Color.parseColor(color))
    else null

    fun Int.hexToString() = String.format("#%06X", 0xFFFFFF and this)
}