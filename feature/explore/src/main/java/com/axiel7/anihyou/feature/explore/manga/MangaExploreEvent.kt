package com.axiel7.anihyou.feature.explore.manga

import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry

interface MangaExploreEvent : UiEvent {
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun addNextInfo()
    fun fetchTrendingManga()
    fun fetchNewlyManga()
    fun refresh()
    fun selectItem(details: BasicMediaDetails?, listEntry: BasicMediaListEntry?)
}