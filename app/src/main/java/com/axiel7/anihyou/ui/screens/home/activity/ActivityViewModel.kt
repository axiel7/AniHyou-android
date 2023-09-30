package com.axiel7.anihyou.ui.screens.home.activity

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.ActivityFeedQuery
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.LikeRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.type.LikeableType
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ActivityViewModel : BaseViewModel() {

    private var page = 1
    var hasNextPage = true
    val activities = mutableStateListOf<ActivityFeedQuery.Activity>()

    fun getActivityFeed() = viewModelScope.launch(dispatcher) {
        ActivityRepository.getActivityFeed(
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
                            || it.onTextActivity?.id == id
                }
                if (foundIndex != -1) {
                    val oldItem = activities[foundIndex]
                    activities[foundIndex] = oldItem.copy(
                        onTextActivity = oldItem.onTextActivity?.copy(
                            isLiked = isLiked,
                            likeCount = if (isLiked) oldItem.onTextActivity.likeCount + 1
                            else oldItem.onTextActivity.likeCount - 1
                        ),
                        onListActivity = oldItem.onListActivity?.copy(
                            listActivityFragment = oldItem.onListActivity.listActivityFragment.copy(
                                isLiked = isLiked,
                                likeCount = if (isLiked)
                                    oldItem.onListActivity.listActivityFragment.likeCount + 1
                                else oldItem.onListActivity.listActivityFragment.likeCount - 1
                            )
                        )
                    )
                }
            }
        }
    }
}