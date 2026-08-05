package com.axiel7.anihyou.feature.thread.publish

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.ThreadRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublishCommentViewModel(
    private val threadRepository: ThreadRepository
) : UiStateViewModel<PublishCommentUiState>(), PublishCommentEvent {

    override val initialState = PublishCommentUiState()

    override fun setPublished() {
        mutableUiState.update { it.copy(savedComment = null) }
    }

    override fun publishThreadComment(
        threadId: Int?,
        parentCommentId: Int?,
        id: Int?,
        text: String
    ) {
        viewModelScope.launch {
            threadRepository.updateThreadComment(
                threadId = threadId.takeIf { it != 0 },
                parentCommentId = parentCommentId.takeIf { it != 0 },
                id = id.takeIf { it != 0 },
                text = text
            ).let { result ->
                mutableUiState.update {
                    if (result is DataResult.Success) {
                        it.copy(
                            isLoading = false,
                            savedComment = result.data,
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
        }
    }
}