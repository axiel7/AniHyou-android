package com.axiel7.anihyou.ui.screens.explore.search.genretag

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.genre.SelectableGenre
import com.axiel7.anihyou.data.repository.SearchRepository
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresTagsViewModel @Inject constructor(
    searchRepository: SearchRepository
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
            mutableUiState.update {
                it.copy(
                    filter = value
                )
            }
        }
    }

    override fun onGenreUpdated(value: SelectableGenre) {
        viewModelScope.launch {
            mutableUiState.value.run {
                val foundIndex = genres.indexOfFirst { it.name == value.name }
                if (foundIndex != -1) {
                    genres[foundIndex] = value
                }
            }
        }
    }

    override fun onTagUpdated(value: SelectableGenre) {
        viewModelScope.launch {
            mutableUiState.value.run {
                val foundIndex = tags.indexOfFirst { it.name == value.name }
                if (foundIndex != -1) {
                    tags[foundIndex] = value
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
            .combine(mutableUiState, ::Pair)
            .onEach { (result, latestUiState) ->
                val externalGenre = latestUiState.externalGenre
                val externalTag = latestUiState.externalTag

                mutableUiState.updateAndGet { uiState ->
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