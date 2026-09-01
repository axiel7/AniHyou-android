package com.axiel7.anihyou.core.ui.utils

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.common.utils.DateUtils
import com.axiel7.anihyou.core.network.fragment.FuzzyDate
import com.axiel7.anihyou.core.resources.R
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object ComposeDateUtils {

    /**
     * Converts seconds to years, months, weeks, days, hours or minutes.
     * Depending on if there is enough time.
     * It always takes the 3 biggest quantities available.
     * @param maxUnit maximum time unit to display, if the time exceed the limit then it is shown as a date
     * @param buildPluralString optional parameter to use in Glance. By default, it uses compose [pluralStringResource]
     */
    @Composable
    fun Long.secondsToLegibleText(
        maxUnit: ChronoUnit = ChronoUnit.YEARS,
        buildPluralString: @Composable (id: Int, time: Long) -> String = { id, time ->
            pluralStringResource(id = id, count = time.toInt(), time)
        }
    ): String {
        var remaining = this

        val years = if (maxUnit >= ChronoUnit.YEARS) (remaining / 31556952).also { remaining %= 31556952 } else 0
        val months = if (maxUnit >= ChronoUnit.MONTHS) (remaining / 2629746).also { remaining %= 2629746 } else 0
        val weeks = if (maxUnit >= ChronoUnit.WEEKS) (remaining / 604800).also { remaining %= 604800 } else 0
        val days = if (maxUnit >= ChronoUnit.DAYS) (remaining / 86400).also { remaining %= 86400 } else 0
        val hours = if (maxUnit >= ChronoUnit.HOURS) (remaining / 3600).also { remaining %= 3600 } else 0
        val minutes = remaining / 60

        val parts = mutableListOf<String>()

        if (years > 0) parts.add(buildPluralString(R.plurals.num_years, years))
        if (months > 0) parts.add(buildPluralString(R.plurals.num_months, months))
        if (weeks > 0) parts.add(buildPluralString(R.plurals.num_weeks, weeks))
        if (days > 0) parts.add(buildPluralString(R.plurals.num_days, days))
        if (hours > 0) parts.add(buildPluralString(R.plurals.hour_abbreviation, hours))
        if (minutes > 0) parts.add(buildPluralString(R.plurals.minutes_abbreviation, minutes))

        return parts.take(2).joinToString(" ")
    }

    @Composable
    fun Long.dateToRelativeText(): String {
        val now = LocalDateTime.now(DateUtils.defaultZoneOffset)
        val past = LocalDateTime.ofInstant(Instant.ofEpochSecond(this), DateUtils.defaultZoneOffset)

        val years = ChronoUnit.YEARS.between(past, now).toInt()
        if (years > 0) return pluralStringResource(R.plurals.num_years, years, years)

        val months = ChronoUnit.MONTHS.between(past, now).toInt()
        if (months > 0) return pluralStringResource(R.plurals.num_months, months, months)

        val weeks = ChronoUnit.WEEKS.between(past, now).toInt()
        if (weeks > 0) return pluralStringResource(R.plurals.num_weeks, weeks, weeks)

        val days = ChronoUnit.DAYS.between(past, now).toInt()
        if (days > 0) return pluralStringResource(R.plurals.num_days, days, days)

        val hours = ChronoUnit.HOURS.between(past, now).toInt()
        if (hours > 0) return pluralStringResource(R.plurals.hour_abbreviation, hours, hours)

        val minutes = ChronoUnit.MINUTES.between(past, now).toInt()
        if (minutes > 0) return pluralStringResource(R.plurals.minutes_abbreviation, minutes, minutes)

        return stringResource(R.string.just_now)
    }

    @Composable
    fun Long.minutesToLegibleText(): String {
        val hours = (this / 60).toInt()
        return if (hours >= 1) {
            val minutes = (this % 60).toInt()
            pluralStringResource(
                id = R.plurals.hour_abbreviation,
                count = hours,
                hours
            ) + " " +
                    pluralStringResource(
                        id = R.plurals.minutes_abbreviation,
                        count = minutes,
                        minutes
                    )
        } else {
            pluralStringResource(id = R.plurals.minutes_abbreviation, this.toInt(), this.toInt())
        }
    }

    @Composable
    fun FuzzyDate.formatted(): String = when {
        month != null && year != null && day != null -> {
            try {
                LocalDate.of(year!!, month!!, day!!).format(
                    DateTimeFormatter.ofPattern(
                        DateFormat.getBestDateTimePattern(Locale.getDefault(), "d MMM yyyy")
                    )
                )
            } catch (_: DateTimeException) {
                "$year-$month-$day"
            }
        }

        month != null && year != null -> {
            try {
                LocalDate.of(year!!, month!!, 1).format(
                    DateTimeFormatter.ofPattern(
                        DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMM yyyy")
                    )
                )
            } catch (_: DateTimeException) {
                "$year-$month"
            }
        }

        month != null && day != null -> {
            try {
                LocalDate.of(DateUtils.currentYear, month!!, day!!).format(
                    DateTimeFormatter.ofPattern(
                        DateFormat.getBestDateTimePattern(Locale.getDefault(), "d MMM")
                    )
                )
            } catch (_: DateTimeException) {
                "$month-$day"
            }
        }

        year != null -> "$year"

        else -> stringResource(R.string.unknown)
    }
}