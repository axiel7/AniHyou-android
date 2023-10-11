package com.axiel7.anihyou.ui.screens.thread

import com.axiel7.anihyou.ThreadDetailsQuery
import com.axiel7.anihyou.ui.common.state.PagedUiState

data class ThreadDetailsUiState(
    val details: ThreadDetailsQuery.Thread? = null,
    val isLiked: Boolean = false,
    override val page: Int = 0,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState<ThreadDetailsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
