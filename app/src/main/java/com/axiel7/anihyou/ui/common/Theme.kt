package com.axiel7.anihyou.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class Theme : Localizable {
    FOLLOW_SYSTEM,
    LIGHT,
    DARK,
    BLACK;

    @Composable
    override fun localized() = stringResource(stringRes)

    val stringRes
        get() = when (this) {
            FOLLOW_SYSTEM -> R.string.theme_system
            LIGHT -> R.string.theme_light
            DARK -> R.string.theme_dark
            BLACK -> R.string.theme_black
        }

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }

        fun valueOfOrNull(value: String) = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}