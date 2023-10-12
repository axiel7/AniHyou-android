package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ReviewDetailsQuery
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
}