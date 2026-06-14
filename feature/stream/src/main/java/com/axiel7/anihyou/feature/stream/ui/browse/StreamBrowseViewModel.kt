package com.axiel7.anihyou.feature.stream.ui.browse

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.MediaListRepository
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.model.media.season
import com.axiel7.anihyou.feature.stream.data.model.PagedAnimeResponse
import com.axiel7.anihyou.feature.stream.data.model.SpotlightResponse
import com.axiel7.anihyou.feature.stream.data.model.StreamAnime
import com.axiel7.anihyou.feature.stream.data.repository.StreamRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.time.LocalDateTime

data class StreamBrowseUiState(
    val spotlight: List<StreamAnime> = emptyList(),
    val trending: List<StreamAnime> = emptyList(),
    val popular: List<StreamAnime> = emptyList(),
    val recent: List<StreamAnime> = emptyList(),
    val searchResults: List<StreamAnime> = emptyList(),
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    
    // Continue watching
    val currentlyWatching: List<CommonMediaListEntry> = emptyList(),
    val isLoadingCurrentlyWatching: Boolean = false,
    
    // Season selection
    val selectedSeason: MediaSeason = LocalDateTime.now().season(),
    val selectedYear: Int = LocalDateTime.now().year,
    val seasonalAnime: List<StreamAnime> = emptyList(),
    val isLoadingSeasonal: Boolean = false,

    override val isLoading: Boolean = false,
    override val error: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}

class StreamBrowseViewModel(
    private val streamRepository: StreamRepository,
    private val mediaListRepository: MediaListRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : UiStateViewModel<StreamBrowseUiState>() {

    override val initialState = StreamBrowseUiState()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true) }

            // Load all sections concurrently
            val tasks = listOf(
                async {
                    when (val r = streamRepository.getSpotlight()) {
                        is DataResult.Success -> mutableUiState.update { it.copy(spotlight = r.data.results) }
                        is DataResult.Error -> mutableUiState.update { it.copy(error = r.message) }
                        else -> {}
                    }
                },
                async {
                    when (val r = streamRepository.getTrending()) {
                        is DataResult.Success -> mutableUiState.update { it.copy(trending = r.data.results) }
                        else -> {}
                    }
                },
                async {
                    when (val r = streamRepository.getPopular()) {
                        is DataResult.Success -> mutableUiState.update { it.copy(popular = r.data.results) }
                        else -> {}
                    }
                },
                async {
                    when (val r = streamRepository.getRecent()) {
                        is DataResult.Success -> mutableUiState.update { it.copy(recent = r.data.results) }
                        else -> {}
                    }
                },
                async {
                    val state = mutableUiState.value
                    mutableUiState.update { it.copy(isLoadingSeasonal = true) }
                    when (val r = streamRepository.getAnimeBySeason(state.selectedSeason, state.selectedYear)) {
                        is DataResult.Success -> mutableUiState.update {
                            it.copy(seasonalAnime = r.data.results, isLoadingSeasonal = false)
                        }
                        is DataResult.Error -> mutableUiState.update {
                            it.copy(error = r.message, isLoadingSeasonal = false)
                        }
                        else -> {}
                    }
                },
                async {
                    val userId = defaultPreferencesRepository.userId.first()
                    if (userId != null) {
                        mutableUiState.update { it.copy(isLoadingCurrentlyWatching = true) }
                        mediaListRepository.getUserMediaList(
                            userId = userId,
                            mediaType = MediaType.ANIME,
                            statusIn = listOf(MediaListStatus.CURRENT, MediaListStatus.REPEATING),
                            sort = listOf(MediaListSort.UPDATED_TIME_DESC),
                            fetchFromNetwork = false,
                            page = 1,
                            perPage = 20,
                        ).first { it !is PagedResult.Loading }.let { result ->
                            mutableUiState.update { state ->
                                when (result) {
                                    is PagedResult.Success -> {
                                        state.copy(
                                            currentlyWatching = result.list,
                                            isLoadingCurrentlyWatching = false
                                        )
                                    }
                                    is PagedResult.Error -> {
                                        state.copy(
                                            isLoadingCurrentlyWatching = false,
                                            error = result.message
                                        )
                                    }
                                    else -> state
                                }
                            }
                        }
                    }
                }
            )

            tasks.awaitAll()
            mutableUiState.update { it.copy(isLoading = false) }
        }
    }

    fun fetchCurrentlyWatching() {
        viewModelScope.launch {
            val userId = defaultPreferencesRepository.userId.first() ?: return@launch
            mutableUiState.update { it.copy(isLoadingCurrentlyWatching = true) }
            mediaListRepository.getUserMediaList(
                userId = userId,
                mediaType = MediaType.ANIME,
                statusIn = listOf(MediaListStatus.CURRENT, MediaListStatus.REPEATING),
                sort = listOf(MediaListSort.UPDATED_TIME_DESC),
                fetchFromNetwork = false,
                page = 1,
                perPage = 20,
            ).collect { result ->
                mutableUiState.update { state ->
                    when (result) {
                        is PagedResult.Success -> {
                            state.copy(
                                currentlyWatching = result.list,
                                isLoadingCurrentlyWatching = false
                            )
                        }
                        is PagedResult.Loading -> {
                            state.copy(isLoadingCurrentlyWatching = true)
                        }
                        is PagedResult.Error -> {
                            state.copy(
                                isLoadingCurrentlyWatching = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSeasonSelected(season: MediaSeason) {
        mutableUiState.update { it.copy(selectedSeason = season) }
        fetchSeasonal()
    }

    fun onYearSelected(year: Int) {
        mutableUiState.update { it.copy(selectedYear = year) }
        fetchSeasonal()
    }

    private fun fetchSeasonal() {
        val state = mutableUiState.value
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoadingSeasonal = true) }
            when (val r = streamRepository.getAnimeBySeason(state.selectedSeason, state.selectedYear)) {
                is DataResult.Success -> mutableUiState.update {
                    it.copy(seasonalAnime = r.data.results, isLoadingSeasonal = false)
                }
                is DataResult.Error -> mutableUiState.update {
                    it.copy(error = r.message, isLoadingSeasonal = false)
                }
                else -> {}
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        mutableUiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            mutableUiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        // Instant inline search
        val state = mutableUiState.value
        val allLoadedAnime = (state.spotlight + state.trending + state.popular + state.recent + state.seasonalAnime +
                state.currentlyWatching.map { item ->
                    StreamAnime(
                        id = item.mediaId,
                        title = com.axiel7.anihyou.feature.stream.data.model.AnimeTitle(
                            english = item.media?.basicMediaDetails?.title?.userPreferred,
                            romaji = item.media?.basicMediaDetails?.title?.userPreferred
                        ),
                        coverImage = com.axiel7.anihyou.feature.stream.data.model.CoverImage(
                            large = item.media?.coverImage?.large
                        ),
                        format = item.media?.basicMediaDetails?.type?.rawValue
                    )
                })
            .distinctBy { it.id }
        val inlineMatches = allLoadedAnime.filter { it.displayTitle.fuzzyMatches(query) }

        mutableUiState.update { it.copy(searchResults = inlineMatches, isSearching = true) }

        viewModelScope.launch {
            when (val r = streamRepository.search(query)) {
                is DataResult.Success -> {
                    val combined = (inlineMatches + r.data.results).distinctBy { it.id }
                    mutableUiState.update {
                        it.copy(searchResults = combined, isSearching = false)
                    }
                }
                is DataResult.Error -> {
                    mutableUiState.update {
                        it.copy(searchResults = inlineMatches, isSearching = false)
                    }
                }
                else -> {}
            }
        }
    }
}

private fun String.fuzzyMatches(query: String): Boolean {
    if (query.isEmpty()) return true
    val cleanTitle = this.lowercase()
    val cleanQuery = query.lowercase()

    // Subsequence check (characters of query appear in order in title)
    var queryIdx = 0
    var titleIdx = 0
    while (queryIdx < cleanQuery.length && titleIdx < cleanTitle.length) {
        if (cleanQuery[queryIdx] == cleanTitle[titleIdx]) {
            queryIdx++
        }
        titleIdx++
    }
    if (queryIdx == cleanQuery.length) return true

    // Word boundary start-with check
    val words = cleanTitle.split(Regex("\\s+"))
    val queryWords = cleanQuery.split(Regex("\\s+"))
    return queryWords.all { qw ->
        words.any { w -> w.contains(qw) || qw.contains(w) }
    }
}
