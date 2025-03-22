package com.axiel7.anihyou.feature.thread.publish

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.domain.repository.ThreadRepository
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class PublishCommentViewModel(
    private val threadRepository: ThreadRepository
) : UiStateViewModel<PublishCommentUiState>(), PublishCommentEvent {

    override val initialState = PublishCommentUiState()

    override fun setWasPublished(value: Boolean) {
        mutableUiState.update { it.copy(wasPublished = value) }
    }

    override fun publishThreadComment(
        threadId: Int?,
        parentCommentId: Int?,
        id: Int?,
        text: String
    ) {
        threadRepository.updateThreadComment(
            threadId = threadId.takeIf { it != 0 },
            parentCommentId = parentCommentId.takeIf { it != 0 },
            id = id.takeIf { it != 0 },
            text = text
        ).onEach { result ->
            mutableUiState.update {
                if (result is DataResult.Success) {
                    it.copy(
                        isLoading = false,
                        wasPublished = result.data != null
                    )
                } else {
                    result.toUiState()
                }
            }
        }.launchIn(viewModelScope)
    }
}