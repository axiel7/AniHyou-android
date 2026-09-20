package com.axiel7.anihyou.feature.addrecommendation

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent

import com.axiel7.anihyou.core.network.SearchMediaQuery

@Immutable
interface AddRecommendationEvent : UiEvent {
    fun saveRecommendation(mediaId: Int, mediaRecommendationId: Int)
    fun insertRecommendation(media: SearchMediaQuery.Medium)
}