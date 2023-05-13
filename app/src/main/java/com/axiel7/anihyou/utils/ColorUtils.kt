package com.axiel7.anihyou.utils

import androidx.compose.ui.graphics.Color

object ColorUtils {

    fun colorFromHex(color: String) = Color(android.graphics.Color.parseColor(color))

}