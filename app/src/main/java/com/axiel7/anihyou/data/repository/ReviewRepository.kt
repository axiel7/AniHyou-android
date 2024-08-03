package com.axiel7.anihyou.data.repository

import com.apollographql.apollo.cache.normalized.watch
import com.axiel7.anihyou.data.api.ReviewApi
import com.axiel7.anihyou.type.ReviewRating
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
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