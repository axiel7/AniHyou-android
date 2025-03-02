package com.axiel7.anihyou.ui.screens.home.current

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

@Keep
enum class CurrentListType: Localizable {
    AIRING,
    BEHIND,
    ANIME,
    MANGA;

    @Composable
    override fun localized() = when (this) {
        AIRING -> stringResource(R.string.airing)
        BEHIND -> stringResource(R.string.anime_behind)
        ANIME -> stringResource(R.string.watching)
        MANGA -> stringResource(R.string.reading)
    }
}