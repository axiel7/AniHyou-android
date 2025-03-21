package com.axiel7.anihyou.feature.characterdetails

import com.axiel7.anihyou.core.network.CharacterMediaQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.ui.common.event.PagedEvent

interface CharacterDetailsEvent : PagedEvent {
    fun toggleFavorite()
    fun selectMediaItem(value: CharacterMediaQuery.Edge?)
    fun onShowVoiceActorsSheet(item: CharacterMediaQuery.Edge)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}