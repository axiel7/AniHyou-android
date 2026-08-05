package com.axiel7.anihyou.core.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class TranslatorApp : Localizable {
    DEFAULT,
    GOOGLE,
    DEEPL,
    TRANSLATE_YOU;

    @get:StringRes
    val stringRes: Int
        get() = when (this) {
            DEFAULT -> R.string.default_setting
            GOOGLE -> R.string.google_translate
            DEEPL -> R.string.deepl
            TRANSLATE_YOU -> R.string.translate_you
        }

    @Composable
    override fun localized() = stringResource(stringRes)

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }
    }
}