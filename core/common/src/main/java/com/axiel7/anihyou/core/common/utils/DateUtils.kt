package com.axiel7.anihyou.core.common.utils

import android.text.format.DateFormat
import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import kotlin.math.absoluteValue

object DateUtils {

    fun Int.minutesToDays() = this / 1440

    val defaultZoneOffset: ZoneOffset get() = ZonedDateTime.now(ZoneId.systemDefault()).offset

    fun String.toIsoFormat(inputFormat: DateTimeFormatter) =
        LocalDate.parse(this, inputFormat).toString()

    fun LocalDate.toEpochMillis() = this.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

    fun LocalDate.getNextDayOfWeek(dayOfWeek: DayOfWeek): LocalDate =
        with(TemporalAdjusters.nextOrSame(dayOfWeek))

    fun LocalDate.toLocalized(
        style: FormatStyle = FormatStyle.MEDIUM
    ): String? = try {
        this.format(DateTimeFormatter.ofLocalizedDate(style)).orEmpty()
    } catch (_: DateTimeException) {
        null
    }

    fun LocalDateTime.toLocalized(
        style: FormatStyle = FormatStyle.MEDIUM
    ): String? = try {
        this.format(DateTimeFormatter.ofLocalizedDate(style)).orEmpty()
    } catch (_: DateTimeException) {
        null
    }

    private fun LocalDateTime.toCalendar(): GregorianCalendar =
        GregorianCalendar.from(this.atZone(ZoneId.systemDefault()))

    val currentYear = Calendar.getInstance()[Calendar.YEAR]
    private const val BASE_YEAR = 1917
    val seasonYears = ((currentYear + 1) downTo BASE_YEAR).toList()

    fun currentTimeSeconds() = System.currentTimeMillis() / 1000

    fun LocalDateTime.tomorrow(): LocalDateTime = toCalendar()
        .apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        .toZonedDateTime().toLocalDateTime()

    /**
     * @returns the requested weekday timestamp (start or end of the day)
     */
    fun LocalDateTime.thisWeekdayTimestamp(dayOfWeek: DayOfWeek, isEndOfDay: Boolean): Long {
        val diff = dayOfWeek.value - this.dayOfWeek.value
        val weekdayDate = this.plusDays(diff.toLong()).toLocalDate()
        return if (isEndOfDay) {
            weekdayDate.plusDays(1).atStartOfDay().minusNanos(1).toEpochSecond(defaultZoneOffset)
        } else {
            weekdayDate.atStartOfDay().minusNanos(1).toEpochSecond(defaultZoneOffset)
        }
    }

    fun Long.timestampToDateString(
        format: String = "EE, d MMM"
    ): String? =
        LocalDateTime.ofEpochSecond(this, 0, defaultZoneOffset).format(
            DateTimeFormatter.ofPattern(
                DateFormat.getBestDateTimePattern(Locale.getDefault(), format)
            )
        )

    fun Long.timestampToTimeString(): String? =
        LocalDateTime.ofEpochSecond(this, 0, defaultZoneOffset).format(
            DateTimeFormatter.ofPattern("HH:mm")
        )

    /**
     * Difference in seconds since now
     */
    fun Long.timestampIntervalSinceNow() =
        (System.currentTimeMillis() - this * 1000).absoluteValue / 1000

    /**
     * @return the date in LocalDate, null if fails
     */
    fun Long.millisToLocalDate(): LocalDate? = runCatching {
        Instant.ofEpochMilli(this).atZone(ZoneId.of("UTC")).toLocalDate()
    }.getOrNull()

    /**
     * @return the date in LocalDate, null if fails
     */
    fun Long.secondsToLocalDateTime(): LocalDateTime? = runCatching {
        Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }.getOrNull()
}