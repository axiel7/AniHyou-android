package com.axiel7.anihyou.core.model

import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

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