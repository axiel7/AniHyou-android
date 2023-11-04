package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.MediaListApi
import com.axiel7.anihyou.data.model.asDataResult
import com.axiel7.anihyou.data.model.asPagedResult
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.FuzzyDate
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDateInput
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaListRepository @Inject constructor(
    private val api: MediaListApi
) {

    fun getUserMediaListPage(
        userId: Int,
        mediaType: MediaType,
        status: MediaListStatus,
        sort: MediaListSort,
        fetchFromNetwork: Boolean = false,
        page: Int = 1,
        perPage: Int = 15,
    ) = api
        .userMediaListQuery(
            userId = userId,
            mediaType = mediaType,
            status = status,
            sort = listOf(sort, MediaListSort.MEDIA_ID_DESC),
            fetchFromNetwork = fetchFromNetwork,
            page = page,
            perPage = perPage,
        )
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) {
            it.Page?.mediaList?.filterNotNull().orEmpty()
        }

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
        oldEntry: BasicMediaListEntry? = null,
        mediaId: Int,
        status: MediaListStatus? = null,
        score: Double? = null,
        progress: Int? = null,
        progressVolumes: Int? = null,
        startedAt: FuzzyDate? = null,
        completedAt: FuzzyDate? = null,
        repeat: Int? = null,
        private: Boolean? = null,
        notes: String? = null,
        customLists: List<String?>? = null,
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
            customLists = customLists,
        )
        .toFlow()
        .asDataResult {
            it.SaveMediaListEntry
        }

    fun deleteEntry(id: Int) = api
        .deleteMediaListMutation(id)
        .toFlow()
        .asDataResult {
            it.DeleteMediaListEntry
        }

    @Suppress("UNCHECKED_CAST")
    fun getMediaListCustomLists(id: Int, userId: Int) = api
        .mediaListCustomLists(id, userId)
        .toFlow()
        .asDataResult {
            it.MediaList?.customLists as? LinkedHashMap<String, Boolean>
        }
}