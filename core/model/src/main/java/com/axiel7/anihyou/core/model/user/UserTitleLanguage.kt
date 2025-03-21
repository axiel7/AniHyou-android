package com.axiel7.anihyou.core.model.user

import com.axiel7.anihyou.core.network.type.UserTitleLanguage
import com.axiel7.anihyou.core.resources.R

fun UserTitleLanguage.Companion.preferenceValues() = arrayOf(
    UserTitleLanguage.ROMAJI,
    UserTitleLanguage.ENGLISH,
    UserTitleLanguage.NATIVE
)

fun UserTitleLanguage.stringRes() = when (this) {
    UserTitleLanguage.ROMAJI, UserTitleLanguage.ROMAJI_STYLISED -> R.string.romaji
    UserTitleLanguage.ENGLISH, UserTitleLanguage.ENGLISH_STYLISED -> R.string.english
    UserTitleLanguage.NATIVE, UserTitleLanguage.NATIVE_STYLISED -> R.string.native_title
    UserTitleLanguage.UNKNOWN__ -> R.string.unknown
}

val UserTitleLanguage.Companion.entriesLocalized
    get() = preferenceValues().associateWith { it.stringRes() }