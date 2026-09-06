package com.axiel7.anihyou.feature.calendar

import androidx.compose.runtime.Stable
import com.axiel7.anihyou.core.base.state.PagedUiState
import com.axiel7.anihyou.core.network.fragment.ExploreMedia
import java.time.LocalDate
import java.time.LocalDateTime

@Stable
data class CalendarUiState(
    val onMyList: Boolean? = null,
    val selectedItem: ExploreMedia? = null,
    val day: LocalDateTime = LocalDateTime.now(),
    val weeklyAnime: MutableMap<LocalDate, List<ExploreMedia>> = mutableMapOf(),
    val fetchFromNetwork: Boolean = false,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val isLoading: Boolean = true,
    override val error: String? = null,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}