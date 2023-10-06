package com.axiel7.anihyou.ui.screens.explore.charts

import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.ui.common.UiState

data class MediaChartUiState(
    val chartType: ChartType,
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : UiState<MediaChartUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
