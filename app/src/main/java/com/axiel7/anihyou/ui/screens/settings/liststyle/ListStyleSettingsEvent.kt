package com.axiel7.anihyou.ui.screens.settings.liststyle

import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.ui.common.ListStyle
import kotlinx.coroutines.flow.StateFlow

interface ListStyleSettingsEvent {
    fun getAnimeListStyle(status: MediaListStatus): StateFlow<ListStyle?>
    fun setAnimeListStyle(status: MediaListStatus, value: ListStyle)
    fun getMangaListStyle(status: MediaListStatus): StateFlow<ListStyle?>
    fun setMangaListStyle(status: MediaListStatus, value: ListStyle)
}