package com.axiel7.anihyou.core.domain.repository

import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.apollographql.apollo.cache.normalized.watch
import com.axiel7.anihyou.core.network.ActivityDetailsQuery
import com.axiel7.anihyou.core.network.api.ActivityApi
import com.axiel7.anihyou.core.network.type.ActivityType
import kotlin.collections.orEmpty

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

    fun deleteActivity(id: Int) = api
        .deleteActivityMutation(id)
        .toFlow()
        .asDataResult { it.DeleteActivity?.deleted }
}