package com.axiel7.anihyou.ui.screens.thread.publish

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.data.repository.ThreadRepository
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PublishCommentViewModel @Inject constructor(
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