package com.axiel7.anihyou.data.repository

import com.axiel7.anihyou.data.api.MediaListApi
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.FuzzyDate
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDateInput
import javax.inject.Inject

class MediaListRepository @Inject constructor(
    private val api: MediaListApi
) {

    suspend fun getUserMediaListPage(
        userId: Int,
        mediaType: MediaType,
        status: MediaListStatus,
        sort: MediaListSort,
        refreshCache: Boolean = false,
        page: Int = 1,
        perPage: Int = 15,
    ) = api
        .userMediaListQuery(
            userId = userId,
            mediaType = mediaType,
            status = status,
            sort = listOf(sort, MediaListSort.MEDIA_ID_DESC),
            refreshCache = refreshCache,
            page = page,
            perPage = perPage,
        )
        .execute()

    fun updateEntryProgress(
        entryId: Int,
        progress: Int
    ) = api
        .updateEntryProgressMutation(entryId, progress)
        .toFlow()
        .asDataResult {
            it.SaveMediaListEntry
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
    ) = api
        .updateEntryMutation(
            mediaId = mediaId,
            status = if (status != oldEntry?.status) status else null,
            score = if (score != oldEntry?.score) score else null,
            progress = if (progress != oldEntry?.progress) progress else null,
            progressVolumes = if (progressVolumes != oldEntry?.progressVolumes) progressVolumes
            else null,
            startedAt = if (startedAt != oldEntry?.startedAt?.fuzzyDate)
                startedAt?.toFuzzyDateInput() else null,
            completedAt = if (completedAt != oldEntry?.completedAt?.fuzzyDate)
                completedAt?.toFuzzyDateInput() else null,
            repeat = if (repeat != oldEntry?.repeat) repeat else null,
            private = if (private != oldEntry?.private) private else null,
            notes = if (notes != oldEntry?.notes) notes else null,
        )
        .toFlow()
        .asDataResult {
            it.SaveMediaListEntry?.basicMediaListEntry
        }

    fun deleteEntry(id: Int) = api
        .deleteMediaListMutation(id)
        .toFlow()
        .asDataResult {
            it.DeleteMediaListEntry
        }
}