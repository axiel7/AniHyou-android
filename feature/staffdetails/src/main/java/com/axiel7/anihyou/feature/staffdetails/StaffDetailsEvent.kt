package com.axiel7.anihyou.feature.staffdetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaType

@Immutable
interface StaffDetailsEvent : UiEvent {
    fun setMediaOnMyList(value: Boolean?)
    fun setMediaType(value: MediaType?)
    fun toggleFavorite()
    fun loadNextPageMedia()
    fun selectMediaItem(value: Pair<Int, StaffMediaGrouped>?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun loadNextPageCharacters()
    fun setCharactersOnMyList(value: Boolean?)
}