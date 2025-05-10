package com.axiel7.anihyou.core.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class HomeTab : Localizable {
    DISCOVER,
    CURRENT,
    ACTIVITY_FEED;

    @Composable
    override fun localized() = stringResource(stringRes)

    @get:StringRes
    val stringRes
        get() = when (this) {
            DISCOVER -> R.string.discover
            ACTIVITY_FEED -> R.string.activity
            CURRENT -> R.string.current
        }

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }

        fun valueOf(index: Int) = entries.find { it.ordinal == index }
    }
}