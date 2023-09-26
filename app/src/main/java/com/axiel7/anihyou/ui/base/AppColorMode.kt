package com.axiel7.anihyou.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class AppColorMode : Localizable {
    DEFAULT,
    PROFILE;
    //TODO: CUSTOM,

    @Composable
    override fun localized() = stringResource(stringRes)

    val stringRes
        get() = when (this) {
            DEFAULT -> R.string.default_setting
            PROFILE -> R.string.profile
        }
}