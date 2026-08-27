package com.axiel7.anihyou.feature.explore.manga

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.ExploreMedia

@Stable
data class MangaExploreUiState(
    val infos: SnapshotStateList<MangaDiscoverInfo>,
    val trendingManga: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val popularManga: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val popularManhwa: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val newlyManga: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val isLoadingTrendingManga: Boolean = true,
    val isLoadingPopularManga: Boolean = true,
    val isLoadingPopularManhwa: Boolean = true,
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

    val allLists
        get() = listOf(trendingManga, newlyManga, popularManga, popularManhwa)
}