package com.axiel7.anihyou.ui.screens.calendar

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.ui.common.state.PagedUiState

@Stable
data class CalendarUiState(
    val weekday: Int = 0,
    val weeklyAnime: SnapshotStateList<AiringAnimesQuery.AiringSchedule> = mutableStateListOf(),
    val onMyList: Boolean? = null,
    val selectedItem: AiringAnimesQuery.AiringSchedule? = null,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}