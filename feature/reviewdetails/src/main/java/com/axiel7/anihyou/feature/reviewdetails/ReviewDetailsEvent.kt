package com.axiel7.anihyou.feature.reviewdetails

import com.axiel7.anihyou.core.network.type.ReviewRating

interface ReviewDetailsEvent {
    fun rateReview(rating: ReviewRating)
}