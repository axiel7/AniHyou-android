package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ToggleLikeMutation
import com.axiel7.anihyou.type.LikeableType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LikeApi @Inject constructor(
    private val client: ApolloClient
) {
    fun toggleLikeMutation(
        likeableId: Int,
        type: LikeableType
    ) = client
        .mutation(
            ToggleLikeMutation(
                likeableId = Optional.present(likeableId),
                type = Optional.present(type)
            )
        )
}