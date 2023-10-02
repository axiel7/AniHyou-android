package com.axiel7.anihyou.ui.screens.home.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.ActivityFeedQuery
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.data.model.activity.updateLikeStatus
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ActivityFeedViewModel : BaseViewModel() {

    var isFollowing by mutableStateOf(true)
        private set

    fun onIsFollowingChanged(value: Boolean) {
        isFollowing = value
        refresh()
    }

    var type by mutableStateOf<ActivityTypeGrouped?>(null)
        private set

    fun onTypeChanged(value: ActivityTypeGrouped?) {
        type = value
        refresh()
    }

    private var page = 1
    var hasNextPage = true
    val activities = mutableStateListOf<ActivityFeedQuery.Activity>()

    fun getActivityFeed() = viewModelScope.launch(dispatcher) {
        ActivityRepository.getActivityFeed(
            isFollowing = isFollowing,
            type = type,
            page = page
        ).collect { result ->
            isLoading = result is PagedResult.Loading && page == 1

            if (result is PagedResult.Success) {
                activities.addAll(result.data)
                hasNextPage = result.nextPage != null
                page = result.nextPage ?: page
            }
        }
    }

    fun refresh() {
        page = 1
        hasNextPage = true
        activities.clear()
    }

    fun toggleLikeActivity(id: Int) = viewModelScope.launch {
        LikeRepository.toggleLike(
            likeableId = id,
            type = LikeableType.ACTIVITY
        ).collect { result ->
            if (result is DataResult.Success) {
                val isLiked = result.data
                val foundIndex = activities.indexOfFirst {
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
                }
            }
        }
    }
}