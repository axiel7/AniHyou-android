package com.axiel7.anihyou.feature.addrecommendation.search

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.network.SearchMediaQuery

@Stable
data class SimpleMediaSearchUiState(
    val searchQuery: String = "",
    val searchResult: SnapshotStateList<SearchMediaQuery.Medium> = mutableStateListOf(),
    val isSearching: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}
