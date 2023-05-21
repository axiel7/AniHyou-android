package com.axiel7.anihyou.utils


import android.icu.text.CompactDecimalFormat
import android.os.Build
import androidx.annotation.RequiresApi
import com.axiel7.anihyou.utils.StringUtils.toStringOrNull
import java.text.NumberFormat
import java.util.Locale

object NumberUtils {

    private val defaultNumberFormat: NumberFormat = NumberFormat.getInstance()

    private val defaultDecimalFormat @RequiresApi(Build.VERSION_CODES.N)
    get() = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)

    fun Int.format(): String = defaultNumberFormat.format(this)

    fun Int.abbreviated(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        defaultDecimalFormat.format(this)
    } else {
        this.format()
    }

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
    fun Int?.toStringPositiveValueOrUnknown() = if (this == 0) UNKNOWN_CHAR else this.toStringOrUnknown()

    fun Float?.toStringOrZero() = this?.toString() ?: "0.0"

    fun Float?.toStringOrUnknown() = this?.toString() ?: UNKNOWN_CHAR

    /**
     * Returns a string representation of the Float.
     * If the Float is `<= 0` or `null` returns `"─"`.
     */
    fun Float?.toStringPositiveValueOrUnknown() = if (this == 0f) UNKNOWN_CHAR else this.toStringOrUnknown()
}