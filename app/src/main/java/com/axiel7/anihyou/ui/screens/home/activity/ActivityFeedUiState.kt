package com.axiel7.anihyou.ui.screens.home.activity

import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.ui.common.UiState

data class ActivityFeedUiState(
    val isFollowing: Boolean = true,
    val type: ActivityTypeGrouped? = null,

    override val error: String? = null,
    override val isLoading: Boolean = true
) : UiState<ActivityFeedUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
