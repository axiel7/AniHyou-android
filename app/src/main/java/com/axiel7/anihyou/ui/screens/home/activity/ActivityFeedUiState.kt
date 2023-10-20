package com.axiel7.anihyou.ui.screens.home.activity

import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.ui.common.state.PagedUiState

data class ActivityFeedUiState(
    val isFollowing: Boolean = true,
    val type: ActivityTypeGrouped = ActivityTypeGrouped.ALL,
    val fetchFromNetwork: Boolean = false,
    override val page: Int = 1,
    override val hasNextPage: Boolean = false,
    override val error: String? = null,
    override val isLoading: Boolean = true
) : PagedUiState<ActivityFeedUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
