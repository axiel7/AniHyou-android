package com.axiel7.anihyou.feature.thread

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.LikeRepository
import com.axiel7.anihyou.core.domain.repository.ThreadRepository
import com.axiel7.anihyou.core.ui.common.navigation.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

@OptIn(ExperimentalCoroutinesApi::class)
class ThreadDetailsViewModel(
    @InjectedParam private val arguments: Route.ThreadDetails,
    defaultPreferencesRepository: DefaultPreferencesRepository,
    private val threadRepository: ThreadRepository,
    private val likeRepository: LikeRepository,
) : PagedUiStateViewModel<ThreadDetailsUiState>(), ThreadDetailsEvent {

    override val initialState = ThreadDetailsUiState()

    override fun toggleLikeThread() {
        viewModelScope.launch {
            likeRepository.toggleThreadLike(
                id = arguments.id
            ).let { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.update { it.copy(isLiked = result.data?.isLiked == true) }
                }
            }
        }
    }

    override fun subscribeToThread(subscribe: Boolean) {
        viewModelScope.launch {
            threadRepository.subscribeToThread(arguments.id, subscribe).let { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.update { it.copy(isSubscribed = result.data!!) }
                }
            }
        }
    }

    override suspend fun toggleLikeComment(id: Int): Boolean {
        likeRepository.toggleThreadCommentLike(
            id = id
        ).let { result ->
            if (result is DataResult.Success && result.data != null) {
                //TODO: update child comment
                //mutableUiState.update { it.copy(isLiked = result.data) }
            }
            return result is DataResult.Success && result.data == true
        }
    }

    override fun refresh() {
        mutableUiState.update { it.copy(page = 1, fetchFromNetwork = true) }
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
                        if (it.page == 1) it.comments.clear()
                        it.comments.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage,
                            fetchFromNetwork = false,
                        )
                    } else {
                        result.toUiState().copy(hasNextPage = false)
                    }
                }
            }
            .launchIn(viewModelScope)

        defaultPreferencesRepository.translatorApp
            .onEach { value ->
                mutableUiState.update { it.copy(translatorApp = value) }
            }
            .launchIn(viewModelScope)
    }
}