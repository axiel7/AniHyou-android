package com.axiel7.anihyou.core.model.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.network.api.model.AnimeSeasonDto
import com.axiel7.anihyou.core.resources.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.YearMonth

@Stable
data class AnimeSeason(
    val year: Int,
    val season: MediaSeason
) : Localizable {
    @Composable
    override fun localized() = "${season.localized()} $year"

    fun toDto() = AnimeSeasonDto(year, season)
}

@Composable
fun MediaSeason.localized() = when (this) {
    MediaSeason.WINTER -> stringResource(R.string.winter)
    MediaSeason.SPRING -> stringResource(R.string.spring)
    MediaSeason.SUMMER -> stringResource(R.string.summer)
    MediaSeason.FALL -> stringResource(R.string.fall)
    MediaSeason.UNKNOWN__ -> stringResource(R.string.unknown)
}

fun MediaSeason.icon() = when (this) {
    MediaSeason.WINTER -> R.drawable.ac_unit_24
    MediaSeason.SPRING -> R.drawable.local_florist_24
    MediaSeason.SUMMER -> R.drawable.sunny_24
    MediaSeason.FALL -> R.drawable.rainy_24
    MediaSeason.UNKNOWN__ -> R.drawable.error_24
}

fun MediaSeason.iconSmall() = when (this) {
    MediaSeason.WINTER -> R.drawable.ac_unit_20
    MediaSeason.SPRING -> R.drawable.local_florist_20
    MediaSeason.SUMMER -> R.drawable.sunny_20
    MediaSeason.FALL -> R.drawable.rainy_20
    MediaSeason.UNKNOWN__ -> R.drawable.error_20
}

fun LocalDateTime.season(): MediaSeason {
    return when (this.month) {
        Month.JANUARY, Month.FEBRUARY, Month.DECEMBER -> MediaSeason.WINTER
        Month.MARCH, Month.APRIL, Month.MAY -> MediaSeason.SPRING
        Month.JUNE, Month.JULY, Month.AUGUST -> MediaSeason.SUMMER
        Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER -> MediaSeason.FALL
    }
}

fun LocalDateTime.currentAnimeSeason(): AnimeSeason {
    var animeSeason = AnimeSeason(year = year, season = season())
    if (month == Month.DECEMBER) {
        animeSeason = animeSeason.copy(year = year + 1)
    }
    return animeSeason
}

fun LocalDateTime.nextAnimeSeason(): AnimeSeason {
    val current = currentAnimeSeason()
    return when (current.season) {
        MediaSeason.WINTER -> current.copy(season = MediaSeason.SPRING)
        MediaSeason.SPRING -> current.copy(season = MediaSeason.SUMMER)
        MediaSeason.SUMMER -> current.copy(season = MediaSeason.FALL)
        MediaSeason.FALL -> current.copy(
            season = MediaSeason.WINTER,
            year = year + 1
        )

        MediaSeason.UNKNOWN__ -> current
    }
}

fun MediaSeason.startMonth(): Month = when (this) {
    MediaSeason.WINTER -> Month.DECEMBER
    MediaSeason.SPRING -> Month.MARCH
    MediaSeason.SUMMER -> Month.JUNE
    MediaSeason.FALL -> Month.SEPTEMBER
    MediaSeason.UNKNOWN__ -> Month.JANUARY
}

fun MediaSeason.endMonth(): Month = when (this) {
    MediaSeason.WINTER -> Month.FEBRUARY
    MediaSeason.SPRING -> Month.MAY
    MediaSeason.SUMMER -> Month.AUGUST
    MediaSeason.FALL -> Month.NOVEMBER
    MediaSeason.UNKNOWN__ -> Month.JANUARY
}

fun currentSeasonStartDate(): LocalDate {
    val now = LocalDateTime.now()
    val startMonth = now.season().startMonth()
    val startYear =
        if (now.month == Month.JANUARY || now.month == Month.FEBRUARY) now.year - 1 else now.year
    return LocalDate.of(startYear, startMonth, 1)
}

fun currentSeasonEndDate(): LocalDate {
    val now = LocalDateTime.now()
    val endMonth = now.season().endMonth()
    val endYear = if (now.month == Month.DECEMBER) now.year + 1 else now.year
    val endDay = YearMonth.of(endYear, endMonth).lengthOfMonth()
    return LocalDate.of(endYear, endMonth, endDay)
}