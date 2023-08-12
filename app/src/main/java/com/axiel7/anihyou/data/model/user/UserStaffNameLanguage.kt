package com.axiel7.anihyou.data.model.user

import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.UserStaffNameLanguage

fun UserStaffNameLanguage.stringRes() = when (this) {
    UserStaffNameLanguage.ROMAJI -> R.string.romaji
    UserStaffNameLanguage.ROMAJI_WESTERN -> R.string.romaji_western_order
    UserStaffNameLanguage.NATIVE -> R.string.native_title
    UserStaffNameLanguage.UNKNOWN__ -> R.string.unknown
}