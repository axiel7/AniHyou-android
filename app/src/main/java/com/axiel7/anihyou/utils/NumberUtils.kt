package com.axiel7.anihyou.utils


import android.icu.text.CompactDecimalFormat
import android.os.Build
import androidx.annotation.RequiresApi
import com.axiel7.anihyou.utils.StringUtils.toStringOrNull
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
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

    private val decimalFormatSymbols = DecimalFormatSymbols.getInstance()
    private val thousandsSeparator = decimalFormatSymbols.groupingSeparator
    private val decimalSeparator = decimalFormatSymbols.decimalSeparator

    fun Int.format(): String = defaultNumberFormat.format(this)
    fun Long.format(): String = defaultNumberFormat.format(this)
    fun Double.format(): String = defaultNumberFormat.format(this)
    fun Double.format(decimalLength: Int): String {
        var pattern = "0"
        if (decimalLength > 0) {
            pattern = "0."
            pattern += "0".repeat(decimalLength)
        }
        defaultDecimalFormat.applyPattern(pattern)
        return defaultDecimalFormat.format(this)
    }

    fun Int.abbreviated(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        defaultCompactDecimalFormat.format(this)
    } else {
        this.format()
    }

    /**
     * Parses the string as a [Double] number and returns the result.
     * It uses the default locale to parse the input
     */
    fun String.toDoubleLocaleInvariant() = defaultNumberFormat.parse(this)?.toDouble()

    /**
     * Parses the string as a [Float] number and returns the result
     * or `null` if the string is not a valid representation of a number.
     * Can be called with comma as decimal separator.
     */
    fun String.toFloatLocaleInvariant() = defaultNumberFormat.parse(this)?.toFloat()

    /**
     * @return if true 1 else 0
     */
    fun Boolean?.toInt(): Int = if (this == true) 1 else 0

    /**
     * Returns a string representation of the Integer. If the Integer is `<= 0` returns `null`.
     * Can be called with a null receiver, in which case it returns `null`.
     */
    fun Int?.toStringPositiveValueOrNull() = if (this == 0) null else this.toStringOrNull()

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

    fun String.formatToDecimal(): String {
        if (matches("\\D".toRegex())) return ""
        if (matches("0+".toRegex())) return "0"

        val sb = StringBuilder()

        var hasDecimalSep = false

        for (char in this) {
            if (char.isDigit()) {
                sb.append(char)
                continue
            }
            if (char == decimalSeparator && !hasDecimalSep && sb.isNotEmpty()) {
                sb.append(char)
                hasDecimalSep = true
            }
        }

        return sb.toString()
    }
}