package com.axiel7.anihyou.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class HomeTab(val index: Int) : Localizable {
    DISCOVER(0),
    ACTIVITY_FEED(1);

    @Composable
    override fun localized() = stringResource(stringRes)

    val stringRes
        get() = when (this) {
            DISCOVER -> R.string.discover
            ACTIVITY_FEED -> R.string.activity
        }

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }

        fun valueOf(index: Int) = entries.find { it.index == index }
    }
}