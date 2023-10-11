package com.axiel7.anihyou.ui.screens.explore.search

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.SearchCharacterQuery
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.SearchStaffQuery
import com.axiel7.anihyou.SearchStudioQuery
import com.axiel7.anihyou.SearchUserQuery
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.SelectableGenre
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
import kotlinx.coroutines.launch
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
            genreCollection = if (initialGenre != null)
                listOf(SelectableGenre(initialGenre, true)) else emptyList(),
            tagCollection = if (initialTag != null)
                listOf(SelectableGenre(initialTag, true)) else emptyList(),
        )
    )
    override val uiState = mutableUiState.asStateFlow()

    fun setQuery(value: String) = mutableUiState.update {
        it.copy(query = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setSearchType(value: SearchType) = mutableUiState.update {
        it.copy(searchType = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setMediaSort(value: MediaSort) = mutableUiState.update {
        it.copy(mediaSort = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setMediaFormats(values: List<MediaFormatLocalizable>) = mutableUiState.update {
        it.copy(selectedMediaFormats = values, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setMediaStatuses(values: List<MediaStatusLocalizable>) = mutableUiState.update {
        it.copy(selectedMediaStatuses = values, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setYear(value: Int?) = mutableUiState.update {
        it.copy(selectedYear = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setOnMyList(value: Boolean) = mutableUiState.update {
        it.copy(onMyList = value, page = 1, hasNextPage = true, isLoading = true)
    }

    val media = mutableStateListOf<SearchMediaQuery.Medium>()
    val characters = mutableStateListOf<SearchCharacterQuery.Character>()
    val staff = mutableStateListOf<SearchStaffQuery.Staff>()
    val studios = mutableStateListOf<SearchStudioQuery.Studio>()
    val users = mutableStateListOf<SearchUserQuery.User>()

    init {
        // media search
        mutableUiState
            .filter { it.searchType.isSearchMedia && it.hasNextPage && it.query.isNotBlank() }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.query == new.query
                        && old.mediaType == new.mediaType
                        && old.mediaSort == new.mediaSort
                        && old.selectedMediaFormats == new.selectedMediaFormats
                        && old.selectedMediaStatuses == new.selectedMediaStatuses
                        && old.selectedYear == new.selectedYear
                        && old.onMyList == new.onMyList
            }
            .flatMapLatest { uiState ->
                searchRepository.searchMedia(
                    mediaType = uiState.mediaType!!,
                    query = uiState.query,
                    sort = listOf(uiState.mediaSortForSearch),
                    genreIn = uiState.selectedGenres.map { it.name },
                    tagIn = uiState.selectedTags.map { it.name },
                    formatIn = uiState.selectedMediaFormats.map { it.value },
                    statusIn = uiState.selectedMediaStatuses.map { it.value },
                    year = uiState.selectedYear,
                    onList = if (uiState.onMyList) true else null,
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

    fun getGenreTagCollection() {
        searchRepository.getGenreTagCollection()
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is DataResult.Success) {
                        val externalGenre = uiState.externalGenre

                        val externalTag = uiState.externalTag

                        uiState.copy(
                            isLoadingGenres = false,
                            genreCollection = result.data.genres.map {
                                if (it == externalGenre) it.copy(isSelected = true) else it
                            },
                            tagCollection = result.data.tags.map {
                                if (it == externalTag) it.copy(isSelected = true) else it
                            }
                        )
                    } else {
                        uiState.copy(
                            isLoadingGenres = result is DataResult.Loading
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onGenreUpdated(value: SelectableGenre) = viewModelScope.launch {
        mutableUiState.update { uiState ->
            val mutableList = uiState.genreCollection.toMutableList()
            val foundIndex = mutableList.indexOf(value)
            if (foundIndex != -1) {
                mutableList[foundIndex] = value
                uiState.copy(
                    genreCollection = mutableList.toList()
                )
            } else uiState
        }
    }

    fun onTagUpdated(value: SelectableGenre) = viewModelScope.launch {
        mutableUiState.update { uiState ->
            val mutableList = uiState.tagCollection.toMutableList()
            val foundIndex = mutableList.indexOf(value)
            if (foundIndex != -1) {
                mutableList[foundIndex] = value
                uiState.copy(
                    tagCollection = mutableList.toList()
                )
            } else uiState
        }
    }

    fun unselectAllGenresAndTags() = viewModelScope.launch {
        mutableUiState.update { uiState ->
            uiState.copy(
                genreCollection = uiState.genreCollection.map { it.copy(isSelected = false) },
                tagCollection = uiState.tagCollection.map { it.copy(isSelected = false) }
            )
        }
    }
}