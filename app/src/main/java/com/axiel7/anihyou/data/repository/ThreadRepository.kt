package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.ThreadApi
import javax.inject.Inject

class ThreadRepository @Inject constructor(
    private val api: ThreadApi
) {

    fun getThreadDetails(threadId: Int) = api
        .threadDetailsQuery(threadId)
        .watch()
        .asDataResult {
            it.Thread
        }

    fun getThreadCommentsPage(
        threadId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) {
        TODO("use paging")
    }

    fun updateThreadComment(
        threadId: Int?,
        parentCommentId: Int?,
        id: Int? = null,
        text: String
    ) = api
        .updateThreadCommentMutation(threadId, parentCommentId, id, text)
        .toFlow()
        .asDataResult {
            it.SaveThreadComment
        }
}