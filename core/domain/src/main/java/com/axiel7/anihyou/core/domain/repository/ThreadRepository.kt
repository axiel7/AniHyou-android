package com.axiel7.anihyou.core.domain.repository

import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.axiel7.anihyou.core.model.thread.ChildComment.Companion.toChildComment
import com.axiel7.anihyou.core.network.api.ThreadApi

class ThreadRepository(
    private val api: ThreadApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    fun getThreadDetails(threadId: Int) = api
        .threadDetailsQuery(threadId)
        .toFlow()
        .asDataResult {
            it.Thread
        }

    suspend fun subscribeToThread(threadId: Int, subscribe: Boolean) = api
        .subscribeToThread(threadId, subscribe)
        .execute()
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
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.threadComments?.filterNotNull().orEmpty().map { it.toChildComment() }
        }

    suspend fun updateThreadComment(
        threadId: Int?,
        parentCommentId: Int?,
        id: Int? = null,
        text: String
    ) = api
        .updateThreadCommentMutation(threadId, parentCommentId, id, text)
        .execute()
        .asDataResult {
            it.SaveThreadComment
        }
}