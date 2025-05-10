package com.axiel7.anihyou.wear.ui.screens.usermedialist

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.base.extensions.indexOfFirstOrNull
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaListRepository
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class UserMediaListViewModel(
    mediaType: MediaType,
    private val mediaListRepository: MediaListRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : PagedUiStateViewModel<UserMediaListUiState>(), UserMediaListEvent {

    override val initialState = UserMediaListUiState(mediaType = mediaType)

    private val myUserId = defaultPreferencesRepository.userId
        .filterNotNull()

    override fun refreshList() {
        mutableUiState.update {
            it.copy(
                fetchFromNetwork = true,
                page = 1,
                hasNextPage = true,
                isLoading = true
            )
        }
    }

    init {
        mutableUiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                mediaListRepository.getUserMediaList(
                    userId = myUserId.first(),
                    mediaType = uiState.mediaType,
                    statusIn = listOf(MediaListStatus.CURRENT, MediaListStatus.REPEATING),
                    sort = listOf(MediaListSort.UPDATED_TIME_DESC),
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    page = uiState.page,
                    perPage = 50,
                )
            }
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is PagedResult.Success) {
                        if (uiState.fetchFromNetwork) uiState.entries.clear()
                        uiState.entries.addAll(result.list)

                        uiState.copy(
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
                            isLoading = false,
                        )
                    }
                    else {
                        result.toUiState(
                            loadingWhen = uiState.page == 1
                                    || (uiState.entries.isEmpty() && uiState.hasNextPage)
                        ).copy(
                            hasNextPage = if (result is PagedResult.Error) false
                            else uiState.hasNextPage
                        )
                    }
                }
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            mediaListRepository.lastUpdatedEntry
                .filterNotNull()
                .collectLatest { newEntry ->
                    uiState.value.run {
                        entries.indexOfFirstOrNull { it.id == newEntry.id }
                            ?.let { index ->
                                if (newEntry.status == MediaListStatus.COMPLETED) {
                                    entries.removeAt(index)
                                } else {
                                    entries[index] =
                                        entries[index].copy(basicMediaListEntry = newEntry)
                                }
                            }
                    }
                }
        }
    }
}