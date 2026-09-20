package com.axiel7.anihyou.feature.addrecommendation.search

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.SearchRepository
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam

class SimpleMediaSearchViewModel(
    @InjectedParam private val mediaType: MediaType,
    private val searchRepository: SearchRepository,
) : UiStateViewModel<SimpleMediaSearchUiState>(), SimpleMediaSearchEvent {

    override val initialState = SimpleMediaSearchUiState()

    private var searchJob: Job? = null

    override fun setQuery(query: String) {
        mutableUiState.update { it.copy(searchQuery = query) }
    }

    override fun search() {
        searchJob?.cancel() // cancel search so two searches don't run at the same time and cause false searches
        val query = uiState.value.searchQuery
        if (query.isBlank()) {
            mutableUiState.update { it.copy(searchResult = mutableStateListOf(), isSearching = false) }
            return
        }
        mutableUiState.update { it.copy(isSearching = true) }
        searchJob = searchRepository.searchMedia(
            mediaType = mediaType,
            query = query,
            page = 1,
            perPage = 25,
        )
        .onEach { result ->
            mutableUiState.update {
                if (result is PagedResult.Success) {
                    it.searchResult.clear() // clear list in every search
                    it.searchResult.addAll(result.list)
                    it.copy(
                        isSearching = false
                    )
                } else {
                    it.copy(
                        error = (result as? PagedResult.Error)?.message,
                        isSearching = false
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}