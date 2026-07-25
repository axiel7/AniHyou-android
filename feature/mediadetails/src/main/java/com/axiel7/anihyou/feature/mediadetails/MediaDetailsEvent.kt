package com.axiel7.anihyou.feature.mediadetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.MediaCharacter
import com.axiel7.anihyou.core.network.type.RecommendationRating

@Immutable
interface MediaDetailsEvent : UiEvent {
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
    fun onVoteClick(recommendedMediaId: Int, recommendationId: Int, rating: RecommendationRating)
}