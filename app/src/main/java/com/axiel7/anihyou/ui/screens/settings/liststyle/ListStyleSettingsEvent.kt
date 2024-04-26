package com.axiel7.anihyou.ui.screens.settings.liststyle

import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.ui.common.ListStyle

interface ListStyleSettingsEvent {
    fun setAnimeListStyle(status: MediaListStatus, value: ListStyle)
    fun setMangaListStyle(status: MediaListStatus, value: ListStyle)
}