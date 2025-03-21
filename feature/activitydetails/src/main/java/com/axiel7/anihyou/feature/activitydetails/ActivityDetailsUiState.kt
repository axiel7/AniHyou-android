package com.axiel7.anihyou.feature.activitydetails

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.network.fragment.ActivityReplyFragment
import com.axiel7.anihyou.core.ui.common.state.UiState
import com.axiel7.anihyou.core.model.activity.GenericActivity

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
