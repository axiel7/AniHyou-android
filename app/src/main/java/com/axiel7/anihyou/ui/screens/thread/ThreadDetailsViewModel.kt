package com.axiel7.anihyou.ui.screens.thread

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.data.repository.ThreadRepository
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.common.navigation.NavArgument
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ThreadDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val threadRepository: ThreadRepository,
    private val likeRepository: LikeRepository,
) : PagedUiStateViewModel<ThreadDetailsUiState>(), ThreadDetailsEvent {

    private val threadId = savedStateHandle.getStateFlow<Int?>(NavArgument.ThreadId.name, null)

    override val initialState = ThreadDetailsUiState()

    override fun toggleLikeThread() {
        threadId.value?.let { threadId ->
            likeRepository.toggleLike(
                likeableId = threadId,
                type = LikeableType.THREAD
            ).onEach { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.update { it.copy(isLiked = result.data) }
                }
            }.launchIn(viewModelScope)
        }
    }

    override fun subscribeToThread(subscribe: Boolean) {
        threadId.value?.let { threadId ->
            threadRepository.subscribeToThread(threadId, subscribe)
                .onEach { result ->
                    if (result is DataResult.Success && result.data != null) {
                        mutableUiState.update { it.copy(isSubscribed = result.data) }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    override suspend fun toggleLikeComment(id: Int): Boolean {
        var liked = false
        runBlocking {
            likeRepository.toggleLike(
                likeableId = id,
                type = LikeableType.THREAD_COMMENT
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

    init {
        // details
        // TODO: also get first comments page with this call
        threadId
            .filterNotNull()
            .flatMapLatest { threadId ->
                threadRepository.getThreadDetails(threadId)
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            details = result.data,
                            isLiked = result.data?.basicThreadDetails?.isLiked ?: false,
                            isSubscribed = result.data?.basicThreadDetails?.isSubscribed ?: false
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
            .distinctUntilChangedBy { it.page }
            .combine(threadId.filterNotNull(), ::Pair)
            .flatMapLatest { (uiState, threadId) ->
                threadRepository.getThreadCommentsPage(
                    threadId = threadId,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        it.comments.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}