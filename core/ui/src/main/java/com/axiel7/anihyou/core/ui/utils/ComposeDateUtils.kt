package com.axiel7.anihyou.core.ui.utils

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.common.utils.DateUtils
import com.axiel7.anihyou.core.common.utils.DateUtils.toLocalized
import com.axiel7.anihyou.core.network.fragment.FuzzyDate
import com.axiel7.anihyou.core.resources.R
import java.time.DateTimeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object ComposeDateUtils {

    /**
     * Converts seconds to years, months, weeks, days, hours or minutes.
     * Depending if there is enough time.
     * Eg. If days greater than 1 and less than 6, returns "x days"
     * @param maxUnit maximum time unit to display, if the time exceed the limit then it is showed as a date
     * @param buildPluralString optional parameter to use in Glance. By default it uses compose [pluralStringResource]
     */
    @Composable
    fun Long.secondsToLegibleText(
        maxUnit: ChronoUnit = ChronoUnit.YEARS,
        isFutureDate: Boolean = true,
        buildPluralString: @Composable (id: Int, time: Long) -> String = { id, time ->
            pluralStringResource(id = id, count = time.toInt(), time)
        }
    ): String {
        val days = this / 86400
        when {
            days > 6 -> {
                val weeks = this / 604800
                if (weeks > 4) {
                    val months = this / 2629746
                    if (months > 12 && maxUnit >= ChronoUnit.YEARS) {
                        val years = this / 31556952
                        return buildPluralString(R.plurals.num_years, years)
                    } else if (maxUnit >= ChronoUnit.MONTHS) {
                        return buildPluralString(R.plurals.num_months, months)
                    }
                } else if (maxUnit >= ChronoUnit.WEEKS) {
                    return buildPluralString(R.plurals.num_weeks, weeks)
                }
            }

            days >= 1 && maxUnit >= ChronoUnit.DAYS -> {
                return buildPluralString(R.plurals.num_days, days)
            }

            else -> {
                val hours = this / 3600
                if (hours >= 1 && maxUnit >= ChronoUnit.HOURS) {
                    return buildPluralString(R.plurals.hour_abbreviation, hours)
                } else if (maxUnit >= ChronoUnit.MINUTES) {
                    val minutes = (this % 3600) / 60
                    return buildPluralString(R.plurals.minutes_abbreviation, minutes)
                }
            }
        }
        return LocalDateTime.now(DateUtils.defaultZoneOffset)
            .plusSeconds(if (isFutureDate) this else -this)
            .toLocalized().orEmpty()
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