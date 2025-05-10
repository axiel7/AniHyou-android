package com.axiel7.anihyou.feature.profile.favorites

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class FavoritesType : Localizable {
    ANIME,
    MANGA,
    CHARACTERS,
    STAFF,
    STUDIOS;

    @Composable
    override fun localized() = when (this) {
        ANIME -> stringResource(R.string.anime)
        MANGA -> stringResource(R.string.manga)
        CHARACTERS -> stringResource(R.string.characters)
        STAFF -> stringResource(R.string.staff)
        STUDIOS -> stringResource(R.string.studios)
    }
}