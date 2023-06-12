package com.axiel7.anihyou.utils

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.fragment.FuzzyDate
import com.axiel7.anihyou.type.FuzzyDateInput
import com.axiel7.anihyou.type.MediaSeason
import java.time.DateTimeException
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import kotlin.math.absoluteValue

object DateUtils {

    private val defaultZoneOffset get() = ZonedDateTime.now(ZoneId.systemDefault()).offset

    fun String.toIsoFormat(inputFormat: DateTimeFormatter) = LocalDate.parse(this, inputFormat).toString()

    fun LocalDate.toEpochMillis() = this.atStartOfDay().toInstant(defaultZoneOffset).toEpochMilli()

    fun LocalDate.getNextDayOfWeek(dayOfWeek: DayOfWeek): LocalDate = with(TemporalAdjusters.nextOrSame(dayOfWeek))

    fun LocalDate?.toLocalized(
        style: FormatStyle = FormatStyle.MEDIUM
    ): String = try {
        this?.format(DateTimeFormatter.ofLocalizedDate(style)) ?: ""
    } catch (e: DateTimeException) {
        ""
    }

    fun LocalDateTime?.toLocalized(
        style: FormatStyle = FormatStyle.MEDIUM
    ): String = try {
        this?.format(DateTimeFormatter.ofLocalizedDate(style)) ?: ""
    } catch (e: DateTimeException) {
        ""
    }

    fun LocalDateTime.toCalendar(): GregorianCalendar = GregorianCalendar.from(this.atZone(ZoneId.systemDefault()))

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private const val BASE_YEAR = 1917
    val seasonYears = ((currentYear + 1) downTo BASE_YEAR).toList()

    fun LocalDateTime.tomorrow(): LocalDateTime = toCalendar()
        .apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }
        .toZonedDateTime().toLocalDateTime()

    fun LocalDateTime.season(): MediaSeason {
        return when (this.month) {
            Month.JANUARY, Month.FEBRUARY, Month.DECEMBER -> MediaSeason.WINTER
            Month.MARCH, Month.APRIL, Month.MAY -> MediaSeason.SPRING
            Month.JUNE, Month.JULY, Month.AUGUST -> MediaSeason.SUMMER
            Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> MediaSeason.FALL
            else -> MediaSeason.UNKNOWN__
        }
    }

    fun LocalDateTime.currentAnimeSeason(): AnimeSeason {
        val animeSeason = AnimeSeason(year = year, season = season())
        if (month == Month.DECEMBER) {
            animeSeason.year += 1
        }
        return animeSeason
    }

    fun LocalDateTime.nextAnimeSeason(): AnimeSeason {
        val current = currentAnimeSeason()
        when (current.season) {
            MediaSeason.WINTER -> {
                current.season = MediaSeason.SPRING
                current.year += 1
            }
            MediaSeason.SPRING -> current.season = MediaSeason.SUMMER
            MediaSeason.SUMMER -> current.season = MediaSeason.FALL
            MediaSeason.FALL -> current.season = MediaSeason.WINTER
            else -> {}
        }
        return current
    }

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

    fun LocalDateTime.toFuzzyDate() = FuzzyDateInput(
        year = Optional.present(year),
        month = Optional.present(monthValue),
        day = Optional.present(dayOfMonth)
    )

    /**
     * Converts seconds to years, months, weeks, days, hours or minutes.
     * Depending if there is enough time.
     * Eg. If days greater than 1 and less than 6, returns "x days"
     */
    @Composable
    fun Long.secondsToLegibleText(): String {
        val days = this / 86400
        if (days > 6) {
            val weeks = this / 604800
            return if (weeks > 4) {
                val months = this / 2629746
                if (months > 12) {
                    val years = this / 31556952
                    stringResource(R.string.num_years).format(years)
                } else stringResource(R.string.num_months).format(months)
            } else stringResource(R.string.num_weeks).format(weeks)
        }
        else if (days >= 1) return stringResource(R.string.num_days).format(days)
        else {
            val hours = this / 3600
            return if (hours >= 1) "$hours ${stringResource(R.string.hour_abbreviation)}"
            else {
                val minutes = (this % 3600) / 60
                "$minutes ${stringResource(R.string.minutes_abbreviation)}"
            }
        }
    }

    @Composable
    fun Long.minutesToLegibleText(): String {
        val hours = this / 60
        return if (hours >= 1) {
            val minutes = this % 60
            "$hours ${stringResource(R.string.hour_abbreviation)} $minutes ${stringResource(R.string.minutes_abbreviation)}"
        } else "$this ${stringResource(R.string.minutes_abbreviation)}"
    }

    fun Long.minutesToDays() = this / 1440

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
    fun Long.timestampIntervalSinceNow() = (System.currentTimeMillis() - this * 1000).absoluteValue / 1000

    /**
     * @return the date in LocalDate, null if fails
     */
    fun Long.millisToLocalDate(): LocalDate? {
        return try {
            Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @return the date in LocalDate, null if fails
     */
    fun Long.secondsToLocalDateTime(): LocalDateTime? {
        return try {
            Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
        } catch (e: Exception) {
            null
        }
    }

    fun FuzzyDate.toLocalDate(): LocalDate? {
        return if (day != null && month != null && year != null)
            LocalDate.of(year, month, day)
        else null
    }

    fun LocalDate.toFuzzyDateInput() = FuzzyDateInput(
        year = Optional.present(year),
        month = Optional.present(monthValue),
        day = Optional.present(dayOfMonth)
    )

    fun LocalDate.toFuzzyDate() = FuzzyDate(
        year = year,
        month = monthValue,
        day = dayOfMonth
    )

    @Composable
    fun FuzzyDate.formatted(): String = when {
        month != null && year != null && day != null -> {
            LocalDate.of(year, month, day).format(
                DateTimeFormatter.ofPattern(
                    DateFormat.getBestDateTimePattern(Locale.getDefault(), "d MMM yyyy")
                )
            )
        }
        month != null && year != null -> {
            LocalDate.of(year, month, 1).format(
                DateTimeFormatter.ofPattern(
                    DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMM yyyy")
                )
            )
        }
        day == null && month == null && year != null -> "$year"
        else -> stringResource(R.string.unknown)
    }
}