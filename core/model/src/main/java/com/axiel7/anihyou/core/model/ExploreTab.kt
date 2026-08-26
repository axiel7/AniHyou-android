package com.axiel7.anihyou.core.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class ExploreTab : Localizable {
    ANIME,
    MANGA,
    RECOMMENDATIONS;

    @Composable
    override fun localized() = stringResource(stringRes)

    @get:StringRes
    val stringRes
        get() = when (this) {
            ANIME -> R.string.anime
            MANGA -> R.string.manga
            RECOMMENDATIONS -> R.string.recommendations
        }

    companion object {
        val entriesLocalized = ExploreTab.entries.associateWith { it.stringRes }

        fun valueOf(index: Int) = ExploreTab.entries.find { it.ordinal == index }
    }
}