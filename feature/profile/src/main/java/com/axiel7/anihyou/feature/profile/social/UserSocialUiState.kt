package com.axiel7.anihyou.feature.profile.social

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.network.FollowersQuery
import com.axiel7.anihyou.core.network.FollowingsQuery
import com.axiel7.anihyou.core.base.state.PagedUiState

@Stable
data class UserSocialUiState(
    val userId: Int? = null,
    val type: UserSocialType = UserSocialType.FOLLOWERS,
    val followers: SnapshotStateList<FollowersQuery.Follower> = mutableStateListOf(),
    val following: SnapshotStateList<FollowingsQuery.Following> = mutableStateListOf(),
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
    override fun setPage(value: Int) = copy(page = value)
}
