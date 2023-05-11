package com.axiel7.anihyou.utils

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.AnimeSeason
import com.axiel7.anihyou.type.FuzzyDate
import com.axiel7.anihyou.type.FuzzyDateInput
import com.axiel7.anihyou.type.MediaSeason
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Locale
import kotlin.math.absoluteValue

object DateUtils {

    fun String.toIsoFormat(inputFormat: DateTimeFormatter) = LocalDate.parse(this, inputFormat).toString()

    fun LocalDate.toEpochMillis() = this.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

    fun LocalDate.getNextDayOfWeek(dayOfWeek: DayOfWeek) = with(TemporalAdjusters.nextOrSame(dayOfWeek))

    fun LocalDateTime.toCalendar() = GregorianCalendar.from(this.atZone(ZoneId.systemDefault()))

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
    fun LocalDateTime.thisWeekdayTimestamp(weekDayOfWeek: DayOfWeek, isEndOfDay: Boolean): Long {
        val diff = weekDayOfWeek.value - this.dayOfWeek.value
        val weekdayDate = this.plusDays(diff.toLong()).toLocalDate()
        return if (isEndOfDay) {
            weekdayDate.plusDays(1).atStartOfDay().minusNanos(1).toEpochSecond(ZoneOffset.UTC)
        } else {
            weekdayDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
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

    fun Long.timestampToDateString() = LocalDate.ofEpochDay(this / 86400).format(
        DateTimeFormatter.ofPattern(
            DateFormat.getBestDateTimePattern(Locale.getDefault(), "EE, d MMM")
        )
    )

    fun Long.timestampIntervalSinceNow() = (System.nanoTime() - this).absoluteValue

    @Composable
    fun FuzzyDateInput.formatted(): String {
        val dayValue = day.getOrNull()
        val monthValue = month.getOrNull()
        val yearValue = year.getOrNull()
        return when {
            monthValue != null && yearValue != null && dayValue != null -> {
                LocalDate.of(yearValue, monthValue, dayValue).format(
                    DateTimeFormatter.ofPattern(
                        DateFormat.getBestDateTimePattern(Locale.getDefault(), "d MMM yyyy")
                    )
                )
            }
            monthValue != null && yearValue != null -> {
                LocalDate.of(yearValue, monthValue, 1).format(
                    DateTimeFormatter.ofPattern(
                        DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMM yyyy")
                    )
                )
            }
            dayValue == null && monthValue == null && yearValue != null -> "$yearValue"
            else -> stringResource(R.string.unknown)
        }

    }
}