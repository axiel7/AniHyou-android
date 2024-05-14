package com.axiel7.anihyou.ui.screens.profile

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.UserActivityQuery
import com.axiel7.anihyou.fragment.UserInfo
import com.axiel7.anihyou.ui.common.state.PagedUiState

@Stable
data class ProfileUiState(
    val userInfo: UserInfo? = null,
    val activities: SnapshotStateList<UserActivityQuery.Activity> = mutableStateListOf(),
    val isLoadingActivity: Boolean = true,
    val isMyProfile: Boolean,
    override val page: Int = 0,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
