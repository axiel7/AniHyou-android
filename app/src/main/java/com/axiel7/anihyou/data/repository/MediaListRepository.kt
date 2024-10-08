package com.axiel7.anihyou.data.repository

import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.axiel7.anihyou.UpdateEntryMutation
import com.axiel7.anihyou.data.api.MediaListApi
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.model.media.advancedScoresMap
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.fragment.CommonPage
import com.axiel7.anihyou.fragment.FuzzyDate
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDate
import com.axiel7.anihyou.utils.DateUtils.toFuzzyDateInput
import com.axiel7.anihyou.utils.NumberUtils.isGreaterThanZero
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaListRepository @Inject constructor(
    private val api: MediaListApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {
    fun getMediaListCollection(
        userId: Int,
        mediaType: MediaType,
        sort: List<MediaListSort>,
        fetchFromNetwork: Boolean = false,
        chunk: Int?,
        perChunk: Int?,
    ) = api
        .mediaListCollection(userId, mediaType, sort, fetchFromNetwork, chunk, perChunk)
        .toFlow()
        .asPagedResult(page = { CommonPage(chunk, it.MediaListCollection?.hasNextChunk) }) {
            it.MediaListCollection?.lists.orEmpty()
        }

    fun getUserMediaList(
        userId: Int,
        mediaType: MediaType,
        status: MediaListStatus?,
        sort: List<MediaListSort>,
        fetchFromNetwork: Boolean = false,
        page: Int?,
        perPage: Int? = 25,
    ) = api
        .userMediaList(userId, mediaType, status, sort, fetchFromNetwork, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.mediaList?.mapNotNull { it?.commonMediaListEntry }.orEmpty()
        }

    fun incrementOneProgress(
        entry: BasicMediaListEntry,
        total: Int?
    ): Flow<DataResult<UpdateEntryMutation.SaveMediaListEntry?>> {
        val newProgress = (entry.progress ?: 0) + 1
        val totalDuration = total.takeIf { it != 0 }
        val isMaxProgress = totalDuration != null && newProgress >= totalDuration
        val isPlanning = entry.status == MediaListStatus.PLANNING
        val newStatus = when {
            isMaxProgress -> MediaListStatus.COMPLETED
            isPlanning -> MediaListStatus.CURRENT
            else -> null
        }
        return updateEntry(
            mediaId = entry.mediaId,
            progress = newProgress,
            status = newStatus,
            startedAt = LocalDate.now().takeIf {
                isPlanning || !entry.progress.isGreaterThanZero()
            }?.toFuzzyDate(),
            completedAt = LocalDate.now().takeIf { isMaxProgress }?.toFuzzyDate(),
        )
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