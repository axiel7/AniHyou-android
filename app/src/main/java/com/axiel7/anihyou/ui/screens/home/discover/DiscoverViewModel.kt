package com.axiel7.anihyou.ui.screens.home.discover

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.AiringOnMyListQuery
import com.axiel7.anihyou.MediaSortedQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.data.model.PagedResult
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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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
            nowAnimeSeason = now.currentAnimeSeason(),
            nextAnimeSeason = now.nextAnimeSeason(),
        )

    val infos = mutableStateListOf(
        DiscoverInfo.AIRING,
        DiscoverInfo.THIS_SEASON,
        DiscoverInfo.TRENDING_ANIME
    )

    override fun addNextInfo() {
        if (infos.size < DiscoverInfo.entries.size)
            infos.add(DiscoverInfo.entries[infos.size])
    }

    val airingAnime = mutableStateListOf<AiringAnimesQuery.AiringSchedule>()

    override fun fetchAiringAnime() {
        if (airingAnime.isEmpty()) {
            mediaRepository.getAiringAnimesPage(
                airingAtGreater = System.currentTimeMillis() / 1000,
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    airingAnime.addAll(result.list)
                }
                mutableUiState.update {
                    it.copy(
                        isLoadingAiring = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    val airingAnimeOnMyList = mutableStateListOf<AiringOnMyListQuery.Medium>()

    override fun fetchAiringAnimeOnMyList() {
        if (airingAnimeOnMyList.isEmpty()) {
            mediaRepository.getAiringAnimeOnMyListPage(page = 1)
                .onEach { result ->
                    if (result is PagedResult.Success) {
                        airingAnimeOnMyList.addAll(result.list)
                    }
                    mutableUiState.update {
                        it.copy(
                            isLoadingAiring = result is PagedResult.Loading,
                            error = (result as? PagedResult.Error)?.message
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    val thisSeasonAnime = mutableStateListOf<SeasonalAnimeQuery.Medium>()

    override fun fetchThisSeasonAnime() {
        if (thisSeasonAnime.isEmpty()) {
            mediaRepository.getSeasonalAnimePage(
                animeSeason = uiState.value.nowAnimeSeason,
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    thisSeasonAnime.addAll(result.list)
                }
                mutableUiState.update {
                    it.copy(
                        isLoadingThisSeason = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    val trendingAnime = mutableStateListOf<MediaSortedQuery.Medium>()

    override fun fetchTrendingAnime() {
        if (trendingAnime.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.ANIME,
                sort = listOf(MediaSort.TRENDING_DESC),
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    trendingAnime.addAll(result.list)
                }
                mutableUiState.update {
                    it.copy(
                        isLoadingTrendingAnime = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    val nextSeasonAnime = mutableStateListOf<SeasonalAnimeQuery.Medium>()

    override fun fetchNextSeasonAnime() {
        if (nextSeasonAnime.isEmpty()) {
            mediaRepository.getSeasonalAnimePage(
                animeSeason = uiState.value.nextAnimeSeason,
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    nextSeasonAnime.addAll(result.list)
                }
                mutableUiState.update {
                    it.copy(
                        isLoadingNextSeason = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
        }
    }

    val trendingManga = mutableStateListOf<MediaSortedQuery.Medium>()

    override fun fetchTrendingManga() {
        if (trendingManga.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.MANGA,
                sort = listOf(MediaSort.TRENDING_DESC),
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    trendingManga.addAll(result.list)
                }
                mutableUiState.update {
                    it.copy(
                        isLoadingTrendingManga = result is PagedResult.Loading,
                        error = (result as? PagedResult.Error)?.message
                    )
                }
            }.launchIn(viewModelScope)
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