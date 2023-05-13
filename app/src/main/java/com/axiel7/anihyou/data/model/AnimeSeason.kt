package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.type.MediaSeason

data class AnimeSeason(
    var year: Int,
    var season: MediaSeason
) : Localizable {
    @Composable
    override fun localized() = "${season.localized()} $year"
}

@Composable
fun MediaSeason.localized() = when (this) {
    MediaSeason.WINTER -> stringResource(R.string.winter)
    MediaSeason.SPRING -> stringResource(R.string.spring)
    MediaSeason.SUMMER -> stringResource(R.string.summer)
    MediaSeason.FALL -> stringResource(R.string.fall)
    MediaSeason.UNKNOWN__ -> stringResource(R.string.unknown)
}