package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.ThreadApi
import com.axiel7.anihyou.data.model.asDataResult
import com.axiel7.anihyou.data.model.asPagedResult
import com.axiel7.anihyou.data.model.thread.ChildComment.Companion.toChildComment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreadRepository @Inject constructor(
    private val api: ThreadApi,
) {

    fun getThreadDetails(threadId: Int) = api
        .threadDetailsQuery(threadId)
        .watch()
        .asDataResult {
            it.Thread
        }

    fun subscribeToThread(threadId: Int, subscribe: Boolean) = api
        .subscribeToThread(threadId, subscribe)
        .toFlow()
        .asDataResult {
            it.ToggleThreadSubscription?.isSubscribed
        }

    fun getThreadCommentsPage(
        threadId: Int,
        fetchFromNetwork: Boolean = false,
        page: Int,
        perPage: Int = 25,
    ) = api
        .childCommentsQuery(threadId, page, perPage)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.threadComments?.filterNotNull().orEmpty().map { it.toChildComment() }
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