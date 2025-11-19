package com.axiel7.anihyou.feature.characterdetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.network.CharacterMediaQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface CharacterDetailsEvent : UiEvent, PagedEvent {
    fun toggleFavorite()
    fun selectMediaItem(value: CharacterMediaQuery.Edge?)
    fun onShowVoiceActorsSheet(item: CharacterMediaQuery.Edge)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}