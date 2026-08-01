package com.axiel7.anihyou.feature.explore.search.genretag

import android.content.Context
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.SearchRepository
import com.axiel7.anihyou.core.model.genre.Genre
import com.axiel7.anihyou.core.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.core.model.genre.SelectableGenre
import com.axiel7.anihyou.core.model.genre.SelectableGenre.Companion.genreTagStringRes
import com.axiel7.anihyou.core.model.genre.Tag
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class GenresTagsViewModel(
    @InjectedParam private val externalGenre: Genre? = null,
    @InjectedParam private val externalTag: Tag? = null,
    private val searchRepository: SearchRepository,
    context: Context,
) : UiStateViewModel<GenresTagsUiState>(), GenresTagsEvent {

    override val initialState = GenresTagsUiState(
        genresAndTagsForSearch = GenresAndTagsForSearch(
            genreIn = setOfNotNull(externalGenre?.genre),
            tagIn = setOfNotNull(externalTag?.tag)
        )
    )

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

    override fun onMinTagPercentageUpdated(value: Int) {
        viewModelScope.launch {
            mutableUiState.update { uiState ->
                uiState.copy(
                    minimumTagPercentage = value,
                    genresAndTagsForSearch = uiState.genresAndTagsForSearch.copy(
                        minimumTagPercentage = value
                    )
                )
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

    override fun resetData() {
        mutableUiState.update { uiState ->
            val unselectedGenres = uiState.genres.map { it.copy(state = SelectableGenre.State.NONE) }
            uiState.genres.clear()
            uiState.genres.addAll(unselectedGenres)

            val unselectedTags = uiState.tags.map { it.copy(state = SelectableGenre.State.NONE) }
            uiState.tags.clear()
            uiState.tags.addAll(unselectedTags)

            uiState.copy(genresAndTagsForSearch = uiState.genresAndTagsForSearch())
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
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is DataResult.Success) {
                        uiState.genres.clear()
                        uiState.genres.addAll(
                            result.data.genres.map {
                                if (it.name == externalGenre?.genre)
                                    it.copy(state = SelectableGenre.State.SELECTED)
                                else it
                            }
                        )

                        uiState.tags.clear()
                        uiState.tags.addAll(
                            result.data.tags.map {
                                if (it.name == externalTag?.tag)
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
                .debounce(200.milliseconds)
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