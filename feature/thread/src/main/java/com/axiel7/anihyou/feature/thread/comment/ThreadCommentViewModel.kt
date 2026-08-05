package com.axiel7.anihyou.feature.thread.comment

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.LikeRepository
import com.axiel7.anihyou.core.model.thread.ChildComment.Companion.toChildComment
import com.axiel7.anihyou.core.network.fragment.CommonThreadComment
import com.axiel7.anihyou.core.ui.common.navigation.Route
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.InjectedParam

class ThreadCommentViewModel(
    @InjectedParam arguments: Route.ThreadCommentDetails,
    defaultPreferencesRepository: DefaultPreferencesRepository,
    private val likeRepository: LikeRepository,
) : UiStateViewModel<ThreadCommentUiState>(), ThreadCommentEvent {

    override val initialState = ThreadCommentUiState(
        childComment = arguments.childComment,
    )

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

    override fun onCommentPublished(comment: CommonThreadComment) {
        mutableUiState.update {
            val childComments = it.childComment.childComments?.plus(comment.toChildComment())
            it.copy(
                childComment = it.childComment.copy(childComments = childComments)
            )
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