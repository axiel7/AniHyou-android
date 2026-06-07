package com.axiel7.anihyou.feature.home.discover

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaListRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.model.media.currentAnimeSeason
import com.axiel7.anihyou.core.model.media.nextAnimeSeason
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class DiscoverViewModel(
    private val mediaRepository: MediaRepository,
    private val mediaListRepository: MediaListRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<DiscoverUiState>(), DiscoverEvent {

    private val now = LocalDateTime.now()

    private val myUserId = defaultPreferencesRepository.userId

    override val initialState =
        DiscoverUiState(
            infos = mutableStateListOf(
                DiscoverInfo.CURRENTLY_WATCHING,
                DiscoverInfo.AIRING,
                DiscoverInfo.THIS_SEASON,
                DiscoverInfo.TRENDING_ANIME
            ),
            nowAnimeSeason = now.currentAnimeSeason(),
            nextAnimeSeason = now.nextAnimeSeason(),
        )

    override fun addNextInfo() {
        mutableUiState.value.run {
            if (infos.size < DiscoverInfo.entries.size)
                infos.add(DiscoverInfo.entries[infos.size])
        }
    }

    override fun fetchAiringAnime() {
        if (mutableUiState.value.airingAnime.isEmpty()) {
            mediaRepository.getAiringAnimesPage(
                airingAtGreater = System.currentTimeMillis() / 1000,
                isAdult = uiState.value.displayAdult,
                page = 1
            ).onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.airingAnime.addAll(result.list)
                    }
                    it.copy(
                        isLoadingAiring = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchAiringAnimeOnMyList() {
        if (mutableUiState.value.airingAnimeOnMyList.isEmpty()) {
            mediaRepository.getAiringAnimeOnMyListPage(page = 1)
                .onEach { result ->
                    mutableUiState.update {
                        if (result is PagedResult.Success) {
                            it.airingAnimeOnMyList.addAll(result.list)
                        }
                        it.copy(
                            isLoadingAiring = result is PagedResult.Loading,
                            error = (result as? PagedResult.Error)?.message
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    override fun fetchThisSeasonAnime() {
        if (mutableUiState.value.thisSeasonAnime.isEmpty()) {
            mediaRepository.getSeasonalAnimePage(
                animeSeason = uiState.value.nowAnimeSeason,
                isAdult = uiState.value.isAdult,
                page = 1
            ).onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.thisSeasonAnime.addAll(result.list)
                    }
                    it.copy(
                        isLoadingThisSeason = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchTrendingAnime() {
        if (mutableUiState.value.trendingAnime.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.ANIME,
                sort = listOf(MediaSort.TRENDING_DESC),
                isAdult = uiState.value.isAdult,
                page = 1
            ).onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.trendingAnime.addAll(result.list)
                    }
                    it.copy(
                        isLoadingTrendingAnime = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchNextSeasonAnime() {
        if (mutableUiState.value.nextSeasonAnime.isEmpty()) {
            mediaRepository.getSeasonalAnimePage(
                animeSeason = uiState.value.nextAnimeSeason,
                isAdult = uiState.value.isAdult,
                page = 1
            ).onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.nextSeasonAnime.addAll(result.list)
                    }
                    it.copy(
                        isLoadingNextSeason = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchTrendingManga() {
        if (mutableUiState.value.trendingManga.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.MANGA,
                sort = listOf(MediaSort.TRENDING_DESC),
                isAdult = uiState.value.isAdult,
                page = 1
            ).onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.trendingManga.addAll(result.list)
                    }
                    it.copy(
                        isLoadingTrendingManga = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchNewlyAnime() {
        if (mutableUiState.value.newlyAnime.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.ANIME,
                sort = listOf(MediaSort.ID_DESC),
                isAdult = uiState.value.isAdult,
                page = 1
            ).onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.newlyAnime.addAll(result.list)
                    }
                    it.copy(
                        isLoadingNewlyAnime = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchNewlyManga() {
        if (mutableUiState.value.newlyManga.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.MANGA,
                sort = listOf(MediaSort.ID_DESC),
                isAdult = uiState.value.isAdult,
                page = 1
            ).onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.newlyManga.addAll(result.list)
                    }
                    it.copy(
                        isLoadingNewlyManga = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchPopularAnime() {
        if (mutableUiState.value.popularAnime.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.ANIME,
                sort = listOf(MediaSort.POPULARITY_DESC),
                isAdult = uiState.value.isAdult,
                page = 1
            ).onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.popularAnime.addAll(result.list)
                    }
                    it.copy(
                        isLoadingPopularAnime = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchRecommendations() {
        if (mutableUiState.value.recommendations.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.ANIME,
                sort = listOf(MediaSort.SCORE_DESC),
                isAdult = uiState.value.isAdult,
                page = 1
            ).onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.recommendations.addAll(result.list)
                    }
                    it.copy(
                        isLoadingRecommendations = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchRandomAnime() {
        viewModelScope.launch {
            val randomPage = (1..50).random()
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.ANIME,
                sort = listOf(MediaSort.ID),
                isAdult = uiState.value.isAdult,
                page = randomPage,
                perPage = 1,
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    val media = result.list.firstOrNull()
                    mutableUiState.update { it.copy(randomAnimeId = media?.basicMediaDetails?.id) }
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun fetchCurrentlyWatching() {
        if (mutableUiState.value.currentlyWatching.isEmpty()) {
            viewModelScope.launch {
                val userId = myUserId.first() ?: return@launch
                mediaListRepository.getUserMediaList(
                    userId = userId,
                    mediaType = MediaType.ANIME,
                    statusIn = listOf(MediaListStatus.CURRENT, MediaListStatus.REPEATING),
                    sort = listOf(MediaListSort.UPDATED_TIME_DESC),
                    fetchFromNetwork = false,
                    page = 1,
                    perPage = 20,
                ).onEach { result ->
                    mutableUiState.update {
                        if (result is PagedResult.Success) {
                            it.currentlyWatching.addAll(result.list)
                        }
                        it.copy(
                            isLoadingCurrentlyWatching = result is PagedResult.Loading,
                            error = (result as? PagedResult.Error)?.message
                        )
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    override fun refresh() {
        mutableUiState.update { it.copy(isLoading = true) }
        mutableUiState.value.run {
            currentlyWatching.clear()
            airingAnime.clear()
            airingAnimeOnMyList.clear()
            thisSeasonAnime.clear()
            trendingAnime.clear()
            nextSeasonAnime.clear()
            trendingManga.clear()
            newlyAnime.clear()
            newlyManga.clear()
            popularAnime.clear()
            recommendations.clear()
            fetchCurrentlyWatching()
            if (airingOnMyList == true) fetchAiringAnimeOnMyList()
            else fetchAiringAnime()
            fetchThisSeasonAnime()
            fetchTrendingAnime()
        }
        viewModelScope.launch {
            // PullToRefresh needs a min delay when changing the isRefreshing state
            delay(1000)
            mutableUiState.update { it.copy(isLoading = false) }
        }
    }

    override fun selectItem(details: BasicMediaDetails?, listEntry: BasicMediaListEntry?) {
        mutableUiState.update {
            it.copy(
                selectedMediaDetails = details,
                selectedMediaListEntry = listEntry
            )
        }
    }

    init {
        defaultPreferencesRepository.airingOnMyList
            .filterNotNull()
            .onEach { value ->
                mutableUiState.update { it.copy(airingOnMyList = value) }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.displayAdult
            .onEach { value ->
                mutableUiState.update { it.copy(displayAdult = value ?: false) }
            }
            .launchIn(viewModelScope)
    }
}