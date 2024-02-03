package com.axiel7.anihyou.ui.screens.staffdetails

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.StaffCharacterQuery
import com.axiel7.anihyou.StaffDetailsQuery
import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.ui.common.state.UiState

@Stable
data class StaffDetailsUiState(
    val details: StaffDetailsQuery.Staff? = null,
    val media: SnapshotStateList<Pair<Int, StaffMediaGrouped>> = mutableStateListOf(),
    val mediaOnMyList: Boolean? = null,
    val pageMedia: Int = 0,
    val hasNextPageMedia: Boolean = true,
    val isLoadingMedia: Boolean = true,
    val selectedMediaItem: Pair<Int, StaffMediaGrouped>? = null,
    val characters: SnapshotStateList<StaffCharacterQuery.Edge> = mutableStateListOf(),
    val charactersOnMyList: Boolean? = null,
    val pageCharacters: Int = 0,
    val hasNextPageCharacters: Boolean = true,
    val isLoadingCharacters: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
