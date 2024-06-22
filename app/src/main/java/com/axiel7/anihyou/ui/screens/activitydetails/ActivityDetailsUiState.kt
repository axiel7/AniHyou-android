package com.axiel7.anihyou.ui.screens.activitydetails

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.data.model.activity.GenericActivity
import com.axiel7.anihyou.fragment.ActivityReplyFragment
import com.axiel7.anihyou.ui.common.state.UiState

@Stable
data class ActivityDetailsUiState(
    val details: GenericActivity? = null,
    val replies: SnapshotStateList<ActivityReplyFragment> = mutableStateListOf(),
    val fetchFromNetwork: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}
