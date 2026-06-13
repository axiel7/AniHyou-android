package com.axiel7.anihyou.core.common.utils

import android.icu.text.CompactDecimalFormat
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

object NumberUtils {
    private val defaultNumberFormat: NumberFormat = NumberFormat.getInstance()

    private val defaultDecimalFormat = DecimalFormat()

    private val defaultCompactDecimalFormat
        get() = CompactDecimalFormat.getInstance(
            Locale.getDefault(),
            CompactDecimalFormat.CompactStyle.SHORT
        )

    fun Int.abbreviated(): String? = runCatching {
        defaultCompactDecimalFormat.format(this)
    }.getOrNull()

    fun Number.format(): String? = try {
        defaultNumberFormat.format(this)
    } catch (_: IllegalArgumentException) {
        null
    }

    fun Double.format(decimalLength: Int): String? {
        var pattern = "0"
        if (decimalLength > 0) {
            pattern = "0."
            pattern += "#".repeat(decimalLength)
        }
        return try {
            defaultDecimalFormat.applyPattern(pattern)
            defaultDecimalFormat.format(this)
        } catch (_: IllegalArgumentException) {
            null
        } catch (_: ArithmeticException) {
            null
        }
    }

    fun Int?.formatPositiveValueOrUnknown() =
        if (this != null && this > 0) this.format() ?: UNKNOWN_CHAR else UNKNOWN_CHAR

    fun Double?.formatPositiveValueOrUnknown() =
        if (this != null && this > 0) this.format() ?: UNKNOWN_CHAR else UNKNOWN_CHAR

    /**
     * Parses the string as a [Double] number and returns the result.
     * It uses the default locale to parse the input
     */
    fun String.toDoubleLocaleInvariant() = toDoubleOrNull()

    /**
     * @return if true 1 else 0
     */
    fun Boolean?.toInt(): Int = if (this == true) 1 else 0

    fun Int?.toStringOrUnknown() = this?.toString() ?: UNKNOWN_CHAR

    fun Int?.isGreaterThanZero() = this != null && this > 0

    fun Double?.isGreaterThanZero() = this != null && this > 0

    fun Double?.isNullOrZero() = this == null || this == 0.0

    fun Float?.toStringOrUnknown() = this?.toString() ?: UNKNOWN_CHAR
}