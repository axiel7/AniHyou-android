package com.axiel7.anihyou.feature.explore.search.genretag

import android.content.Context
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.common.DataResult
import com.axiel7.anihyou.core.domain.repository.SearchRepository
import com.axiel7.anihyou.core.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.core.model.genre.SelectableGenre
import com.axiel7.anihyou.core.model.genre.SelectableGenre.Companion.genreTagStringRes
import com.axiel7.anihyou.core.ui.common.viewmodel.UiStateViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class GenresTagsViewModel(
    private val searchRepository: SearchRepository,
    context: Context,
) : UiStateViewModel<GenresTagsUiState>(), GenresTagsEvent {

    override val initialState = GenresTagsUiState()

    override fun setExternalGenre(value: SelectableGenre) {
        mutableUiState.update {
            it.genres.add(value)
            it.copy(
                externalGenre = value,
                genresAndTagsForSearch = it.genresAndTagsForSearch.copy(
                    genreIn = it.genresAndTagsForSearch.genreIn + value.name
                )
            )
        }
    }

    override fun setExternalTag(value: SelectableGenre) {
        mutableUiState.update {
            it.tags.add(value)
            it.copy(
                externalTag = value,
                genresAndTagsForSearch = it.genresAndTagsForSearch.copy(
                    tagIn = it.genresAndTagsForSearch.tagIn + value.name
                )
            )
        }
    }

    override fun onFilterChanged(value: String) {
        viewModelScope.launch {
            mutableUiState.update { it.copy(filter = value) }
        }
    }

    override fun onGenreUpdated(value: SelectableGenre) {
        viewModelScope.launch {
            mutableUiState.first().updateGenre(value)
        }
    }

    override suspend fun onGenreRemoved(name: String): GenresAndTagsForSearch {
        val uiState = mutableUiState.first()
        uiState.genres.find { it.name == name }?.let { genre ->
            uiState.updateGenre(genre.copy(state = SelectableGenre.State.NONE))
            val genresAndTagsForSearch = uiState.genresAndTagsForSearch()
            mutableUiState.emit(
                uiState.copy(genresAndTagsForSearch = genresAndTagsForSearch)
            )
            return genresAndTagsForSearch
        }
        return uiState.genresAndTagsForSearch
    }

    override fun onTagUpdated(value: SelectableGenre) {
        viewModelScope.launch {
            mutableUiState.first().updateTag(value)
        }
    }

    override suspend fun onTagRemoved(name: String): GenresAndTagsForSearch {
        val uiState = mutableUiState.first()
        uiState.tags.find { it.name == name }?.let { tag ->
            uiState.updateTag(tag.copy(state = SelectableGenre.State.NONE))
            val genresAndTagsForSearch = uiState.genresAndTagsForSearch()
            mutableUiState.emit(
                uiState.copy(genresAndTagsForSearch = genresAndTagsForSearch)
            )
            return genresAndTagsForSearch
        }
        return uiState.genresAndTagsForSearch
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

    override fun resetData() {
        mutableUiState.update {
            val unselectedGenres = it.genres.map { it.copy(state = SelectableGenre.State.NONE) }
            it.genres.clear()
            it.genres.addAll(unselectedGenres)

            val unselectedTags = it.tags.map { it.copy(state = SelectableGenre.State.NONE) }
            it.tags.clear()
            it.tags.addAll(unselectedTags)

            it.copy(genresAndTagsForSearch = it.genresAndTagsForSearch())
        }
    }

    override suspend fun onDismissSheet() {
        val state = mutableUiState.first()
        mutableUiState.emit(
            state.copy(genresAndTagsForSearch = state.genresAndTagsForSearch())
        )
    }

    override fun fetchGenreTagCollection() {
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
            }
            .launchIn(viewModelScope)
    }

    init {
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