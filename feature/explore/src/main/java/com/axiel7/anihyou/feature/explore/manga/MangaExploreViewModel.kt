package com.axiel7.anihyou.feature.explore.manga

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.base.extensions.indexOfFirstOrNull
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.ExploreMedia
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MangaExploreViewModel(
    private val mediaRepository: MediaRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository
) : UiStateViewModel<MangaExploreUiState>(), MangaExploreEvent {

    override val initialState = MangaExploreUiState(
        infos = mutableStateListOf(
            MangaDiscoverInfo.NEWLY_MANGA,
            MangaDiscoverInfo.TRENDING_MANGA,
        )
    )

    override fun addNextInfo() {
        mutableUiState.value.run {
            if (infos.size < MangaDiscoverInfo.entries.size) {
                infos.add(MangaDiscoverInfo.entries[infos.size])
            }
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

    override fun refresh() {
        mutableUiState.update { it.copy(isLoading = true) }
        mutableUiState.value.run {
            trendingManga.clear()
            newlyManga.clear()
            fetchTrendingManga()
            fetchNewlyManga()
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

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        val selectedMediaId = uiState.value.selectedMediaDetails?.id ?: return
        mutableUiState.update { it.copy(selectedMediaListEntry = newListEntry) }

        uiState.value.allLists.forEach { list ->
            list.indexOfFirstOrNull { it.id == selectedMediaId }?.let { index ->
                list[index] = list[index].copy(
                    mediaListEntry = newListEntry?.let {
                        ExploreMedia.MediaListEntry(
                            __typename = "ExploreMedia.MediaListEntry",
                            id = it.id,
                            mediaId = it.mediaId,
                            basicMediaListEntry = it,
                        )
                    }
                )
            }
        }
    }

    init {
        defaultPreferencesRepository.displayAdult
            .onEach { value ->
                mutableUiState.update { it.copy(displayAdult = value ?: false) }
            }
            .launchIn(viewModelScope)
    }
}