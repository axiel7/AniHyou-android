package com.axiel7.anihyou.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class AppColorMode : Localizable {
    DEFAULT,
    PROFILE,
    CUSTOM;

    @Composable
    override fun localized() = stringResource(stringRes)

    val stringRes
        get() = when (this) {
            DEFAULT -> R.string.default_setting
            PROFILE -> R.string.profile
            CUSTOM -> R.string.custom_color
        }

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }
    }
}