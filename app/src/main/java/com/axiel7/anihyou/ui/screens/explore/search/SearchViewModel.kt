package com.axiel7.anihyou.ui.screens.explore.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.SelectableGenre
import com.axiel7.anihyou.data.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.data.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.data.repository.SearchRepository
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.UiStateViewModel
import com.axiel7.anihyou.ui.screens.explore.GENRE_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.MEDIA_SORT_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.MEDIA_TYPE_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.TAG_ARGUMENT
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository,
) : UiStateViewModel<SearchUiState>() {

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

    fun setQuery(value: String) = mutableUiState.update { it.copy(query = value) }

    fun setSearchType(value: SearchType) = mutableUiState.update { it.copy(searchType = value) }

    fun setMediaSort(value: MediaSort) = mutableUiState.update { it.copy(mediaSort = value) }

    fun setMediaFormats(values: List<MediaFormatLocalizable>) =
        mutableUiState.update { it.copy(selectedMediaFormats = values) }

    fun setMediaStatuses(values: List<MediaStatusLocalizable>) =
        mutableUiState.update { it.copy(selectedMediaStatuses = values) }

    fun setYear(value: Int?) = mutableUiState.update { it.copy(selectedYear = value) }

    fun setOnMyList(value: Boolean) = mutableUiState.update { it.copy(onMyList = value) }

    val searchedMedia = uiState
        .filter {
            it.performSearch
                    && it.mediaType != null
                    && it.query.isNotBlank()
        }
        .map { it.copy(performSearch = false) }
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
            ).flow
        }
        .cachedIn(viewModelScope)

    //TODO: other search types
    /*val searchedCharacters = mutableStateListOf<SearchCharacterQuery.Character>()

    private fun searchCharacter(query: String) = viewModelScope.launch(dispatcher) {
        SearchRepository.searchCharacter(query = query).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                searchedCharacters.clear()
                searchedCharacters.addAll(result.data)
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }

    val searchedStaff = mutableStateListOf<SearchStaffQuery.Staff>()

    private fun searchStaff(query: String) = viewModelScope.launch(dispatcher) {
        SearchRepository.searchStaff(query = query).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                searchedStaff.clear()
                searchedStaff.addAll(result.data)
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }

    val searchedStudios = mutableStateListOf<SearchStudioQuery.Studio>()

    private fun searchStudio(query: String) = viewModelScope.launch(dispatcher) {
        SearchRepository.searchStudio(query = query).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                searchedStudios.clear()
                searchedStudios.addAll(result.data)
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }

    val searchedUsers = mutableStateListOf<SearchUserQuery.User>()

    private fun searchUser(query: String) = viewModelScope.launch(dispatcher) {
        SearchRepository.searchUser(query = query).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                searchedUsers.clear()
                searchedUsers.addAll(result.data)
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }*/

    fun getGenreTagCollection() = viewModelScope.launch {
        searchRepository.getGenreTagCollection().collect { result ->
            result.handleDataResult { data ->
                mutableUiState.updateAndGet { uiState ->
                    val externalGenre = uiState.externalGenre

                    val externalTag = uiState.externalTag

                    uiState.copy(
                        genreCollection = data.genres.map {
                            if (it == externalGenre) it.copy(isSelected = true) else it
                        },
                        tagCollection = data.tags.map {
                            if (it == externalTag) it.copy(isSelected = true) else it
                        }
                    )
                }
            }
        }
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