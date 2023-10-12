package com.axiel7.anihyou.ui.screens.profile.favorites

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

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