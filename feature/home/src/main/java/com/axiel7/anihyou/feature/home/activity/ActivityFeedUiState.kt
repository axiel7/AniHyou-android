package com.axiel7.anihyou.feature.home.activity

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.core.network.ActivityFeedQuery
import com.axiel7.anihyou.core.base.state.PagedUiState

@Stable
data class ActivityFeedUiState(
    val activities: SnapshotStateList<ActivityFeedQuery.Activity> = mutableStateListOf(),
    val isFollowing: Boolean = true,
    val type: ActivityTypeGrouped = ActivityTypeGrouped.ALL,
    val fetchFromNetwork: Boolean = false,
    override val page: Int = 1,
    override val hasNextPage: Boolean = false,
    override val error: String? = null,
    override val isLoading: Boolean = true
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
