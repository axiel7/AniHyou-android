package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.ActivityApi
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import javax.inject.Inject

class ActivityRepository @Inject constructor(
    private val api: ActivityApi
) {

    fun getActivityFeed(
        isFollowing: Boolean,
        type: ActivityTypeGrouped? = null,
        refreshCache: Boolean = false,
        page: Int,
        perPage: Int = 25,
    ) = api
        .activityFeedQuery(isFollowing, type, refreshCache, page, perPage)
        .watch()
        .asDataResult {
            PageResult(
                list = it.Page?.activities?.filterNotNull().orEmpty(),
                nextPage = if (it.Page?.pageInfo?.hasNextPage == true)
                    it.Page.pageInfo.currentPage?.plus(1)
                else null
            )
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