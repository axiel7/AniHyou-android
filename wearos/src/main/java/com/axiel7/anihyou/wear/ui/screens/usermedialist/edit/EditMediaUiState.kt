package com.axiel7.anihyou.wear.ui.screens.usermedialist.edit

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry

@Immutable
data class EditMediaUiState(
    val entry: CommonMediaListEntry? = null,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
