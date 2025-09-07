package com.axiel7.anihyou.feature.explore.search

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.SearchRepository
import com.axiel7.anihyou.core.model.SearchType
import com.axiel7.anihyou.core.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.core.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.core.network.SearchMediaQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel(
    arguments: Routes.Search,
    private val searchRepository: SearchRepository,
) : PagedUiStateViewModel<SearchUiState>(), SearchEvent {

    private val mediaType = arguments.mediaType?.let { MediaType.safeValueOf(it) }
    private val mediaSort = arguments.mediaSort?.let { MediaSort.safeValueOf(it) }

    override val initialState =
        SearchUiState(
            searchType = if (mediaType == MediaType.MANGA) SearchType.MANGA else SearchType.ANIME,
            mediaSort = mediaSort ?: MediaSort.SEARCH_MATCH,
            genresAndTagsForSearch = GenresAndTagsForSearch(
                genreIn = arguments.genre?.let { listOf(it) } ?: emptyList(),
                tagIn = arguments.tag?.let { listOf(it) } ?: emptyList()
            ),
            onMyList = arguments.onList,
            hasNextPage = arguments.genre != null
                    || arguments.tag != null
                    || arguments.mediaSort != null
        )

    override fun setQuery(value: String) {
        mutableUiState.update {
            val shouldLoad = it.hasFiltersApplied || value.isNotBlank()
            it.copy(
                query = value,
                page = if (shouldLoad) 1 else it.page,
                hasNextPage = shouldLoad,
                isLoading = shouldLoad,
            )
        }
    }

    override fun setSearchType(value: SearchType) = mutableUiState.update {
        it.copy(searchType = value, page = 1, hasNextPage = true)
    }

    override fun setMediaSort(value: MediaSort) = mutableUiState.update {
        it.copy(mediaSort = value, page = 1, hasNextPage = true, isLoading = true)
    }

    override fun setMediaFormats(values: List<MediaFormatLocalizable>) = mutableUiState.update {
        it.copy(
            selectedMediaFormats = values,
            page = 1,
            hasNextPage = true,
            isLoading = true,
            mediaFormatsChanged = true,
        )
    }

    override fun setMediaStatuses(values: List<MediaStatusLocalizable>) = mutableUiState.update {
        it.copy(
            selectedMediaStatuses = values,
            page = 1,
            hasNextPage = true,
            isLoading = true,
            mediaStatusesChanged = true,
        )
    }

    override fun setStartYear(value: Int?) = mutableUiState.update {
        it.copy(startYear = value, page = 1, hasNextPage = true, isLoading = true)
    }

    override fun setEndYear(value: Int?) = mutableUiState.update {
        it.copy(endYear = value, page = 1, hasNextPage = true, isLoading = true)
    }

    override fun setSeason(value: MediaSeason?) = mutableUiState.update {
        it.copy(season = value, page = 1, hasNextPage = true, isLoading = true)
    }

    override fun setEpCh(value: IntRange?) = mutableUiState.update {
        it.copy(minEpCh = value?.start, maxEpCh = value?.endInclusive)
    }

    override fun setDuration(value: IntRange?) = mutableUiState.update {
        it.copy(minDuration = value?.start, maxDuration = value?.endInclusive)
    }

    override fun setOnMyList(value: Boolean?) = mutableUiState.update {
        it.copy(onMyList = value, page = 1, hasNextPage = true, isLoading = true)
    }

    override fun setIsDoujin(value: Boolean?) = mutableUiState.update {
        it.copy(isDoujin = value, page = 1, hasNextPage = true, isLoading = true)
    }

    override fun setIsAdult(value: Boolean?) = mutableUiState.update {
        it.copy(isAdult = value, page = 1, hasNextPage = true, isLoading = true)
    }

    override fun setCountry(value: CountryOfOrigin?) = mutableUiState.update {
        it.copy(country = value, page = 1, hasNextPage = true, isLoading = true)
    }

    override fun onGenreTagStateChanged(genresAndTagsForSearch: GenresAndTagsForSearch) =
        mutableUiState.update {
            it.copy(
                genresAndTagsForSearch = genresAndTagsForSearch,
                genresOrTagsChanged = true,
                page = 1,
                hasNextPage = true,
                isLoading = true
            )
        }

    override fun clearFilters() = mutableUiState.update {
        it.copy(
            genresAndTagsForSearch = GenresAndTagsForSearch(),
            genresOrTagsChanged = true,
            selectedMediaFormats = emptyList(),
            selectedMediaStatuses = emptyList(),
            startYear = null,
            endYear = null,
            onMyList = null,
            isDoujin = null,
            isAdult = null,
            country = null,
            season = null,
            clearedFilters = true,
            page = 1,
            hasNextPage = true,
            isLoading = true
        )
    }

    override fun selectMediaItem(value: SearchMediaQuery.Medium?) = mutableUiState.update {
        it.copy(selectedMediaItem = value)
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        mutableUiState.value.run {
            selectedMediaItem?.let { selectedItem ->
                val index = media.indexOf(selectedItem)
                if (index != -1) {
                    media[index] = selectedItem.copy(
                        mediaListEntry = newListEntry?.let {
                            SearchMediaQuery.MediaListEntry(
                                __typename = "SearchMediaQuery.MediaListEntry",
                                id = newListEntry.id,
                                mediaId = newListEntry.mediaId,
                                basicMediaListEntry = newListEntry
                            )
                        }
                    )
                }
            }
        }
    }

    init {
        // media search
        mutableUiState
            .filter { it.searchType.isSearchMedia && it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.query == new.query
                        && old.mediaType == new.mediaType
                        && old.mediaSort == new.mediaSort
                        && old.startYear == new.startYear
                        && old.endYear == new.endYear
                        && old.season == new.season
                        && old.minEpCh == new.minEpCh
                        && old.maxEpCh == new.maxEpCh
                        && old.minDuration == new.minDuration
                        && old.maxDuration == new.maxDuration
                        && old.onMyList == new.onMyList
                        && old.isDoujin == new.isDoujin
                        && old.isAdult == new.isAdult
                        && old.country == new.country
                        && !new.genresOrTagsChanged
                        && !new.mediaFormatsChanged
                        && !new.mediaStatusesChanged
            }
            .flatMapLatest { uiState ->
                searchRepository.searchMedia(
                    mediaType = uiState.mediaType!!,
                    query = uiState.query,
                    sort = listOf(uiState.mediaSortForSearch),
                    genreIn = uiState.genresAndTagsForSearch.genreIn,
                    genreNotIn = uiState.genresAndTagsForSearch.genreNot,
                    tagIn = uiState.genresAndTagsForSearch.tagIn,
                    tagNotIn = uiState.genresAndTagsForSearch.tagNot,
                    minimumTagPercentage = uiState.genresAndTagsForSearch.minimumTagPercentage,
                    formatIn = uiState.selectedMediaFormats.map { it.value },
                    statusIn = uiState.selectedMediaStatuses.map { it.value },
                    episodesLesser = uiState.maxEpCh.takeIf { uiState.isAnime },
                    episodesGreater = uiState.minEpCh?.minus(1).takeIf { uiState.isAnime },
                    durationLesser = uiState.maxDuration.takeIf { uiState.isAnime },
                    durationGreater = uiState.minDuration?.minus(1).takeIf { uiState.isAnime },
                    chaptersLesser = uiState.maxEpCh.takeIf { uiState.isManga },
                    chaptersGreater = uiState.minEpCh?.minus(1).takeIf { uiState.isManga },
                    volumesLesser = uiState.maxDuration.takeIf { uiState.isManga },
                    volumesGreater = uiState.minDuration?.minus(1).takeIf { uiState.isManga },
                    startYear = uiState.startYear,
                    endYear = uiState.endYear,
                    season = uiState.season,
                    onList = uiState.onMyList,
                    isLicensed = uiState.isDoujin?.not(),
                    isAdult = uiState.isAdult,
                    country = uiState.country,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.media.clear()
                        it.media.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                            genresOrTagsChanged = false,
                            mediaFormatsChanged = false,
                            mediaStatusesChanged = false,
                            clearedFilters = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        // character search
        mutableUiState
            .filter {
                it.searchType == SearchType.CHARACTER
                        && it.hasNextPage
                        && it.query.isNotBlank()
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.query == new.query
            }
            .flatMapLatest { uiState ->
                searchRepository.searchCharacter(
                    query = uiState.query,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.characters.clear()
                        it.characters.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        // staff search
        mutableUiState
            .filter {
                it.searchType == SearchType.STAFF
                        && it.hasNextPage
                        && it.query.isNotBlank()
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.query == new.query
            }
            .flatMapLatest { uiState ->
                searchRepository.searchStaff(
                    query = uiState.query,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.staff.clear()
                        it.staff.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        // studio search
        mutableUiState
            .filter {
                it.searchType == SearchType.STUDIO
                        && it.hasNextPage
                        && it.query.isNotBlank()
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.query == new.query
            }
            .flatMapLatest { uiState ->
                searchRepository.searchStudio(
                    query = uiState.query,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.studios.clear()
                        it.studios.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        // user search
        mutableUiState
            .filter {
                it.searchType == SearchType.USER
                        && it.hasNextPage
                        && it.query.isNotBlank()
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.query == new.query
            }
            .flatMapLatest { uiState ->
                searchRepository.searchUser(
                    query = uiState.query,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.users.clear()
                        it.users.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}