package com.axiel7.anihyou.feature.explore.recommendations

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.MediaRecommendationsQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.RecommendationRating
import com.axiel7.anihyou.core.network.type.RecommendationSort

@Immutable
interface RecommendationsEvent : UiEvent, PagedEvent {
    fun onVoteClick(item: MediaRecommendationsQuery.Recommendation, rating: RecommendationRating)
    fun refresh()
    fun onSortChange(value: RecommendationSort)
    fun onMyListChange(value: Boolean)
    fun selectItem(details: BasicMediaDetails?, listEntry: BasicMediaListEntry?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}