package com.axiel7.anihyou.feature.explore.anime

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.base.extensions.indexOfFirstOrNull
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.model.media.currentAnimeSeason
import com.axiel7.anihyou.core.model.media.nextAnimeSeason
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds

class AnimeExploreViewModel(
    private val mediaRepository: MediaRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository
) : UiStateViewModel<AnimeExploreUiState>(), AnimeExploreEvent {

    // idk how this could be made any simpler than this.
    override fun onUpdateListEntry(
        newListEntry: BasicMediaListEntry?
    ) {
        val selectedMediaId = uiState.value.selectedMediaDetails?.id ?: return
        mutableUiState.update { it.copy(selectedMediaListEntry = newListEntry) }

        uiState.value.airingAnimeOnMyList.indexOfFirstOrNull { it.id == selectedMediaId }
            ?.let { index ->
                val list = uiState.value.airingAnimeOnMyList
                val oldValue = list[index]
                list[index] = oldValue.copy(
                    mediaListEntry = newListEntry?.let {
                        oldValue.mediaListEntry?.copy(basicMediaListEntry = it)
                    }
                )
            }

        uiState.value.airingAnime.indexOfFirstOrNull { it.media?.id == selectedMediaId }
            ?.let { index ->
                val list = uiState.value.airingAnime
                val oldValue = list[index]
                list[index] = oldValue.copy(
                    media = oldValue.media?.copy(
                        mediaListEntry = newListEntry?.let {
                            oldValue.media?.mediaListEntry?.copy(basicMediaListEntry = it)
                        }
                    )
                )
            }

        uiState.value.thisSeasonAnime.indexOfFirstOrNull { it.id == selectedMediaId }
            ?.let { index ->
                val list = uiState.value.thisSeasonAnime
                val oldValue = list[index]
                list[index] = oldValue.copy(
                    mediaListEntry = newListEntry?.let {
                        oldValue.mediaListEntry?.copy(basicMediaListEntry = it)
                    }
                )
            }

        uiState.value.trendingAnime.indexOfFirstOrNull { it.id == selectedMediaId }
            ?.let { index ->
                val list = uiState.value.trendingAnime
                val oldValue = list[index]
                list[index] = oldValue.copy(
                    mediaListEntry = newListEntry?.let {
                        oldValue.mediaListEntry?.copy(basicMediaListEntry = it)
                    }
                )
            }

        uiState.value.nextSeasonAnime.indexOfFirstOrNull { it.id == selectedMediaId }
            ?.let { index ->
                val list = uiState.value.nextSeasonAnime
                val oldValue = list[index]
                list[index] = oldValue.copy(
                    mediaListEntry = newListEntry?.let {
                        oldValue.mediaListEntry?.copy(basicMediaListEntry = it)
                    }
                )
            }

        uiState.value.newlyAnime.indexOfFirstOrNull { it.id == selectedMediaId }
            ?.let { index ->
                val list = uiState.value.newlyAnime
                val oldValue = list[index]
                list[index] = oldValue.copy(
                    mediaListEntry = newListEntry?.let {
                        oldValue.mediaListEntry?.copy(basicMediaListEntry = it)
                    }
                )
            }
    }

    private val now = LocalDateTime.now()

    override val initialState =
        AnimeExploreUiState(
            infos = mutableStateListOf(
                AnimeDiscoverInfo.AIRING,
                AnimeDiscoverInfo.THIS_SEASON,
                AnimeDiscoverInfo.TRENDING_ANIME
            ),
            nowAnimeSeason = now.currentAnimeSeason(),
            nextAnimeSeason = now.nextAnimeSeason(),
        )

    override fun addNextInfo() {
        mutableUiState.value.run {
            if (infos.size < AnimeDiscoverInfo.entries.size) {
                infos.add(AnimeDiscoverInfo.entries[infos.size])
            }
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

    override fun refresh() {
        mutableUiState.update { it.copy(isLoading = true) }
        mutableUiState.value.run {
            airingAnime.clear()
            airingAnimeOnMyList.clear()
            thisSeasonAnime.clear()
            trendingAnime.clear()
            nextSeasonAnime.clear()
            newlyAnime.clear()
            newlyManga.clear()
            if (airingOnMyList == true) fetchAiringAnimeOnMyList()
            else fetchAiringAnime()
            fetchThisSeasonAnime()
            fetchTrendingAnime()
        }
        viewModelScope.launch {
            // PullToRefresh needs a min delay when changing the isRefreshing state
            delay(1000.milliseconds)
            mutableUiState.update { it.copy(isLoading = false) }
        }
    }

    override fun selectItem(
        details: BasicMediaDetails?,
        listEntry: BasicMediaListEntry?,
    ) {
        mutableUiState.update {
            it.copy(
                selectedMediaDetails = details,
                selectedMediaListEntry = listEntry,
            )
        }
    }

    init {
        defaultPreferencesRepository.airingOnMyList
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