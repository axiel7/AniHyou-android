package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ActivityFeedQuery
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import kotlinx.coroutines.flow.flow

object ActivityRepository {

    fun getActivityFeed(
        page: Int,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = ActivityFeedQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

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
}