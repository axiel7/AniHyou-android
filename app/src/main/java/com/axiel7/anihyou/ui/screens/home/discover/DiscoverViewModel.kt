package com.axiel7.anihyou.ui.screens.home.discover

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.api.response.PagedResult
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.utils.DateUtils.currentAnimeSeason
import com.axiel7.anihyou.utils.DateUtils.nextAnimeSeason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<DiscoverUiState>(), DiscoverEvent {

    private val now = LocalDateTime.now()

    override val initialState =
        DiscoverUiState(
            infos = mutableStateListOf(
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

    override fun refresh() {
        mutableUiState.update { it.copy(isLoading = true) }
        mutableUiState.value.run {
            airingAnime.clear()
            airingAnimeOnMyList.clear()
            thisSeasonAnime.clear()
            trendingAnime.clear()
            nextSeasonAnime.clear()
            trendingManga.clear()
            newlyAnime.clear()
            newlyManga.clear()
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
    }
}