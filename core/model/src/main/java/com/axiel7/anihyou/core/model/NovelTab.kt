package com.axiel7.anihyou.core.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R


enum class NovelTab: Localizable {
    MANGA,
    NOVEL;

    @Composable
    override fun localized() = stringResource(stringRes)

    @get:StringRes
    val stringRes
        get() = when (this) {
            MANGA -> R.string.manga
            NOVEL -> R.string.novels
        }

    companion object {
        val entriesLocalized = NovelTab.entries.associateWith { it.stringRes }

        fun valueOf(index: Int) = NovelTab.entries.find { it.ordinal == index }
    }
}