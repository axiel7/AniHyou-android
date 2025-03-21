package com.axiel7.anihyou.feature.home.discover

import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry

interface DiscoverEvent {
    fun addNextInfo()
    fun fetchAiringAnime()
    fun fetchAiringAnimeOnMyList()
    fun fetchThisSeasonAnime()
    fun fetchTrendingAnime()
    fun fetchNextSeasonAnime()
    fun fetchTrendingManga()
    fun fetchNewlyAnime()
    fun fetchNewlyManga()
    fun refresh()
    fun selectItem(details: BasicMediaDetails?, listEntry: BasicMediaListEntry?)
}