package com.axiel7.anihyou.ui.screens.thread

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.thread.ChildComment
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.data.repository.ThreadRepository
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ThreadDetailsViewModel(
    threadId: Int
) : BaseViewModel() {

    var threadDetails = ThreadRepository.getThreadDetails(threadId).dataResultStateInViewModel()

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
}