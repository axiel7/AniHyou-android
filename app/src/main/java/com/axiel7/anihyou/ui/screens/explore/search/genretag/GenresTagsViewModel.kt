package com.axiel7.anihyou.ui.screens.explore.search.genretag

import android.content.Context
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.common.indexOfFirstOrNull
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.model.genre.SelectableGenre
import com.axiel7.anihyou.data.model.genre.SelectableGenre.Companion.genreTagStringRes
import com.axiel7.anihyou.data.repository.SearchRepository
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class GenresTagsViewModel @Inject constructor(
    searchRepository: SearchRepository,
    @ApplicationContext context: Context,
) : UiStateViewModel<GenresTagsUiState>(), GenresTagsEvent {

    override val initialState = GenresTagsUiState()

    override fun setExternalGenre(value: SelectableGenre?) {
        mutableUiState.update {
            it.copy(externalGenre = value)
        }
    }

    override fun setExternalTag(value: SelectableGenre?) {
        mutableUiState.update {
            it.copy(externalTag = value)
        }
    }

    override fun onFilterChanged(value: String) {
        viewModelScope.launch {
            mutableUiState.update { it.copy(filter = value) }
        }
    }

    override fun onGenreUpdated(value: SelectableGenre) {
        viewModelScope.launch {
            mutableUiState.value.run {
                genres.indexOfFirstOrNull { it.name == value.name }?.let { index ->
                    genres[index] = value
                }
                displayGenres.indexOfFirstOrNull { it.name == value.name }?.let { index ->
                    displayGenres[index] = value
                }
            }
        }
    }

    override fun onTagUpdated(value: SelectableGenre) {
        viewModelScope.launch {
            mutableUiState.value.run {
                tags.indexOfFirstOrNull { it.name == value.name }?.let { index ->
                    tags[index] = value
                }
                displayTags.indexOfFirstOrNull { it.name == value.name }?.let { index ->
                    displayTags[index] = value
                }
            }
        }
    }

    override fun unselectAllGenresAndTags() {
        viewModelScope.launch {
            mutableUiState.value.run {
                val unselectedGenres = genres.map { it.copy(state = SelectableGenre.State.NONE) }
                genres.clear()
                genres.addAll(unselectedGenres)

                val unselectedTags = tags.map { it.copy(state = SelectableGenre.State.NONE) }
                tags.clear()
                tags.addAll(unselectedTags)
            }
        }
    }

    init {
        searchRepository.getGenreTagCollection()
            .combine(
                mutableUiState
                    .distinctUntilChanged { old, new ->
                        old.externalGenre == new.externalGenre
                                && old.externalTag == new.externalTag
                    },
                ::Pair
            )
            .onEach { (result, latestUiState) ->
                val externalGenre = latestUiState.externalGenre
                val externalTag = latestUiState.externalTag

                mutableUiState.update { uiState ->
                    if (result is DataResult.Success) {
                        uiState.genres.clear()
                        uiState.genres.addAll(
                            result.data.genres.map {
                                if (it == externalGenre)
                                    it.copy(state = SelectableGenre.State.SELECTED)
                                else it
                            }
                        )

                        uiState.tags.clear()
                        uiState.tags.addAll(
                            result.data.tags.map {
                                if (it == externalTag)
                                    it.copy(state = SelectableGenre.State.SELECTED)
                                else it
                            }
                        )
                        uiState.copy(
                            isLoading = false,
                            displayGenres = uiState.genres,
                            displayTags = uiState.tags,
                        )
                    } else {
                        uiState.copy(
                            isLoading = result is DataResult.Loading
                        )
                    }
                }
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            mutableUiState
                .distinctUntilChangedBy { it.filter }
                .debounce(200)
                .collectLatest { uiState ->
                    val genresFiltered = if (uiState.filter.isNotBlank()) {
                        uiState.genres.filter { genre ->
                            val localizedName = genre.name.genreTagStringRes()
                                ?.let { context.getString(it) } ?: genre.name
                            localizedName.contains(uiState.filter, ignoreCase = true)
                        }
                    } else {
                        uiState.genres
                    }

                    val tagsFiltered = if (uiState.filter.isNotBlank()) {
                        uiState.tags.filter { it.name.contains(uiState.filter, ignoreCase = true) }
                    } else {
                        uiState.tags
                    }

                    mutableUiState.update {
                        it.copy(
                            displayGenres = genresFiltered.toMutableStateList(),
                            displayTags = tagsFiltered.toMutableStateList()
                        )
                    }
                }
        }
    }
}