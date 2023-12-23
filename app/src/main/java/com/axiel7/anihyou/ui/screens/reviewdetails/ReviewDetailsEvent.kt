package com.axiel7.anihyou.ui.screens.reviewdetails

import com.axiel7.anihyou.type.ReviewRating

interface ReviewDetailsEvent {
    fun rateReview(rating: ReviewRating)
}