package com.axiel7.anihyou.ui.screens.activitydetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.activity.GenericActivity
import com.axiel7.anihyou.data.model.activity.toGenericActivity
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.fragment.ActivityReplyFragment
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ActivityDetailsViewModel(
    private val activityId: Int
) : BaseViewModel() {

    var activityDetails by mutableStateOf<GenericActivity?>(null)
    val replies = mutableStateListOf<ActivityReplyFragment>()

    fun getActivityDetails() = viewModelScope.launch(dispatcher) {
        ActivityRepository.getActivityDetails(activityId = activityId).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                activityDetails =
                    result.data.onTextActivity?.toGenericActivity()
                        ?: result.data.onListActivity?.toGenericActivity()
                                ?: result.data.onMessageActivity?.toGenericActivity()
                activityDetails?.replies?.let { replies.addAll(it) }
            }
        }
    }

    fun toggleLikeActivity() = viewModelScope.launch(dispatcher) {
        LikeRepository.toggleLike(
            likeableId = activityId,
            type = LikeableType.ACTIVITY
        ).collect { result ->
            if (result is DataResult.Success) {
                activityDetails = activityDetails?.updateLikeStatus(result.data)
            }
        }
    }

    fun toggleLikeReply(id: Int) = viewModelScope.launch(dispatcher) {
        LikeRepository.toggleLike(
            likeableId = id,
            type = LikeableType.ACTIVITY_REPLY
        ).collect { result ->
            if (result is DataResult.Success) {
                val isLiked = result.data
                val foundIndex = replies.indexOfFirst { it.id == id }
                if (foundIndex != -1) {
                    val oldItem = replies[foundIndex]
                    replies[foundIndex] = oldItem.copy(
                        isLiked = isLiked,
                        likeCount = if (isLiked) oldItem.likeCount + 1
                        else oldItem.likeCount - 1
                    )
                }
            }
        }
    }
}