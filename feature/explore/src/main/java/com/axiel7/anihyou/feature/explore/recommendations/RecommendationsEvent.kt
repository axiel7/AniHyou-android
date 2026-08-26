package com.axiel7.anihyou.feature.explore.recommendations

import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.RecommendationRating
import com.axiel7.anihyou.core.network.type.RecommendationSort

interface RecommendationsEvent : UiEvent, PagedEvent {
    fun onVoteClick(recommendedMediaId: Int, baseMediaId: Int, recommendationId: Int,rating: RecommendationRating)
    fun refresh()
    fun onSortChange(value: RecommendationSort)
    fun onMyListChange(value: Boolean?)
    fun clearErrorId()
    fun selectItem(details: BasicMediaDetails?, listEntry: BasicMediaListEntry?)
    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?)
}