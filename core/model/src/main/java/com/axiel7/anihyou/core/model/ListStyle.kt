package com.axiel7.anihyou.core.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class ListStyle : Localizable {
    STANDARD, COMPACT, MINIMAL, GRID;

    @get:StringRes
    val stringRes: Int
        get() = when (this) {
            STANDARD -> R.string.standard
            COMPACT -> R.string.compact
            MINIMAL -> R.string.minimal
            GRID -> R.string.grid
        }

    @Composable
    override fun localized() = stringResource(stringRes)

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }
    }
}