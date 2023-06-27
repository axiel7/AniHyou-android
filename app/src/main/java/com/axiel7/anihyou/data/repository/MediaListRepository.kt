package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.axiel7.anihyou.DeleteMediaListMutation
import com.axiel7.anihyou.UpdateEntryMutation
import com.axiel7.anihyou.UpdateEntryProgressMutation
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryMutation
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.FuzzyDate
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDateInput
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
        emit(PagedResult.Loading)

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
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val mediaPage = response?.data?.Page
            if (mediaPage != null) emit(PagedResult.Success(
                data = mediaPage.mediaList?.filterNotNull().orEmpty(),
                nextPage = if (mediaPage.pageInfo?.hasNextPage == true)
                    mediaPage.pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Empty"))
        }
    }

    fun updateEntryProgress(
        entryId: Int,
        progress: Int
    ) = flow {
        emit(DataResult.Loading)

        val response = UpdateEntryProgressMutation(
            saveMediaListEntryId = Optional.present(entryId),
            progress = Optional.present(progress)
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val entry = response?.data?.SaveMediaListEntry
            if (entry != null) emit(DataResult.Success(entry))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun updateEntry(
        oldEntry: BasicMediaListEntry?,
        mediaId: Int,
        status: MediaListStatus?,
        score: Double?,
        progress: Int?,
        progressVolumes: Int?,
        startedAt: FuzzyDate?,
        completedAt: FuzzyDate?,
        repeat: Int?,
        private: Boolean?,
        notes: String?,
    ) = flow {
        emit(DataResult.Loading)

        val response = UpdateEntryMutation(
            mediaId = Optional.present(mediaId),
            status = if (status != oldEntry?.status) Optional.present(status)
            else Optional.absent(),
            score = if (score != oldEntry?.score) Optional.present(score)
            else Optional.absent(),
            progress = if (progress != oldEntry?.progress) Optional.present(progress)
            else Optional.absent(),
            progressVolumes = if (progressVolumes != oldEntry?.progressVolumes) Optional.present(
                progressVolumes
            )
            else Optional.absent(),
            startedAt = if (startedAt != oldEntry?.startedAt?.fuzzyDate) Optional.present(
                startedAt?.toFuzzyDateInput()
            )
            else Optional.absent(),
            completedAt = if (completedAt != oldEntry?.completedAt?.fuzzyDate) Optional.present(
                completedAt?.toFuzzyDateInput()
            )
            else Optional.absent(),
            repeat = if (repeat != oldEntry?.repeat) Optional.present(repeat)
            else Optional.absent(),
            private = if (private != oldEntry?.private) Optional.present(private)
            else Optional.absent(),
            notes = if (notes != oldEntry?.notes) Optional.present(notes)
            else Optional.absent()
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val entry = response?.data?.SaveMediaListEntry?.basicMediaListEntry
            if (entry != null) emit(DataResult.Success(entry))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun deleteEntry(id: Int) = flow {
        emit(DataResult.Loading)

        val response = DeleteMediaListMutation(
            mediaListEntryId = Optional.present(id)
        ).tryMutation()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val entry = response?.data?.DeleteMediaListEntry
            if (entry != null) emit(DataResult.Success(true))
            else emit(DataResult.Error(message = "Error"))
        }
    }
}