package com.axiel7.anihyou.ui.screens.home.activity

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.ActivityFeedQuery
import com.axiel7.anihyou.data.repository.ActivityRepository
import com.axiel7.anihyou.data.repository.PagedResult
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
}