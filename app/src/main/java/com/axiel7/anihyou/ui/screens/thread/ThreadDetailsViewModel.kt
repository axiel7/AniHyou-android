package com.axiel7.anihyou.ui.screens.thread

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.thread.ChildComment
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.data.repository.ThreadRepository
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    threadRepository: ThreadRepository,
    private val likeRepository: LikeRepository,
) : PagedUiStateViewModel<ThreadDetailsUiState>() {

    val threadId =
        savedStateHandle.getStateFlow<Int?>(THREAD_ID_ARGUMENT.removeFirstAndLast(), null)

    override val mutableUiState = MutableStateFlow(ThreadDetailsUiState())
    override val uiState = mutableUiState.asStateFlow()

    val threadComments = mutableStateListOf<ChildComment>()

    fun toggleLikeThread() {
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

    suspend fun toggleLikeComment(id: Int): Boolean {
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
                            isLiked = result.data?.basicThreadDetails?.isLiked ?: false
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
                if (result is PagedResult.Success) {
                    threadComments.addAll(result.list)
                } else {
                    result.toUiState()
                }
            }
            .launchIn(viewModelScope)
    }
}