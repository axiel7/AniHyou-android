package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.ReviewApi
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val api: ReviewApi
) {

    fun getReviewDetails(reviewId: Int) = api
        .reviewDetailsQuery(reviewId)
        .watch()
        .asDataResult {
            it.Review
        }
}