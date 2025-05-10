package com.axiel7.anihyou.core.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class DefaultTab : Localizable {
    LAST_USED,
    HOME,
    ANIME,
    MANGA,
    PROFILE,
    EXPLORE;

    @get:StringRes
    val stringRes: Int
        get() = when (this) {
            LAST_USED -> R.string.last_used
            HOME -> R.string.home
            ANIME -> R.string.anime
            MANGA -> R.string.manga
            PROFILE -> R.string.profile
            EXPLORE -> R.string.explore
        }

    @Composable
    override fun localized() = stringResource(id = stringRes)

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }

        fun valueOf(index: Int) = entries.find { it.ordinal == index }
    }
}