package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.axiel7.anihyou.core.network.RateReviewMutation
import com.axiel7.anihyou.core.network.ReviewDetailsQuery
import com.axiel7.anihyou.core.network.type.ReviewRating

class ReviewApi (
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