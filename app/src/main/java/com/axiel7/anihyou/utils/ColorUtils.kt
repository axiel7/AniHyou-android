package com.axiel7.anihyou.utils

import androidx.compose.ui.graphics.Color
import java.util.Locale

object ColorUtils {

    fun colorFromHex(color: String?) = if (!color.isNullOrEmpty()) try {
        Color(android.graphics.Color.parseColor(color))
    } catch (e: IllegalArgumentException) {
        null
    } else null

    fun Int.hexToString() = String.format("#%06X", 0xFFFFFF and this)

    val Color.hexCode: String
        inline get() {
            val a: Int = (alpha * 255).toInt()
            val r: Int = (red * 255).toInt()
            val g: Int = (green * 255).toInt()
            val b: Int = (blue * 255).toInt()
            return java.lang.String.format(Locale.getDefault(), "%02X%02X%02X%02X", a, r, g, b)
        }
}