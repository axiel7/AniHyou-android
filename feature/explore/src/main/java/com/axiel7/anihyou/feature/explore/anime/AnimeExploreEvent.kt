package com.axiel7.anihyou.feature.explore.anime

import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry

interface AnimeExploreEvent : UiEvent {
    fun addNextInfo()
    fun fetchAiringAnime()
    fun fetchAiringAnimeOnMyList()
    fun fetchThisSeasonAnime()
    fun fetchTrendingAnime()
    fun fetchNextSeasonAnime()
    fun fetchNewlyAnime()
    fun refresh()
    fun selectItem(details: BasicMediaDetails?, listEntry: BasicMediaListEntry?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}