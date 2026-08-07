package com.axiel7.anihyou.feature.settings.priority_colors

import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.core.base.state.UiState

data class PriorityColorUiState(
    val lowPriorityColor: Color = Color.Green,
    val mediumPriorityColor: Color = Color.Yellow,
    val highPriorityColor: Color = Color.Red,
    override val isLoading: Boolean = false,
    override val error: String? = null
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
