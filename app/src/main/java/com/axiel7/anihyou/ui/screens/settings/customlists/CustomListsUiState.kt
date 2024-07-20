package com.axiel7.anihyou.ui.screens.settings.customlists

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.state.UiState

@Immutable
data class CustomListsUiState(
    val animeLists: SnapshotStateList<String> = mutableStateListOf(),
    val mangaLists: SnapshotStateList<String> = mutableStateListOf(),
    override val isLoading: Boolean = false,
    override val error: String? = null,
): UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)

    fun customLists(type: MediaType) = when (type) {
        MediaType.ANIME -> animeLists
        MediaType.MANGA -> mangaLists
        else -> null
    }
}