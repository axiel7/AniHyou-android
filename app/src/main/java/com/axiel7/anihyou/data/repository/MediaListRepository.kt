package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.axiel7.anihyou.UpdateEntryProgressMutation
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryMutation
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.flow.flow

object MediaListRepository {

    fun getUserMediaListPage(
        userId: Int?,
        mediaType: MediaType,
        status: MediaListStatus,
        sort: MediaListSort,
        refreshCache: Boolean = false,
        page: Int = 1,
        perPage: Int = 15,
    ) = flow {
        emit(UiState.Loading)

        val response = UserMediaListQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            userId = Optional.present(
                // if no user id specified, use the authenticated user id
                userId ?: LoginRepository.getUserId()
            ),
            type = Optional.present(mediaType),
            status = Optional.present(status),
            sort = Optional.present(listOf(sort, MediaListSort.MEDIA_ID_DESC))
        ).tryQuery(
            fetchPolicy = if (refreshCache) FetchPolicy.NetworkFirst else FetchPolicy.CacheFirst
        )

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val mediaPage = response?.data?.Page
            if (mediaPage != null) emit(UiState.Success(mediaPage))
            else emit(UiState.Error(message = "Empty"))
        }
    }

    fun updateEntryProgress(
        entryId: Int,
        progress: Int
    ) = flow {
        emit(UiState.Loading)

        val response = UpdateEntryProgressMutation(
            saveMediaListEntryId = Optional.present(entryId),
            progress = Optional.present(progress)
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(UiState.Error(message = error))
        else {
            val entry = response?.data?.SaveMediaListEntry
            if (entry != null) emit(UiState.Success(entry))
            else emit(UiState.Error(message = "Error"))
        }
    }
}