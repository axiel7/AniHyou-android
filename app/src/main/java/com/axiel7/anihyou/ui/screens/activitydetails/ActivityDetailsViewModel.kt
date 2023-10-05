package com.axiel7.anihyou.ui.screens.activitydetails

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.activity.toGenericActivity
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.fragment.ActivityReplyFragment
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.common.UiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    activityRepository: ActivityRepository,
    private val likeRepository: LikeRepository,
) : UiStateViewModel<ActivityDetailsUiState>() {

    private val activityId: Int = savedStateHandle[ACTIVITY_ID_ARGUMENT.removeFirstAndLast()]!!

    override val mutableUiState = MutableStateFlow(ActivityDetailsUiState())
    override val uiState = mutableUiState.asStateFlow()

    val replies = mutableStateListOf<ActivityReplyFragment>()

    init {
        activityRepository.getActivityDetails(activityId = activityId)
            .onEach { result ->
                result.handleDataResult { data ->
                    mutableUiState.updateAndGet {
                        it.copy(
                            details = data?.onTextActivity?.toGenericActivity()
                                ?: data?.onListActivity?.toGenericActivity()
                                ?: data?.onMessageActivity?.toGenericActivity()
                        )
                    }.also {
                        replies.addAll(it.details?.replies.orEmpty())
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleLikeActivity() = viewModelScope.launch {
        likeRepository.toggleLike(
            likeableId = activityId,
            type = LikeableType.ACTIVITY
        ).collect { result ->
            result.handleDataResult { data ->
                if (data != null) {
                    mutableUiState.updateAndGet {
                        it.copy(details = it.details?.updateLikeStatus(data))
                    }
                } else {
                    mutableUiState.updateAndGet { it.setError("Like failed") }
                }
            }
        }
    }

    fun toggleLikeReply(id: Int) = viewModelScope.launch {
        likeRepository.toggleLike(
            likeableId = id,
            type = LikeableType.ACTIVITY_REPLY
        ).collect { result ->
            result.handleDataResult { data ->
                if (data != null) {
                    val foundIndex = replies.indexOfFirst { it.id == id }
                    if (foundIndex != -1) {
                        val oldItem = replies[foundIndex]
                        replies[foundIndex] = oldItem.copy(
                            isLiked = data,
                            likeCount = if (data) oldItem.likeCount + 1
                            else oldItem.likeCount - 1
                        )
                    }
                }
                null
            }
        }
    }
}