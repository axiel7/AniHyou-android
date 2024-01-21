package com.axiel7.anihyou.ui.screens.explore.search.genretag

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.data.model.genre.SelectableGenre
import com.axiel7.anihyou.data.repository.SearchRepository
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresTagsViewModel @Inject constructor(
    searchRepository: SearchRepository
) : UiStateViewModel<GenresTagsUiState>() {

    override val initialState = GenresTagsUiState()

    fun setExternalGenre(value: SelectableGenre?) = mutableUiState.update {
        it.copy(externalGenre = value)
    }

    fun setExternalTag(value: SelectableGenre?) = mutableUiState.update {
        it.copy(externalTag = value)
    }

    // TODO: migrate to StateFlow

    var filter by mutableStateOf("")
        private set

    fun onFilterChanged(value: String) {
        filter = value
    }

    private val genres = mutableStateListOf<SelectableGenre>()
    private val tags = mutableStateListOf<SelectableGenre>()

    val displayGenres by derivedStateOf {
        if (filter.isNotBlank()) genres.filter { it.name.contains(filter, ignoreCase = true) }
        else genres
    }
    val displayTags by derivedStateOf {
        if (filter.isNotBlank()) tags.filter { it.name.contains(filter, ignoreCase = true) }
        else tags
    }

    private val selectedGenres
        get() = genres.filter { it.state == SelectableGenre.State.SELECTED }.map { it.name }

    private val excludedGenres
        get() = genres.filter { it.state == SelectableGenre.State.EXCLUDED }.map { it.name }

    private val selectedTags
        get() = tags.filter { it.state == SelectableGenre.State.SELECTED }.map { it.name }

    private val excludedTags
        get() = tags.filter { it.state == SelectableGenre.State.EXCLUDED }.map { it.name }

    val genresAndTagsForSearch
        get() = GenresAndTagsForSearch(
            genreIn = selectedGenres,
            genreNot = excludedGenres,
            tagIn = selectedTags,
            tagNot = excludedTags
        )

    fun onGenreUpdated(value: SelectableGenre) = viewModelScope.launch {
        val foundIndex = genres.indexOfFirst { it.name == value.name }
        if (foundIndex != -1) {
            genres[foundIndex] = value
        }
    }

    fun onTagUpdated(value: SelectableGenre) = viewModelScope.launch {
        val foundIndex = tags.indexOfFirst { it.name == value.name }
        if (foundIndex != -1) {
            tags[foundIndex] = value
        }
    }

    fun unselectAllGenresAndTags() = viewModelScope.launch {
        val unselectedGenres = genres.map { it.copy(state = SelectableGenre.State.NONE) }
        genres.clear()
        genres.addAll(unselectedGenres)

        val unselectedTags = tags.map { it.copy(state = SelectableGenre.State.NONE) }
        tags.clear()
        tags.addAll(unselectedTags)
    }

    init {
        searchRepository.getGenreTagCollection()
            .combine(mutableUiState, ::Pair)
            .onEach { (result, latestUiState) ->
                val externalGenre = latestUiState.externalGenre
                val externalTag = latestUiState.externalTag

                mutableUiState.update { uiState ->
                    if (result is DataResult.Success) {
                        genres.clear()
                        genres.addAll(
                            result.data.genres.map {
                                if (it == externalGenre)
                                    it.copy(state = SelectableGenre.State.SELECTED)
                                else it
                            }
                        )

                        tags.clear()
                        tags.addAll(
                            result.data.tags.map {
                                if (it == externalTag)
                                    it.copy(state = SelectableGenre.State.SELECTED)
                                else it
                            }
                        )
                        uiState.copy(
                            isLoading = false,
                        )
                    } else {
                        uiState.copy(
                            isLoading = result is DataResult.Loading
                        )
                    }
                }
            }.launchIn(viewModelScope)
    }
}