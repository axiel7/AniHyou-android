package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.axiel7.anihyou.core.network.ChildCommentsQuery
import com.axiel7.anihyou.core.network.SubscribeThreadMutation
import com.axiel7.anihyou.core.network.ThreadDetailsQuery
import com.axiel7.anihyou.core.network.UpdateThreadCommentMutation

class ThreadApi (
    private val client: ApolloClient
) {
    fun threadDetailsQuery(threadId: Int) = client
        .query(
            ThreadDetailsQuery(
                threadId = Optional.present(threadId)
            )
        )

    fun subscribeToThread(threadId: Int, subscribe: Boolean) = client
        .mutation(
            SubscribeThreadMutation(
                threadId = Optional.present(threadId),
                subscribe = Optional.present(subscribe)
            )
        )

    fun childCommentsQuery(
        threadId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            ChildCommentsQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                threadId = Optional.present(threadId)
            )
        )

    fun updateThreadCommentMutation(
        threadId: Int?,
        parentCommentId: Int?,
        id: Int?,
        text: String
    ) = client
        .mutation(
            UpdateThreadCommentMutation(
                threadId = Optional.presentIfNotNull(threadId),
                parentCommentId = Optional.presentIfNotNull(parentCommentId),
                id = Optional.presentIfNotNull(id),
                text = Optional.present(text)
            )
        )
}