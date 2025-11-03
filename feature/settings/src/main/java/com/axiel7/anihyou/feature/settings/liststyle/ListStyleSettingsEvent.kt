package com.axiel7.anihyou.feature.settings.liststyle

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.network.type.MediaListStatus

@Immutable
interface ListStyleSettingsEvent {
    fun setAnimeListStyle(status: MediaListStatus, value: ListStyle)
    fun setMangaListStyle(status: MediaListStatus, value: ListStyle)
}