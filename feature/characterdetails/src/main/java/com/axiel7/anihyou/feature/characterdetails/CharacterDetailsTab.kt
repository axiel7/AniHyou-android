package com.axiel7.anihyou.feature.characterdetails

import androidx.annotation.DrawableRes
import com.axiel7.anihyou.core.ui.common.TabRowItem
import com.axiel7.anihyou.core.resources.R

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