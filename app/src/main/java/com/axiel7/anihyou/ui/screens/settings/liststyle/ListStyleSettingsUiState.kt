package com.axiel7.anihyou.ui.screens.settings.liststyle

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.state.UiState

@Immutable
data class ListStyleSettingsUiState(
    val animeCurrentListStyle: ListStyle = ListStyle.STANDARD,
    val animePlanningListStyle: ListStyle = ListStyle.STANDARD,
    val animeCompletedListStyle: ListStyle = ListStyle.STANDARD,
    val animeDroppedListStyle: ListStyle = ListStyle.STANDARD,
    val animePausedListStyle: ListStyle = ListStyle.STANDARD,
    val animeRepeatingListStyle: ListStyle = ListStyle.STANDARD,
    val mangaCurrentListStyle: ListStyle = ListStyle.STANDARD,
    val mangaPlanningListStyle: ListStyle = ListStyle.STANDARD,
    val mangaCompletedListStyle: ListStyle = ListStyle.STANDARD,
    val mangaDroppedListStyle: ListStyle = ListStyle.STANDARD,
    val mangaPausedListStyle: ListStyle = ListStyle.STANDARD,
    val mangaRepeatingListStyle: ListStyle = ListStyle.STANDARD,
    override val error: String? = null,
    override val isLoading: Boolean = false
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
