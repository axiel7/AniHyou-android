package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
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
            typeIn = if (type == ActivityTypeGrouped.ALL) null else type.value,
            fetchFromNetwork = fetchFromNetwork,
            page = page,
            perPage = perPage
        )
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.activities?.filterNotNull().orEmpty()
        }

    fun getActivityDetails(activityId: Int) = api
        .activityDetailsQuery(activityId)
        .watch()
        .asDataResult {
            it.Activity
        }

    fun updateTextActivity(
        id: Int? = null,
        text: String
    ) = api
        .updateTextActivityMutation(id, text)
        .toFlow()
        .asDataResult {
            it.SaveTextActivity
        }

    fun updateActivityReply(
        activityId: Int,
        id: Int? = null,
        text: String
    ) = api
        .updateActivityReplyMutation(activityId, id, text)
        .toFlow()
        .asDataResult {
            it.SaveActivityReply
        }
}