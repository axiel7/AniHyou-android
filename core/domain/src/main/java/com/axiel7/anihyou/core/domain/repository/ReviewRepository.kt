package com.axiel7.anihyou.core.domain.repository

import com.apollographql.apollo.cache.normalized.watch
import com.axiel7.anihyou.core.network.api.ReviewApi
import com.axiel7.anihyou.core.network.type.ReviewRating

class ReviewRepository (
    private val api: ReviewApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    fun getReviewDetails(reviewId: Int) = api
        .reviewDetailsQuery(reviewId)
        .watch()
        .asDataResult {
            it.Review
        }

    fun rateReview(reviewId: Int, rating: ReviewRating) = api
        .rateReview(reviewId, rating)
        .toFlow()
        .asDataResult { it.RateReview }
}