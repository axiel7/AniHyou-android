package com.axiel7.anihyou.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.ActivityApi
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.data.paging.ActivityFeedPagingSourceFactory
import javax.inject.Inject

class ActivityRepository @Inject constructor(
    private val api: ActivityApi,
    private val activityFeedPagingSourceFactory: ActivityFeedPagingSourceFactory,
) {

    fun getActivityFeed(
        isFollowing: Boolean,
        type: ActivityTypeGrouped? = null,
        refreshCache: Boolean = false,
    ) = Pager(
        config = PagingConfig(pageSize = 25)
    ) {
        activityFeedPagingSourceFactory.create(isFollowing, type, refreshCache)
    }.flow

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