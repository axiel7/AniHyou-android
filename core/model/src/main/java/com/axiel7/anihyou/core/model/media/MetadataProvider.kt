package com.axiel7.anihyou.core.model.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.resources.R

enum class MetadataProvider {
    TMDB,
    ALIST;

    @Composable
    fun localized() = stringResource(stringRes)

    val stringRes
        get() = when (this) {
            TMDB -> R.string.tmdb
            ALIST -> R.string.anilist
        }

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }
    }
}
