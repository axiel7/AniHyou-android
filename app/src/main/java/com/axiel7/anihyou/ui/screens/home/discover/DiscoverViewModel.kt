package com.axiel7.anihyou.ui.screens.home.discover

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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
import com.axiel7.anihyou.utils.DateUtils.currentAnimeSeason
import com.axiel7.anihyou.utils.DateUtils.nextAnimeSeason
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : ViewModel() {

    val infos = mutableStateListOf(
        DiscoverInfo.AIRING,
        DiscoverInfo.THIS_SEASON,
        DiscoverInfo.TRENDING_ANIME
    )

    fun addNextInfo() {
        if (infos.size < DiscoverInfo.entries.size)
            infos.add(DiscoverInfo.entries[infos.size])
    }

    private val now = LocalDateTime.now()
    val nowAnimeSeason = now.currentAnimeSeason()
    val nextAnimeSeason = now.nextAnimeSeason()

    val airingOnMyList = defaultPreferencesRepository.airingOnMyList
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    var isLoadingAiring by mutableStateOf(true)
        private set

    val airingAnime = mutableStateListOf<AiringAnimesQuery.AiringSchedule>()

    fun fetchAiringAnime() {
        if (airingAnime.isEmpty()) {
            mediaRepository.getAiringAnimesPage(
                airingAtGreater = System.currentTimeMillis() / 1000,
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    airingAnime.addAll(result.list)
                }
                isLoadingAiring = result is PagedResult.Loading
            }.launchIn(viewModelScope)
        }
    }

    val airingAnimeOnMyList = mutableStateListOf<AiringOnMyListQuery.Medium>()

    fun fetchAiringAnimeOnMyList() {
        if (airingAnimeOnMyList.isEmpty()) {
            mediaRepository.getAiringAnimeOnMyListPage(page = 1)
                .onEach { result ->
                    if (result is PagedResult.Success) {
                        airingAnimeOnMyList.addAll(result.list)
                    }
                    isLoadingAiring = result is PagedResult.Loading
                }
                .launchIn(viewModelScope)
        }
    }

    var isLoadingThisSeason by mutableStateOf(true)
        private set

    val thisSeasonAnime = mutableStateListOf<SeasonalAnimeQuery.Medium>()

    fun fetchThisSeasonAnime() {
        if (thisSeasonAnime.isEmpty()) {
            mediaRepository.getSeasonalAnimePage(
                animeSeason = nowAnimeSeason,
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    thisSeasonAnime.addAll(result.list)
                }
                isLoadingThisSeason = result is PagedResult.Loading
            }.launchIn(viewModelScope)
        }
    }

    var isLoadingTrendingAnime by mutableStateOf(true)
        private set

    val trendingAnime = mutableStateListOf<MediaSortedQuery.Medium>()

    fun fetchTrendingAnime() {
        if (trendingAnime.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.ANIME,
                sort = listOf(MediaSort.TRENDING_DESC),
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    trendingAnime.addAll(result.list)
                }
                isLoadingTrendingAnime = result is PagedResult.Loading
            }.launchIn(viewModelScope)
        }
    }

    var isLoadingNextSeason by mutableStateOf(true)
        private set

    val nextSeasonAnime = mutableStateListOf<SeasonalAnimeQuery.Medium>()

    fun fetchNextSeasonAnime() {
        if (nextSeasonAnime.isEmpty()) {
            mediaRepository.getSeasonalAnimePage(
                animeSeason = nextAnimeSeason,
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    nextSeasonAnime.addAll(result.list)
                }
                isLoadingNextSeason = result is PagedResult.Loading
            }.launchIn(viewModelScope)
        }
    }

    var isLoadingTrendingManga by mutableStateOf(true)
        private set

    val trendingManga = mutableStateListOf<MediaSortedQuery.Medium>()

    fun fetchTrendingManga() {
        if (trendingManga.isEmpty()) {
            mediaRepository.getMediaSortedPage(
                mediaType = MediaType.MANGA,
                sort = listOf(MediaSort.TRENDING_DESC),
                page = 1
            ).onEach { result ->
                if (result is PagedResult.Success) {
                    trendingManga.addAll(result.list)
                }
                isLoadingTrendingManga = result is PagedResult.Loading
            }.launchIn(viewModelScope)
        }
    }

    var selectedMediaDetails: BasicMediaDetails? = null
        private set
    var selectedMediaListEntry: BasicMediaListEntry? = null
        private set

    fun selectItem(details: BasicMediaDetails?, listEntry: BasicMediaListEntry?) {
        selectedMediaDetails = details
        selectedMediaListEntry = listEntry
    }
}