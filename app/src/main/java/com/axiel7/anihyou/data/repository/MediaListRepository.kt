package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.FetchPolicy
import com.apollographql.apollo3.cache.normalized.fetchPolicy
import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.MediaListApi
import com.axiel7.anihyou.data.model.asDataResult
import com.axiel7.anihyou.data.model.asPagedResult
import com.axiel7.anihyou.data.model.media.advancedScoresMap
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.CommonPage
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
        status: MediaListStatus?,
        sort: List<MediaListSort>,
        fetchFromNetwork: Boolean = false,
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .userMediaListQuery(
            userId = userId,
            mediaType = mediaType,
            status = status,
            sort = sort,
            fetchFromNetwork = fetchFromNetwork,
            page = page,
            perPage = perPage,
        )
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.mediaList?.mapNotNull { it?.commonMediaListEntry }.orEmpty()
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
        advancedScores: Collection<Double>? = null,
        progress: Int? = null,
        progressVolumes: Int? = null,
        startedAt: FuzzyDate? = null,
        completedAt: FuzzyDate? = null,
        repeat: Int? = null,
        private: Boolean? = null,
        hiddenFromStatusLists: Boolean? = null,
        notes: String? = null,
        customLists: List<String?>? = null,
    ) = api
        .updateEntryMutation(
            mediaId = mediaId,
            status = status.takeIf { status != oldEntry?.status },
            score = score.takeIf { score != oldEntry?.score },
            advancedScores = advancedScores?.toList()
                .takeIf { oldEntry?.advancedScoresMap()?.values != advancedScores },
            progress = progress.takeIf { progress != oldEntry?.progress },
            progressVolumes = progressVolumes.takeIf { progressVolumes != oldEntry?.progressVolumes },
            startedAt =
            startedAt?.toFuzzyDateInput().takeIf { startedAt != oldEntry?.startedAt?.fuzzyDate },
            completedAt =
            completedAt?.toFuzzyDateInput()
                .takeIf { completedAt != oldEntry?.completedAt?.fuzzyDate },
            repeat = repeat.takeIf { repeat != oldEntry?.repeat },
            private = private.takeIf { private != oldEntry?.private },
            hiddenFromStatusLists = hiddenFromStatusLists
                .takeIf { hiddenFromStatusLists != oldEntry?.hiddenFromStatusLists },
            notes = notes.takeIf { notes != oldEntry?.notes },
            customLists = customLists,
        )
        .toFlow()
        .asDataResult {
            it.SaveMediaListEntry
        }

    suspend fun updateMediaListCache(data: BasicMediaListEntry) {
        api.updateMediaListCache(data)
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

    fun getMediaListIds(
        userId: Int,
        type: MediaType,
        status: MediaListStatus?,
        chunk: Int = 1,
        perChunk: Int = 500,
    ) = api
        .mediaListIds(userId, type, status, chunk, perChunk)
        .fetchPolicy(FetchPolicy.CacheFirst)
        .toFlow()
        .asPagedResult(
            page = { CommonPage(chunk, it.MediaListCollection?.hasNextChunk) }
        ) { data ->
            data.MediaListCollection?.lists
                ?.flatMap { list ->
                    list?.entries?.mapNotNull { it?.mediaId }.orEmpty()
                }
                .orEmpty()
        }
}