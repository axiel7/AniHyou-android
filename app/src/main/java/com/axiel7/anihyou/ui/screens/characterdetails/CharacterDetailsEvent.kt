package com.axiel7.anihyou.ui.screens.characterdetails

import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.event.PagedEvent

interface CharacterDetailsEvent : PagedEvent {
    fun toggleFavorite()
    fun selectMediaItem(value: CharacterMediaQuery.Edge?)
    fun onShowVoiceActorsSheet(item: CharacterMediaQuery.Edge)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}