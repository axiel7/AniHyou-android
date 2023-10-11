package com.axiel7.anihyou.ui.screens.explore.charts

import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.ui.common.state.PagedUiState

data class MediaChartUiState(
    val chartType: ChartType? = null,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val isLoading: Boolean = true,
    override val error: String? = null,
) : PagedUiState<MediaChartUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
