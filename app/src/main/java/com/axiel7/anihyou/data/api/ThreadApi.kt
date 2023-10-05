package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ChildCommentsQuery
import com.axiel7.anihyou.ThreadDetailsQuery
import com.axiel7.anihyou.UpdateThreadCommentMutation
import javax.inject.Inject

class ThreadApi @Inject constructor(
    private val client: ApolloClient
) {
    fun threadDetailsQuery(threadId: Int) = client
        .query(
            ThreadDetailsQuery(
                threadId = Optional.present(threadId)
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