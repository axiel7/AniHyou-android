package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.ReviewApi
import com.axiel7.anihyou.data.model.asDataResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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