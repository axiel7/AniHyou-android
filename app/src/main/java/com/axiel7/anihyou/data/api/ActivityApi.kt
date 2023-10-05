package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.axiel7.anihyou.ActivityDetailsQuery
import com.axiel7.anihyou.ActivityFeedQuery
import com.axiel7.anihyou.UpdateActivityReplyMutation
import com.axiel7.anihyou.UpdateTextActivityMutation
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import javax.inject.Inject

class ActivityApi @Inject constructor(
    private val client: ApolloClient
) {
    fun activityFeedQuery(
        isFollowing: Boolean,
        type: ActivityTypeGrouped?,
        refreshCache: Boolean,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            ActivityFeedQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                isFollowing = Optional.present(isFollowing),
                typeIn = Optional.presentIfNotNull(type?.value),
            )
        )
        .fetchPolicy(if (refreshCache) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)

    fun activityDetailsQuery(activityId: Int) = client
        .query(
            ActivityDetailsQuery(
                activityId = Optional.present(activityId)
            )
        )

    fun updateTextActivityMutation(
        id: Int?,
        text: String
    ) = client
        .mutation(
            UpdateTextActivityMutation(
                id = Optional.presentIfNotNull(id),
                text = Optional.present(text)
            )
        )

    fun updateActivityReplyMutation(
        activityId: Int,
        id: Int?,
        text: String
    ) = client
        .mutation(
            UpdateActivityReplyMutation(
                activityId = Optional.present(activityId),
                id = Optional.presentIfNotNull(id),
                text = Optional.present(text)
            )
        )
}