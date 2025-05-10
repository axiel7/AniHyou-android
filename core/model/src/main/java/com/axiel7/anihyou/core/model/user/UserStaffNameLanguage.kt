package com.axiel7.anihyou.core.model.user

import com.axiel7.anihyou.core.network.type.UserStaffNameLanguage
import com.axiel7.anihyou.core.resources.R

fun UserStaffNameLanguage.stringRes() = when (this) {
    UserStaffNameLanguage.ROMAJI -> R.string.romaji
    UserStaffNameLanguage.ROMAJI_WESTERN -> R.string.romaji_western_order
    UserStaffNameLanguage.NATIVE -> R.string.native_title
    UserStaffNameLanguage.UNKNOWN__ -> R.string.unknown
}

val UserStaffNameLanguage.Companion.entriesLocalized
    get() = knownEntries.associateWith { it.stringRes() }