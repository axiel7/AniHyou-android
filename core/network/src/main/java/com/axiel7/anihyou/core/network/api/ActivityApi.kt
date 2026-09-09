package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.cache.normalized.FetchPolicy
import com.apollographql.cache.normalized.api.CacheKey
import com.apollographql.cache.normalized.apolloStore
import com.apollographql.cache.normalized.fetchPolicy
import com.axiel7.anihyou.core.network.ActivityDetailsQuery
import com.axiel7.anihyou.core.network.ActivityFeedQuery
import com.axiel7.anihyou.core.network.DeleteActivityMutation
import com.axiel7.anihyou.core.network.UpdateActivityReplyMutation
import com.axiel7.anihyou.core.network.UpdateTextActivityMutation
import com.axiel7.anihyou.core.network.fragment.ListActivityFragment
import com.axiel7.anihyou.core.network.fragment.ListActivityFragmentImpl
import com.axiel7.anihyou.core.network.fragment.MessageActivityFragment
import com.axiel7.anihyou.core.network.fragment.MessageActivityFragmentImpl
import com.axiel7.anihyou.core.network.fragment.TextActivityFragment
import com.axiel7.anihyou.core.network.fragment.TextActivityFragmentImpl
import com.axiel7.anihyou.core.network.type.ActivityType

class ActivityApi(
    private val client: ApolloClient
) {
    fun activityFeedQuery(
        isFollowing: Boolean,
        typeIn: List<ActivityType>?,
        userIdIn: List<Int>?,
        fetchFromNetwork: Boolean,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            ActivityFeedQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                isFollowing = Optional.present(isFollowing),
                typeIn = Optional.presentIfNotNull(typeIn),
                userIdIn = Optional.presentIfNotNull(userIdIn),
            )
        )
        .fetchPolicy(if (fetchFromNetwork) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst)

    fun activityDetailsQuery(activityId: Int) = client
        .query(
            ActivityDetailsQuery(
                activityId = Optional.present(activityId)
            )
        )

    suspend fun updateActivityDetailsCache(
        id: Int,
        activity: ActivityDetailsQuery.Activity,
    ) {
        client.apolloStore
            .writeOperation(
                operation = ActivityDetailsQuery(
                    activityId = Optional.present(id)
                ),
                data = ActivityDetailsQuery.Data(
                    Activity = activity
                ),
                publish = true
            )
    }

    suspend fun updateActivityDetailsCache(
        listActivity: ListActivityFragment? = null,
        textActivity: TextActivityFragment? = null,
        messageActivity: MessageActivityFragment? = null
    ) {
        val id = listActivity?.id ?: textActivity?.id ?: messageActivity?.id ?: return
        val operation = client.apolloStore
            .readOperation(
                operation = ActivityDetailsQuery(
                    activityId = Optional.present(id)
                ),
            )
        operation.data?.Activity?.let { activity ->
            updateActivityDetailsCache(
                id = id,
                activity = activity.copy(
                    onListActivity = listActivity?.let {
                        activity.onListActivity?.copy(listActivityFragment = listActivity)
                    } ?: activity.onListActivity,
                    onTextActivity = textActivity?.let {
                        activity.onTextActivity?.copy(textActivityFragment = textActivity)
                    } ?: activity.onTextActivity,
                    onMessageActivity = messageActivity?.let {
                        activity.onMessageActivity?.copy(messageActivityFragment = messageActivity)
                    } ?: activity.onMessageActivity,
                )
            )
        }
    }

    suspend fun updateListActivityFragment(data: ListActivityFragment) =
        client.apolloStore
            .writeFragment(
                fragment = ListActivityFragmentImpl(),
                cacheKey = CacheKey("${data.__typename}:${data.id}"),
                data = data,
                publish = true,
            )

    suspend fun updateTextActivityFragment(data: TextActivityFragment) =
        client.apolloStore
            .writeFragment(
                fragment = TextActivityFragmentImpl(),
                cacheKey = CacheKey("${data.__typename}:${data.id}"),
                data = data,
                publish = true,
            )

    suspend fun updateMessageActivityFragment(data: MessageActivityFragment) =
        client.apolloStore
            .writeFragment(
                fragment = MessageActivityFragmentImpl(),
                cacheKey = CacheKey("${data.__typename}:${data.id}"),
                data = data,
                publish = true,
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

    fun deleteActivityMutation(id: Int) = client
        .mutation(DeleteActivityMutation(id = Optional.present(id)))
}