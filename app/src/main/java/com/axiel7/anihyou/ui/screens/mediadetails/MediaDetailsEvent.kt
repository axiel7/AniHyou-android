package com.axiel7.anihyou.ui.screens.mediadetails

import com.axiel7.anihyou.fragment.BasicMediaListEntry

interface MediaDetailsEvent {
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun toggleFavorite()
    fun fetchCharactersAndStaff()
    fun fetchRelationsAndRecommendations()
    fun fetchStats()
    fun fetchThreads()
    fun fetchReviews()
}