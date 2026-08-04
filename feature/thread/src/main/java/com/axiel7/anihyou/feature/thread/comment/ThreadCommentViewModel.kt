package com.axiel7.anihyou.feature.thread.comment

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.LikeRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ThreadCommentViewModel(
    defaultPreferencesRepository: DefaultPreferencesRepository,
    private val likeRepository: LikeRepository,
): UiStateViewModel<ThreadCommentUiState>(), ThreadCommentEvent {

    override val initialState = ThreadCommentUiState()

    override suspend fun toggleLikeComment(id: Int): Boolean {
        likeRepository.toggleThreadCommentLike(
            id = id
        ).let { result ->
            if (result is DataResult.Success && result.data != null) {
                //TODO: update parent child comment
                mutableUiState.update { it.copy(isLiked = result.data == true) }
            }
            return result is DataResult.Success && result.data == true
        }
    }

    init {
        defaultPreferencesRepository.translatorApp
            .onEach { value ->
                mutableUiState.update { it.copy(translatorApp = value) }
            }
            .launchIn(viewModelScope)
    }
}