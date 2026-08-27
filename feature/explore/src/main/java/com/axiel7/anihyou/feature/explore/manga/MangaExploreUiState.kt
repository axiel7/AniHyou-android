package com.axiel7.anihyou.feature.explore.manga

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.network.MediaSortedQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry

@Stable
data class MangaExploreUiState(
    val infos: SnapshotStateList<MangaDiscoverInfo>,
    val trendingManga: SnapshotStateList<MediaSortedQuery.Medium> = mutableStateListOf(),
    val newlyManga: SnapshotStateList<MediaSortedQuery.Medium> = mutableStateListOf(),
    val isLoadingTrendingManga: Boolean = true,
    val isLoadingNewlyManga: Boolean = true,
    val displayAdult: Boolean = false,
    val selectedMediaDetails: BasicMediaDetails? = null,
    val selectedMediaListEntry: BasicMediaListEntry? = null,
    override val isLoading: Boolean = false,
    override val error: String? = null
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)

    val isAdult = displayAdult.takeIf { !it }
}