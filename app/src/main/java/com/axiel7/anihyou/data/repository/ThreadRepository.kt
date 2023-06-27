package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.ThreadCommentsQuery
import com.axiel7.anihyou.ThreadDetailsQuery
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import kotlinx.coroutines.flow.flow

object ThreadRepository {

    fun getThreadDetails(threadId: Int) = flow {
        emit(DataResult.Loading)

        val response = ThreadDetailsQuery(
            threadId = Optional.present(threadId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val thread = response?.data?.Thread
            if (thread != null) emit(DataResult.Success(data = thread))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getThreadCommentsPage(
        threadId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = ThreadCommentsQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            threadId = Optional.present(threadId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val comments = response?.data?.Page?.threadComments?.filterNotNull()
            val pageInfo = response?.data?.Page?.pageInfo
            if (comments != null) emit(PagedResult.Success(
                data = comments,
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Error"))
        }
    }
}