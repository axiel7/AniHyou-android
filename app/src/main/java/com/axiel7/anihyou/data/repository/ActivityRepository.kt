package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.axiel7.anihyou.ActivityDetailsQuery
import com.axiel7.anihyou.ActivityFeedQuery
import com.axiel7.anihyou.UpdateActivityReplyMutation
import com.axiel7.anihyou.UpdateTextActivityMutation
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryMutation
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import kotlinx.coroutines.flow.flow

object ActivityRepository {

    fun getActivityFeed(
        isFollowing: Boolean,
        type: ActivityTypeGrouped? = null,
        refreshCache: Boolean = false,
        page: Int,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = ActivityFeedQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            isFollowing = Optional.present(isFollowing),
            typeIn = Optional.presentIfNotNull(type?.value),
        ).tryQuery(
            fetchPolicy = if (refreshCache) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst
        )

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val activities = response?.data?.Page?.activities?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (activities != null) emit(
                PagedResult.Success(
                    data = activities,
                    nextPage = if (pageInfo?.hasNextPage == true)
                        pageInfo.currentPage?.plus(1)
                    else null
                )
            )
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getActivityDetails(activityId: Int) = flow {
        emit(DataResult.Loading)

        val response = ActivityDetailsQuery(
            activityId = Optional.present(activityId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val activity = response?.data?.Activity
            if (activity != null) emit(DataResult.Success(data = activity))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun updateTextActivity(
        id: Int? = null,
        text: String
    ) = flow {
        emit(DataResult.Loading)

        val response = UpdateTextActivityMutation(
            id = Optional.presentIfNotNull(id),
            text = Optional.present(text)
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val activity = response?.data?.SaveTextActivity
            if (activity != null) emit(DataResult.Success(data = true))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun updateActivityReply(
        activityId: Int,
        id: Int? = null,
        text: String
    ) = flow {
        emit(DataResult.Loading)

        val response = UpdateActivityReplyMutation(
            activityId = Optional.present(activityId),
            id = Optional.presentIfNotNull(id),
            text = Optional.present(text)
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val reply = response?.data?.SaveActivityReply
            if (reply != null) emit(DataResult.Success(data = true))
            else emit(DataResult.Error(message = "Error"))
        }
    }
}