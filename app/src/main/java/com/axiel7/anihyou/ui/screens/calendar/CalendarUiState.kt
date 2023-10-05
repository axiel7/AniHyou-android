package com.axiel7.anihyou.ui.screens.calendar

import com.axiel7.anihyou.ui.common.UiState

data class CalendarUiState(
    val onMyList: Boolean = false,
    val weekday: Int = 0,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState<CalendarUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}