package com.axiel7.anihyou.ui.screens.studiodetails

import com.axiel7.anihyou.StudioDetailsQuery
import com.axiel7.anihyou.ui.common.state.PagedUiState

data class StudioDetailsUiState(
    val details: StudioDetailsQuery.Studio? = null,
    override val page: Int = 1,
    override val hasNextPage: Boolean = false,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState<StudioDetailsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
