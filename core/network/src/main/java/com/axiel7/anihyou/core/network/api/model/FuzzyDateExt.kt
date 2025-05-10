package com.axiel7.anihyou.core.network.api.model

import com.apollographql.apollo.api.Optional
import com.axiel7.anihyou.core.network.fragment.FuzzyDate
import com.axiel7.anihyou.core.network.type.FuzzyDateInput
import java.time.LocalDate
import java.time.LocalDateTime

fun LocalDateTime.toFuzzyDate() = FuzzyDateInput(
    year = Optional.present(year),
    month = Optional.present(monthValue),
    day = Optional.present(dayOfMonth)
)

fun FuzzyDate?.isNull(): Boolean {
    return this == null || (day == null && month == null && year == null)
}

fun FuzzyDate.toLocalDate(): LocalDate? {
    return if (day != null && month != null && year != null)
        LocalDate.of(year, month, day)
    else null
}

fun FuzzyDate.toFuzzyDateInput() = FuzzyDateInput(
    year = Optional.present(year),
    month = Optional.present(month),
    day = Optional.present(day)
)

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