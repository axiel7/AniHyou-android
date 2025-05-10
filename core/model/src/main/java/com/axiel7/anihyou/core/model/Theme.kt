package com.axiel7.anihyou.core.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class Theme : Localizable {
    FOLLOW_SYSTEM,
    LIGHT,
    DARK;

    @Composable
    override fun localized() = stringResource(stringRes)

    val stringRes
        get() = when (this) {
            FOLLOW_SYSTEM -> R.string.theme_system
            LIGHT -> R.string.theme_light
            DARK -> R.string.theme_dark
        }

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }

        fun valueOfOrNull(value: String) = try {
            valueOf(value)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}