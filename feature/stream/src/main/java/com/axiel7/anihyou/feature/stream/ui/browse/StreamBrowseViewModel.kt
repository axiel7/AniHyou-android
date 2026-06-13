package com.axiel7.anihyou.feature.stream.ui.browse

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.feature.stream.data.model.PagedAnimeResponse
import com.axiel7.anihyou.feature.stream.data.model.SpotlightResponse
import com.axiel7.anihyou.feature.stream.data.model.StreamAnime
import com.axiel7.anihyou.feature.stream.data.repository.StreamRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StreamBrowseUiState(
    val spotlight: List<StreamAnime> = emptyList(),
    val trending: List<StreamAnime> = emptyList(),
    val popular: List<StreamAnime> = emptyList(),
    val recent: List<StreamAnime> = emptyList(),
    val searchResults: List<StreamAnime> = emptyList(),
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}

class StreamBrowseViewModel(
    private val streamRepository: StreamRepository,
) : UiStateViewModel<StreamBrowseUiState>() {

    override val initialState = StreamBrowseUiState()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true) }

            // Load all sections concurrently
            launch {
                when (val r = streamRepository.getSpotlight()) {
                    is DataResult.Success -> mutableUiState.update { it.copy(spotlight = r.data.results) }
                    is DataResult.Error -> mutableUiState.update { it.copy(error = r.message) }
                    else -> {}
                }
            }
            launch {
                when (val r = streamRepository.getTrending()) {
                    is DataResult.Success -> mutableUiState.update { it.copy(trending = r.data.results) }
                    else -> {}
                }
            }
            launch {
                when (val r = streamRepository.getPopular()) {
                    is DataResult.Success -> mutableUiState.update { it.copy(popular = r.data.results) }
                    else -> {}
                }
            }
            launch {
                when (val r = streamRepository.getRecent()) {
                    is DataResult.Success -> mutableUiState.update { it.copy(recent = r.data.results) }
                    else -> {}
                }
            }

            mutableUiState.update { it.copy(isLoading = false) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        mutableUiState.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            mutableUiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        viewModelScope.launch {
            mutableUiState.update { it.copy(isSearching = true) }
            when (val r = streamRepository.search(query)) {
                is DataResult.Success -> mutableUiState.update {
                    it.copy(searchResults = r.data.results, isSearching = false)
                }
                is DataResult.Error -> mutableUiState.update {
                    it.copy(error = r.message, isSearching = false)
                }
                else -> {}
            }
        }
    }
}
