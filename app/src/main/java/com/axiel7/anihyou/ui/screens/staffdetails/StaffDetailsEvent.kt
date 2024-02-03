package com.axiel7.anihyou.ui.screens.staffdetails

import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.fragment.BasicMediaListEntry

interface StaffDetailsEvent {
    fun setMediaOnMyList(value: Boolean?)
    fun toggleFavorite()
    fun loadNextPageMedia()
    fun selectMediaItem(value: Pair<Int, StaffMediaGrouped>?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun loadNextPageCharacters()
    fun setCharactersOnMyList(value: Boolean?)
}