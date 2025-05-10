package com.axiel7.anihyou.core.resources

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt

object ColorUtils {

    fun colorFromHex(color: String?) = if (!color.isNullOrEmpty()) try {
        Color(color.toColorInt())
    } catch (_: IllegalArgumentException) {
        null
    } else null

    fun Int.hexToString() = String.format("#%06X", 0xFFFFFF and this)

    val Color.hexCode: String
        inline get() {
            val a: Int = (alpha * 255).toInt()
            val r: Int = (red * 255).toInt()
            val g: Int = (green * 255).toInt()
            val b: Int = (blue * 255).toInt()
            return String.format("%02X%02X%02X%02X", a, r, g, b)
        }

    val Color.isBlack: Boolean
        inline get() {
            return red == 0.0f && green == 0.0f && blue == 0.0f
        }

    val Color.isWhite: Boolean
        inline get() {
            return red == 1.0f && green == 1.0f && blue == 1.0f
        }
}