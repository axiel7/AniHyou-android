package com.axiel7.anihyou.ui.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class Theme(val value: String) : Localizable {
    FOLLOW_SYSTEM("follow_system"),
    LIGHT("light"),
    DARK("dark"),
    BLACK("black");

    @Composable
    override fun localized() = stringResource(stringRes)

    val stringRes get() = when (this) {
        FOLLOW_SYSTEM -> R.string.theme_system
        LIGHT -> R.string.theme_light
        DARK -> R.string.theme_dark
        BLACK -> R.string.theme_black
    }
}