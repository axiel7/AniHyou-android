package com.axiel7.anihyou.feature.reviewdetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.type.ReviewRating

@Immutable
interface ReviewDetailsEvent : UiEvent {
    fun rateReview(rating: ReviewRating)
}