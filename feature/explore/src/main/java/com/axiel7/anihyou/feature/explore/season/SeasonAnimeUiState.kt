package com.axiel7.anihyou.feature.explore.season

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.base.state.PagedUiState
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.network.fragment.ExploreMedia
import com.axiel7.anihyou.core.network.type.MediaSort

@Stable
data class SeasonAnimeUiState(
    val season: AnimeSeason? = null,
    val sort: MediaSort = MediaSort.POPULARITY_DESC,
    val animeSeasonal: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val selectedItem: ExploreMedia? = null,
    val listStyle: ListStyle = ListStyle.GRID,
    val displayAdult: Boolean = false,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val isLoading: Boolean = true,
    override val error: String? = null,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)

    val isAdult = displayAdult.takeIf { !it }
}
