package com.axiel7.anihyou.ui.screens.staffdetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.StaffDetailsQuery
import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.ui.common.state.UiState

@Immutable
data class StaffDetailsUiState(
    val details: StaffDetailsQuery.Staff? = null,
    val mediaOnMyList: Boolean? = null,
    val pageMedia: Int = 0,
    val hasNextPageMedia: Boolean = true,
    val isLoadingMedia: Boolean = true,
    val selectedMediaItem: Pair<Int, StaffMediaGrouped>? = null,
    val pageCharacters: Int = 0,
    val hasNextPageCharacters: Boolean = true,
    val isLoadingCharacters: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
