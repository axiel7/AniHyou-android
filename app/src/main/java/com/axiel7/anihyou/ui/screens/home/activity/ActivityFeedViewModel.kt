package com.axiel7.anihyou.ui.screens.home.activity

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.common.UiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityFeedViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val likeRepository: LikeRepository,
) : UiStateViewModel<ActivityFeedUiState>() {

    override val mutableUiState = MutableStateFlow(ActivityFeedUiState())
    override val uiState = mutableUiState.asStateFlow()

    fun setIsFollowing(value: Boolean) = mutableUiState.update { it.copy(isLoading = value) }

    fun setType(value: ActivityTypeGrouped?) = mutableUiState.update { it.copy(type = value) }

    private val refreshCache = MutableStateFlow(false)
    fun setRefreshCache(value: Boolean) {
        refreshCache.update { value }
        mutableUiState.update { it }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val activities = uiState
        .flatMapLatest {
            activityRepository.getActivityFeed(
                isFollowing = it.isFollowing,
                type = it.type,
                refreshCache = refreshCache.value,
            )
        }
        .onCompletion {
            if (refreshCache.value) setRefreshCache(false)
        }
        .cachedIn(viewModelScope)

    fun toggleLikeActivity(id: Int) = viewModelScope.launch {
        likeRepository.toggleLike(
            likeableId = id,
            type = LikeableType.ACTIVITY
        ).collect { result ->
            result.handleDataResult { data ->
                /*val foundIndex = activities.indexOfFirst {
                    it.onListActivity?.listActivityFragment?.id == id
                            || it.onTextActivity?.textActivityFragment?.id == id
                }
                if (foundIndex != -1) {
                    val oldItem = activities[foundIndex]
                    activities[foundIndex] = oldItem.copy(
                        onTextActivity = oldItem.onTextActivity?.copy(
                            textActivityFragment = oldItem.onTextActivity.textActivityFragment
                                .updateLikeStatus(isLiked)
                        ),
                        onListActivity = oldItem.onListActivity?.copy(
                            listActivityFragment = oldItem.onListActivity.listActivityFragment
                                .updateLikeStatus(isLiked)
                        )
                    )
                }*/
                null
            }
        }
    }
}