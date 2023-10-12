package com.axiel7.anihyou.ui.screens.profile.social

import com.axiel7.anihyou.ui.common.state.PagedUiState

data class UserSocialUiState(
    val userId: Int? = null,
    val type: UserSocialType = UserSocialType.FOLLOWERS,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState<UserSocialUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
    override fun setPage(value: Int) = copy(page = value)
}
