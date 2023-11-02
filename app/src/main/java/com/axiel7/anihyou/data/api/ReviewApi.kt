package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.RateReviewMutation
import com.axiel7.anihyou.ReviewDetailsQuery
import com.axiel7.anihyou.type.ReviewRating
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewApi @Inject constructor(
    private val client: ApolloClient
) {
    fun reviewDetailsQuery(reviewId: Int) = client
        .query(
            ReviewDetailsQuery(
                reviewId = Optional.present(reviewId)
            )
        )

    fun rateReview(reviewId: Int, rating: ReviewRating) = client
        .mutation(
            RateReviewMutation(
                reviewId = Optional.present(reviewId),
                rating = Optional.present(rating)
            )
        )
}