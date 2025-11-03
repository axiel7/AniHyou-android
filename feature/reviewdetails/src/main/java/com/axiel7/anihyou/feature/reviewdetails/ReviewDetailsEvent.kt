package com.axiel7.anihyou.feature.reviewdetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.network.type.ReviewRating

@Immutable
interface ReviewDetailsEvent {
    fun rateReview(rating: ReviewRating)
}