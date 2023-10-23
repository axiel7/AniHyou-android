package com.axiel7.anihyou.ui.screens.explore.search

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.SearchCharacterQuery
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.SearchStaffQuery
import com.axiel7.anihyou.SearchStudioQuery
import com.axiel7.anihyou.SearchUserQuery
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.data.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.data.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.data.repository.SearchRepository
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.ui.screens.explore.GENRE_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.MEDIA_SORT_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.MEDIA_TYPE_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.TAG_ARGUMENT
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository,
) : PagedUiStateViewModel<SearchUiState>() {

    private val initialMediaType = savedStateHandle
        .get<String?>(MEDIA_TYPE_ARGUMENT.removeFirstAndLast())?.let { MediaType.valueOf(it) }

    private val initialMediaSort = savedStateHandle
        .get<String?>(MEDIA_SORT_ARGUMENT.removeFirstAndLast())?.let { MediaSort.valueOf(it) }

    private val initialGenre: String? = savedStateHandle[GENRE_ARGUMENT.removeFirstAndLast()]

    private val initialTag: String? = savedStateHandle[TAG_ARGUMENT.removeFirstAndLast()]

    override val mutableUiState = MutableStateFlow(
        SearchUiState(
            searchType = if (initialMediaType == MediaType.MANGA) SearchType.MANGA else SearchType.ANIME,
            mediaSort = initialMediaSort ?: MediaSort.SEARCH_MATCH,
            genresAndTagsForSearch = GenresAndTagsForSearch(
                genreIn = initialGenre?.let { listOf(it) } ?: emptyList(),
                tagIn = initialTag?.let { listOf(it) } ?: emptyList()
            ),
            hasNextPage = initialGenre != null
                    || initialTag != null
                    || initialMediaType != null
                    || initialMediaSort != null
        )
    )
    override val uiState = mutableUiState.asStateFlow()

    fun setQuery(value: String) = mutableUiState.update {
        it.copy(query = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setSearchType(value: SearchType) = mutableUiState.update {
        it.copy(searchType = value, page = 1, hasNextPage = true)
    }

    fun setMediaSort(value: MediaSort) = mutableUiState.update {
        it.copy(mediaSort = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setMediaFormats(values: List<MediaFormatLocalizable>) = mutableUiState.update {
        it.copy(
            selectedMediaFormats = values,
            page = 1,
            hasNextPage = true,
            isLoading = true,
            mediaFormatsChanged = true,
        )
    }

    fun setMediaStatuses(values: List<MediaStatusLocalizable>) = mutableUiState.update {
        it.copy(
            selectedMediaStatuses = values,
            page = 1,
            hasNextPage = true,
            isLoading = true,
            mediaStatusesChanged = true,
        )
    }

    fun setStartYear(value: Int?) = mutableUiState.update {
        it.copy(startYear = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setEndYear(value: Int?) = mutableUiState.update {
        it.copy(endYear = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setOnMyList(value: Boolean?) = mutableUiState.update {
        it.copy(onMyList = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun onGenreTagStateChanged(genresAndTagsForSearch: GenresAndTagsForSearch) =
        mutableUiState.update {
            it.copy(
                genresAndTagsForSearch = genresAndTagsForSearch,
                genresOrTagsChanged = true,
                page = 1,
                hasNextPage = true,
                isLoading = true
            )
        }

    val media = mutableStateListOf<SearchMediaQuery.Medium>()
    val characters = mutableStateListOf<SearchCharacterQuery.Character>()
    val staff = mutableStateListOf<SearchStaffQuery.Staff>()
    val studios = mutableStateListOf<SearchStudioQuery.Studio>()
    val users = mutableStateListOf<SearchUserQuery.User>()

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
                        && old.onMyList == new.onMyList
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
                    formatIn = uiState.selectedMediaFormats.map { it.value },
                    statusIn = uiState.selectedMediaStatuses.map { it.value },
                    startYear = uiState.startYear,
                    endYear = uiState.endYear,
                    onList = uiState.onMyList,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) media.clear()
                        media.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                            genresOrTagsChanged = false,
                            mediaFormatsChanged = false,
                            mediaStatusesChanged = false,
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
                        if (it.page == 1) characters.clear()
                        characters.addAll(result.list)
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
                        if (it.page == 1) staff.clear()
                        staff.addAll(result.list)
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
                        if (it.page == 1) studios.clear()
                        studios.addAll(result.list)
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
                        if (it.page == 1) users.clear()
                        users.addAll(result.list)
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