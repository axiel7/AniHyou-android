package com.axiel7.anihyou.ui.screens.explore.search

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.SearchCharacterQuery
import com.axiel7.anihyou.SearchMediaQuery
import com.axiel7.anihyou.SearchStaffQuery
import com.axiel7.anihyou.SearchStudioQuery
import com.axiel7.anihyou.SearchUserQuery
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.data.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.data.repository.SearchRepository
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.UiStateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    initialMediaType: MediaType?,
    initialMediaSort: MediaSort?,
    initialGenre: String?,
    initialTag: String?,
) : UiStateViewModel() {

    private val perPage = 25

    var searchType by mutableStateOf(
        if (initialMediaType == MediaType.MANGA) SearchType.MANGA else SearchType.ANIME
    )
        private set

    fun onSearchTypeChanged(value: SearchType) {
        searchType = value
    }

    val mediaType by derivedStateOf {
        when (searchType) {
            SearchType.ANIME -> MediaType.ANIME
            SearchType.MANGA -> MediaType.MANGA
            else -> null
        }
    }

    var mediaSort by mutableStateOf(initialMediaSort ?: MediaSort.SEARCH_MATCH)
        private set

    fun onMediaSortChanged(value: MediaSort) {
        mediaSort = value
    }

    val genreCollection = if (initialGenre != null) mutableStateMapOf(initialGenre to true)
    else mutableStateMapOf()

    val tagCollection = if (initialTag != null) mutableStateMapOf(initialTag to true)
    else mutableStateMapOf()

    val selectedGenres by derivedStateOf { genreCollection.filter { it.value } }
    val selectedTags by derivedStateOf { tagCollection.filter { it.value } }

    val selectedMediaFormats = mutableStateListOf<MediaFormatLocalizable>()
    fun onMediaFormatChanged(values: List<MediaFormatLocalizable>) {
        selectedMediaFormats.clear()
        selectedMediaFormats.addAll(values)
        viewModelScope.launch(Dispatchers.IO) {
            mediaType?.let { searchMedia(mediaType = it, lastQuery, resetPage = true) }
        }
    }

    val selectedMediaStatuses = mutableStateListOf<MediaStatusLocalizable>()
    fun onMediaStatusChanged(values: List<MediaStatusLocalizable>) {
        selectedMediaStatuses.clear()
        selectedMediaStatuses.addAll(values)
        viewModelScope.launch(Dispatchers.IO) {
            mediaType?.let { searchMedia(mediaType = it, lastQuery, resetPage = true) }
        }
    }

    var selectedYear by mutableStateOf<Int?>(null)
        private set

    fun onYearChanged(value: Int?) {
        selectedYear = value
        viewModelScope.launch(Dispatchers.IO) {
            mediaType?.let { searchMedia(mediaType = it, lastQuery, resetPage = true) }
        }
    }

    var onMyList by mutableStateOf(false)
        private set

    fun onMyListChanged(value: Boolean) {
        onMyList = value
        viewModelScope.launch(Dispatchers.IO) {
            mediaType?.let { searchMedia(mediaType = it, lastQuery, resetPage = true) }
        }
    }

    private var lastQuery = ""
    fun runSearch(query: String) {
        lastQuery = query
        when (searchType) {
            SearchType.ANIME -> searchMedia(MediaType.ANIME, query, resetPage = true)
            SearchType.MANGA -> searchMedia(MediaType.MANGA, query, resetPage = true)
            SearchType.CHARACTER -> searchCharacter(query)
            SearchType.STAFF -> searchStaff(query)
            SearchType.STUDIO -> searchStudio(query)
            SearchType.USER -> searchUser(query)
        }
    }

    private var pageMedia = 1
    private var hasNextPageMedia = true
    val searchedMedia = mutableStateListOf<SearchMediaQuery.Medium>()

    fun searchMedia(
        mediaType: MediaType,
        query: String,
        resetPage: Boolean
    ) = viewModelScope.launch(dispatcher) {
        if (resetPage) pageMedia = 1

        val selectedGenres = genreCollection.filterValues { it }.keys.toList()
        val selectedTags = tagCollection.filterValues { it }.keys.toList()

        if ((selectedGenres.isNotEmpty() || selectedTags.isNotEmpty()
            || selectedMediaFormats.isNotEmpty() || selectedMediaStatuses.isNotEmpty()
            || selectedYear != null)
            && mediaSort == MediaSort.SEARCH_MATCH
        ) {
            mediaSort = MediaSort.POPULARITY_DESC
        }

        SearchRepository.searchMedia(
            mediaType = mediaType,
            query = query,
            sort = listOf(mediaSort),
            genreIn = selectedGenres,
            tagIn = selectedTags,
            formatIn = selectedMediaFormats.map { it.value },
            statusIn = selectedMediaStatuses.map { it.value },
            year = selectedYear,
            onList = onMyList,
            page = pageMedia,
            perPage = perPage,
        ).collect { result ->
            isLoading = pageMedia == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                if (resetPage) searchedMedia.clear()
                searchedMedia.addAll(result.data)
                hasNextPageMedia = result.nextPage != null
                pageMedia = result.nextPage ?: pageMedia
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }

    val searchedCharacters = mutableStateListOf<SearchCharacterQuery.Character>()

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
    }

    var isLoadingGenres by mutableStateOf(false)
        private set

    fun getGenreTagCollection() = viewModelScope.launch(dispatcher) {
        SearchRepository.getGenreTagCollection().collect { result ->
            isLoadingGenres = result is DataResult.Loading

            if (result is DataResult.Success) {
                result.data.genres.forEach {
                    genreCollection[it] = false
                }
                val externalGenre =
                    if (genreCollection.size == 1) genreCollection.firstNotNullOf { it.key }
                    else null
                externalGenre?.let { genreCollection[externalGenre] = true }

                result.data.tags.forEach {
                    tagCollection[it] = false
                }
                val externalTag =
                    if (tagCollection.size == 1) tagCollection.firstNotNullOf { it.key }
                    else null
                externalTag?.let { tagCollection[externalTag] = true }
            } else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    fun unselectAllGenresAndTags() {
        viewModelScope.launch(Dispatchers.IO) {
            genreCollection.forEach { (t, _) -> genreCollection[t] = false }
            tagCollection.forEach { (t, _) -> tagCollection[t] = false }
        }
    }
}