package com.axiel7.anihyou.core.domain.repository

import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.utils.NumberUtils.isGreaterThanZero
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.model.media.advancedScoresMap
import com.axiel7.anihyou.core.model.media.isUsingVolumeProgress
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.network.UpdateEntryMutation
import com.axiel7.anihyou.core.network.api.MediaListApi
import com.axiel7.anihyou.core.network.api.model.isNull
import com.axiel7.anihyou.core.network.api.model.toFuzzyDate
import com.axiel7.anihyou.core.network.api.model.toFuzzyDateInput
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.CommonPage
import com.axiel7.anihyou.core.network.fragment.FuzzyDate
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate

class MediaListRepository (
    private val api: MediaListApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    private val _lastUpdatedEntry = MutableStateFlow<BasicMediaListEntry?>(null)
    val lastUpdatedEntry = _lastUpdatedEntry.asStateFlow()

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
        statusIn: List<MediaListStatus>?,
        sort: List<MediaListSort>,
        fetchFromNetwork: Boolean = false,
        page: Int?,
        perPage: Int? = 25,
    ) = api
        .userMediaList(userId, mediaType, statusIn, sort, fetchFromNetwork, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.mediaList?.mapNotNull { it?.commonMediaListEntry }.orEmpty()
        }

    fun getMySeasonalAnime(
        animeSeason: AnimeSeason,
        sort: List<MediaSort> = listOf(MediaSort.POPULARITY_DESC),
        fetchFromNetwork: Boolean = false,
        page: Int,
        perPage: Int = 25,
    ) = api
        .mySeasonalAnimeQuery(animeSeason.toDto(), sort, fetchFromNetwork, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            data.Page?.media?.mapNotNull { it?.mediaListEntry?.commonMediaListEntry }.orEmpty()
        }

    fun incrementProgress(
        entry: BasicMediaListEntry,
        increment: Int = 1,
        total: Int?
    ): Flow<DataResult<UpdateEntryMutation.SaveMediaListEntry?>> {
        val newProgress = (entry.progressOrVolumes() ?: 0) + increment
        val totalDuration = total.takeIf { it != 0 }
        val isMaxProgress = totalDuration != null && newProgress >= totalDuration
        val isPlanning = entry.status == MediaListStatus.PLANNING
        val isRepeating = entry.status == MediaListStatus.REPEATING

        val newStatus = when {
            isMaxProgress -> MediaListStatus.COMPLETED
            isPlanning -> MediaListStatus.CURRENT
            else -> null
        }
        return updateEntry(
            oldEntry = entry,
            mediaId = entry.mediaId,
            progress = newProgress.takeIf { !entry.isUsingVolumeProgress() },
            progressVolumes = newProgress.takeIf { entry.isUsingVolumeProgress() },
            status = newStatus,
            startedAt = LocalDate.now().takeIf {
                (!isRepeating || entry.startedAt?.fuzzyDate?.isNull() != false) &&
                (isPlanning || !entry.progress.isGreaterThanZero())
            }?.toFuzzyDate() ?: entry.startedAt?.fuzzyDate,
            completedAt = LocalDate.now().takeIf {
                (!isRepeating || entry.completedAt?.fuzzyDate?.isNull() != false) && isMaxProgress
            }?.toFuzzyDate() ?: entry.completedAt?.fuzzyDate,
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
        startedAt: FuzzyDate? = oldEntry?.startedAt?.fuzzyDate,
        completedAt: FuzzyDate? = oldEntry?.completedAt?.fuzzyDate,
        repeat: Int? = null,
        private: Boolean? = null,
        hiddenFromStatusLists: Boolean? = null,
        notes: String? = null,
    ) = api
        .updateEntryMutation(
            mediaId = mediaId,
            status = status.takeIf { status != oldEntry?.status },
            score = score.takeIf { score != oldEntry?.score },
            advancedScores = advancedScores?.toList()
                .takeIf { oldEntry?.advancedScoresMap()?.values != advancedScores },
            progress = progress.takeIf { progress != oldEntry?.progress },
            progressVolumes = progressVolumes.takeIf { progressVolumes != oldEntry?.progressVolumes },
            startedAt = startedAt?.toFuzzyDateInput(),
            completedAt = completedAt?.toFuzzyDateInput(),
            repeat = repeat.takeIf { repeat != oldEntry?.repeat },
            private = private.takeIf { private != oldEntry?.private },
            hiddenFromStatusLists = hiddenFromStatusLists
                .takeIf { hiddenFromStatusLists != oldEntry?.hiddenFromStatusLists },
            notes = notes.takeIf { notes != oldEntry?.notes },
        )
        .toFlow()
        .onEach {
            it.data?.SaveMediaListEntry?.basicMediaListEntry?.let { entry ->
                _lastUpdatedEntry.emit(entry)
                api.updateMediaListCache(entry)
            }
        }
        .asDataResult {
            it.SaveMediaListEntry
        }

    fun updateEntryCustomLists(
        mediaId: Int,
        customLists: List<String?>,
    ) = api
        .updateEntryCustomListsMutation(mediaId, customLists)
        .toFlow()
        .onEach {
            it.data?.SaveMediaListEntry?.basicMediaListEntry?.let { entry ->
                _lastUpdatedEntry.emit(entry)
                api.updateMediaListCache(entry)
            }
        }
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