package com.axiel7.anihyou.core.domain.repository

import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.fetchPolicy
import com.axiel7.anihyou.core.network.ActivityDetailsQuery
import com.axiel7.anihyou.core.network.api.ActivityApi
import com.axiel7.anihyou.core.network.type.ActivityType

class ActivityRepository(
    private val api: ActivityApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    fun getActivityFeed(
        isFollowing: Boolean,
        typeIn: List<ActivityType>,
        fetchFromNetwork: Boolean = false,
        page: Int,
        perPage: Int = 25
    ) = api
        .activityFeedQuery(
            isFollowing = isFollowing,
            typeIn = typeIn,
            fetchFromNetwork = fetchFromNetwork,
            page = page,
            perPage = perPage
        )
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.activities?.filterNotNull().orEmpty()
        }

    fun getActivityDetails(
        activityId: Int,
        fetchFromNetwork: Boolean = false
    ) = api
        .activityDetailsQuery(activityId)
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)
        .toFlow()
        .asDataResult {
            it.Activity
        }

    suspend fun updateActivityDetailsCache(
        id: Int,
        activity: ActivityDetailsQuery.Activity,
    ) = api.updateActivityDetailsCache(id, activity)

    suspend fun updateTextActivity(
        id: Int? = null,
        text: String
    ) = api
        .updateTextActivityMutation(id, text)
        .execute()
        .asDataResult {
            it.SaveTextActivity?.onTextActivity
        }

    suspend fun updateActivityReply(
        activityId: Int,
        id: Int? = null,
        text: String
    ) = api
        .updateActivityReplyMutation(activityId, id, text)
        .execute()
        .asDataResult {
            it.SaveActivityReply?.activityReplyFragment
        }

    suspend fun deleteActivity(id: Int) = api
        .deleteActivityMutation(id)
        .execute()
        .asDataResult { it.DeleteActivity?.deleted }
}