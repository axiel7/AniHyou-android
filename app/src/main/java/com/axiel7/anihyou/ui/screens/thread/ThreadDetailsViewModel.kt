package com.axiel7.anihyou.ui.screens.thread

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.thread.ChildComment
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.data.repository.ThreadRepository
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ThreadDetailsViewModel(
    private val threadId: Int
) : BaseViewModel() {

    var threadDetails = ThreadRepository.getThreadDetails(threadId)
        .onEach {
            if (it is DataResult.Success) isLiked = it.data.basicThreadDetails.isLiked ?: false
        }
        .dataResultStateInViewModel()
    var isLiked by mutableStateOf(false)

    var threadComments = mutableStateListOf<ChildComment>()
    var page = 1
    var hasNextPage = true

    fun getThreadComments(threadId: Int) = viewModelScope.launch(dispatcher) {
        ThreadRepository.getThreadCommentsPage(
            threadId = threadId,
            page = page
        ).collect { result ->
            isLoading = page == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                threadComments.addAll(result.data)
                hasNextPage = result.nextPage != null
                page = result.nextPage ?: page
            }
        }
    }

    fun toggleLikeThread() = viewModelScope.launch(dispatcher) {
        LikeRepository.toggleLike(
            likeableId = threadId,
            type = LikeableType.THREAD
        ).collect { result ->
            if (result is DataResult.Success && result.data) {
                isLiked = !isLiked
            }
        }
    }

    suspend fun toggleLikeComment(id: Int): Boolean {
        var success = false
        runBlocking {
            LikeRepository.toggleLike(
                likeableId = id,
                type = LikeableType.THREAD_COMMENT
            ).collect { result ->
                success = result is DataResult.Success && result.data
            }
        }
        return success
    }
}