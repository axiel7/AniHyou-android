package com.axiel7.anihyou.ui.screens.explore.charts

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.ui.common.state.PagedUiState

@Stable
data class MediaChartUiState(
    val chartType: ChartType? = null,
    val media: SnapshotStateList<MediaChartQuery.Medium> = mutableStateListOf(),
    val selectedItem: MediaChartQuery.Medium? = null,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val isLoading: Boolean = true,
    override val error: String? = null,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)

    companion object {
        const val PER_PAGE = 25
    }
}
