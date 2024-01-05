package com.axiel7.anihyou.utils

import android.icu.text.CompactDecimalFormat
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

object NumberUtils {

    private val defaultNumberFormat: NumberFormat = NumberFormat.getInstance()

    private val defaultCompactDecimalFormat
        @RequiresApi(Build.VERSION_CODES.N)
        get() = CompactDecimalFormat.getInstance(
            Locale.getDefault(),
            CompactDecimalFormat.CompactStyle.SHORT
        )

    private val defaultDecimalFormat = DecimalFormat()

    fun Number.format(): String? = try {
        defaultNumberFormat.format(this)
    } catch (e: IllegalArgumentException) {
        null
    }

    fun Double.format(decimalLength: Int): String? {
        var pattern = "0"
        if (decimalLength > 0) {
            pattern = "0."
            pattern += "0".repeat(decimalLength)
        }
        return try {
            defaultDecimalFormat.applyPattern(pattern)
            defaultDecimalFormat.format(this)
        } catch (e: IllegalArgumentException) {
            null
        } catch (e: ArithmeticException) {
            null
        }
    }

    fun Int.abbreviated(): String? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        try {
            defaultCompactDecimalFormat.format(this)
        } catch (e: IllegalArgumentException) {
            null
        }
    } else {
        this.format()
    }

    fun Int?.formatPositiveValueOrUnknown() =
        if (this != null && this > 0) this.format() ?: UNKNOWN_CHAR else UNKNOWN_CHAR

    fun Double?.formatPositiveValueOrUnknown() =
        if (this != null && this > 0) this.format() ?: UNKNOWN_CHAR else UNKNOWN_CHAR

    /**
     * Parses the string as a [Double] number and returns the result.
     * It uses the default locale to parse the input
     */
    fun String.toDoubleLocaleInvariant() = try {
        defaultNumberFormat.parse(this)?.toDouble()
    } catch (e: ParseException) {
        null
    }

    /**
     * Parses the string as a [Float] number and returns the result
     * or `null` if the string is not a valid representation of a number.
     * Can be called with comma as decimal separator.
     */
    fun String.toFloatLocaleInvariant() = try {
        defaultNumberFormat.parse(this)?.toFloat()
    } catch (e: ParseException) {
        null
    }

    /**
     * @return if true 1 else 0
     */
    fun Boolean?.toInt(): Int = if (this == true) 1 else 0

    fun Int?.toStringOrZero() = this?.toString() ?: "0"

    fun Int?.toStringOrUnknown() = this?.toString() ?: UNKNOWN_CHAR

    /**
     * Returns a string representation of the Integer.
     * If the Integer is `<= 0` or `null` returns `"─"`.
     */
    fun Int?.toStringPositiveValueOrUnknown() =
        if (this == 0) UNKNOWN_CHAR else this.toStringOrUnknown()

    fun Int?.isGreaterThanZero() = this != null && this > 0

    fun Float?.toStringOrZero() = this?.toString() ?: "0.0"

    fun Float?.toStringOrUnknown() = this?.toString() ?: UNKNOWN_CHAR

    /**
     * Returns a string representation of the Float.
     * If the Float is `<= 0` or `null` returns `"─"`.
     */
    fun Float?.toStringPositiveValueOrUnknown() =
        if (this == 0f) UNKNOWN_CHAR else this.toStringOrUnknown()
}