package com.axiel7.anihyou.ui.screens.profile.favorites

import com.axiel7.anihyou.ui.common.state.PagedUiState

data class UserFavoritesUiState(
    val userId: Int? = null,
    val type: FavoritesType = FavoritesType.ANIME,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState<UserFavoritesUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
    override fun setPage(value: Int) = copy(page = value)
}
