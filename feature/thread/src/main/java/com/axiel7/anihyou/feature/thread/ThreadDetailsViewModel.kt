package com.axiel7.anihyou.feature.thread

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.domain.repository.LikeRepository
import com.axiel7.anihyou.core.domain.repository.ThreadRepository
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
class ThreadDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val threadRepository: ThreadRepository,
    private val likeRepository: LikeRepository,
) : PagedUiStateViewModel<ThreadDetailsUiState>(), ThreadDetailsEvent {

    private val arguments = savedStateHandle.toRoute<Routes.ThreadDetails>()

    override val initialState = ThreadDetailsUiState()

    override fun toggleLikeThread() {
        likeRepository.toggleThreadLike(
            id = arguments.id
        ).onEach { result ->
            if (result is DataResult.Success && result.data != null) {
                mutableUiState.update { it.copy(isLiked = result.data?.isLiked == true) }
            }
        }.launchIn(viewModelScope)
    }

    override fun subscribeToThread(subscribe: Boolean) {
        threadRepository.subscribeToThread(arguments.id, subscribe)
            .onEach { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.update { it.copy(isSubscribed = result.data!!) }
                }
            }
            .launchIn(viewModelScope)
    }

    override suspend fun toggleLikeComment(id: Int): Boolean {
        var liked = false
        runBlocking {
            likeRepository.toggleThreadCommentLike(
                id = id
            ).onEach { result ->
                if (result is DataResult.Success && result.data != null) {
                    //TODO: update child comment
                    //mutableUiState.update { it.copy(isLiked = result.data) }
                }
            }.collect { result ->
                liked = result is DataResult.Success && result.data == true
            }
        }
        return liked
    }

    override fun refresh() {
        mutableUiState.update { it.copy(fetchFromNetwork = true) }
    }

    init {
        // details
        threadRepository.getThreadDetails(arguments.id)
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            details = result.data,
                            isLiked = result.data?.basicThreadDetails?.isLiked == true,
                            isSubscribed = result.data?.basicThreadDetails?.isSubscribed == true
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)

        // comments
        mutableUiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && !new.fetchFromNetwork
            }
            .flatMapLatest { uiState ->
                threadRepository.getThreadCommentsPage(
                    threadId = arguments.id,
                    fetchFromNetwork = uiState.fetchFromNetwork,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.comments.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}