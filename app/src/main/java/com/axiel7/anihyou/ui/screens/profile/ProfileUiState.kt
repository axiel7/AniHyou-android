package com.axiel7.anihyou.ui.screens.profile

import com.axiel7.anihyou.fragment.UserInfo
import com.axiel7.anihyou.ui.common.state.PagedUiState

data class ProfileUiState(
    val userInfo: UserInfo? = null,
    val isLoadingActivity: Boolean = true,
    val isMyProfile: Boolean? = null,
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
