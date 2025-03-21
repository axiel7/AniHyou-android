package com.axiel7.anihyou.core.ui.utils

import android.icu.text.CompactDecimalFormat
import java.util.Locale

object NumberUtils {

    private val defaultCompactDecimalFormat
        get() = CompactDecimalFormat.getInstance(
            Locale.getDefault(),
            CompactDecimalFormat.CompactStyle.SHORT
        )

    fun Int.abbreviated(): String? = runCatching {
        defaultCompactDecimalFormat.format(this)
    }.getOrNull()
}