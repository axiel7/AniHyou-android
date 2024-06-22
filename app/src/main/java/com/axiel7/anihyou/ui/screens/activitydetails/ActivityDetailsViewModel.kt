package com.axiel7.anihyou.ui.screens.activitydetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.activity.toGenericActivity
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.common.viewmodel.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    activityRepository: ActivityRepository,
    private val likeRepository: LikeRepository,
) : UiStateViewModel<ActivityDetailsUiState>(), ActivityDetailsEvent {

    val arguments = savedStateHandle.toRoute<ActivityDetails>()

    override val initialState = ActivityDetailsUiState()

    override fun toggleLikeActivity() {
        viewModelScope.launch {
            likeRepository.toggleLike(
                likeableId = arguments.id,
                type = LikeableType.ACTIVITY
            ).collect { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.update {
                        it.copy(
                            details = it.details?.updateLikeStatus(result.data)
                        )
                    }
                } else if (result !is DataResult.Loading) {
                    mutableUiState.update {
                        it.copy(
                            error = "Like failed",
                        )
                    }
                }
            }
        }
    }

    override fun toggleLikeReply(id: Int) {
        viewModelScope.launch {
            likeRepository.toggleLike(
                likeableId = id,
                type = LikeableType.ACTIVITY_REPLY
            ).collect { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.value.run {
                        val foundIndex = replies.indexOfFirst { it.id == id }
                        if (foundIndex != -1) {
                            val oldItem = replies[foundIndex]
                            replies[foundIndex] = oldItem.copy(
                                isLiked = result.data,
                                likeCount = if (result.data) oldItem.likeCount + 1
                                else oldItem.likeCount - 1
                            )
                        }
                    }
                } else if (result !is DataResult.Loading) {
                    mutableUiState.update {
                        it.copy(
                            error = "Like failed",
                        )
                    }
                }
            }
        }
    }

    override fun refresh() {
        mutableUiState.update { it.copy(fetchFromNetwork = true) }
    }

    init {
        mutableUiState
            .distinctUntilChanged { _, new -> !new.fetchFromNetwork }
            .flatMapLatest { uiState ->
                activityRepository.getActivityDetails(
                    activityId = arguments.id,
                    fetchFromNetwork = uiState.fetchFromNetwork
                )
            }
            .onEach { result ->
                if (result is DataResult.Success) {
                    mutableUiState.updateAndGet {
                        it.copy(
                            isLoading = false,
                            fetchFromNetwork = false,
                            details = result.data?.onTextActivity?.toGenericActivity()
                                ?: result.data?.onListActivity?.toGenericActivity()
                                ?: result.data?.onMessageActivity?.toGenericActivity()
                        )
                    }.also {
                        it.replies.addAll(it.details?.replies.orEmpty())
                    }
                } else {
                    mutableUiState.update { result.toUiState() }
                }
            }
            .launchIn(viewModelScope)
    }
}