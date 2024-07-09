package com.axiel7.anihyou.data.repository

import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.apollographql.apollo.cache.normalized.watch
import com.axiel7.anihyou.ActivityDetailsQuery
import com.axiel7.anihyou.data.api.ActivityApi
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.data.model.asDataResult
import com.axiel7.anihyou.data.model.asPagedResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepository @Inject constructor(
    private val api: ActivityApi,
) {

    fun getActivityFeed(
        isFollowing: Boolean,
        type: ActivityTypeGrouped,
        fetchFromNetwork: Boolean = false,
        page: Int,
        perPage: Int = 25
    ) = api
        .activityFeedQuery(
            isFollowing = isFollowing,
            typeIn = type.value,
            fetchFromNetwork = fetchFromNetwork,
            page = page,
            perPage = perPage
        )
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.activities?.filterNotNull().orEmpty()
        }

    fun getActivityDetails(
        activityId: Int,
        fetchFromNetwork: Boolean = false
    ) = api
        .activityDetailsQuery(activityId)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .watch()
        .asDataResult {
            it.Activity
        }

    suspend fun updateActivityDetailsCache(
        id: Int,
        activity: ActivityDetailsQuery.Activity,
    ) = api.updateActivityDetailsCache(id, activity)

    fun updateTextActivity(
        id: Int? = null,
        text: String
    ) = api
        .updateTextActivityMutation(id, text)
        .toFlow()
        .asDataResult {
            it.SaveTextActivity?.onTextActivity
        }

    fun updateActivityReply(
        activityId: Int,
        id: Int? = null,
        text: String
    ) = api
        .updateActivityReplyMutation(activityId, id, text)
        .toFlow()
        .asDataResult {
            it.SaveActivityReply?.activityReplyFragment
        }
}