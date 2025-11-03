package com.axiel7.anihyou.feature.staffdetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry

@Immutable
interface StaffDetailsEvent {
    fun setMediaOnMyList(value: Boolean?)
    fun toggleFavorite()
    fun loadNextPageMedia()
    fun selectMediaItem(value: Pair<Int, StaffMediaGrouped>?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun loadNextPageCharacters()
    fun setCharactersOnMyList(value: Boolean?)
}