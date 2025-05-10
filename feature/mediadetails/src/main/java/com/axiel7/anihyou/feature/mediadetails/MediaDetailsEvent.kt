package com.axiel7.anihyou.feature.mediadetails

import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.MediaCharacter

interface MediaDetailsEvent {
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun toggleFavorite()
    fun fetchCharactersAndStaff()
    fun fetchRelationsAndRecommendations()
    fun fetchStats()
    fun fetchThreads()
    fun fetchReviews()
    fun fetchActivity()
    fun showVoiceActorsSheet(character: MediaCharacter)
    fun hideVoiceActorSheet()
}