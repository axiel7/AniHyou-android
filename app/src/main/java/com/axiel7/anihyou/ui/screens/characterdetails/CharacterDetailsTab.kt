package com.axiel7.anihyou.ui.screens.characterdetails

import androidx.annotation.DrawableRes
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.common.TabRowItem

enum class CharacterDetailsTab {
    INFO,
    MEDIA;

    @get:DrawableRes
    val icon
        get() = when (this) {
            INFO -> R.drawable.info_24
            MEDIA -> R.drawable.movie_24
        }

    companion object {
        val tabRows = entries.map { TabRowItem(value = it, icon = it.icon) }.toTypedArray()
    }
}