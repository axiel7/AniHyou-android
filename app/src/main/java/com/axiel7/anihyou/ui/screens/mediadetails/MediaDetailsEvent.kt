package com.axiel7.anihyou.ui.screens.mediadetails

import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.MediaCharacter

interface MediaDetailsEvent {
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
    fun toggleFavorite()
    fun fetchCharactersAndStaff()
    fun fetchRelationsAndRecommendations()
    fun fetchStats()
    fun fetchThreads()
    fun fetchReviews()
    fun showVoiceActorsSheet(character: MediaCharacter)
    fun hideVoiceActorSheet()
}