package com.axiel7.anihyou.ui.screens.home.discover

import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry

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
    fun selectItem(details: BasicMediaDetails?, listEntry: BasicMediaListEntry?)
}