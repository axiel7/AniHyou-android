package com.axiel7.anihyou.feature.explore.recommendations

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.base.state.PagedUiState
import com.axiel7.anihyou.core.network.MediaRecommendationsQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.type.RecommendationSort

@Stable
data class RecommendationsUiState(
    val recommendations: SnapshotStateList<MediaRecommendationsQuery.Recommendation> = mutableStateListOf(),
    val onMyList: Boolean = false,
    val blurAdult: Boolean = false,
    val sort: RecommendationSort = RecommendationSort.ID_DESC,
    val selectedMediaDetails: BasicMediaDetails? = null,
    val selectedMediaListEntry: BasicMediaListEntry? = null,
    val fetchFromNetwork: Boolean = false,
    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val isLoading: Boolean = true,
    override val error: String? = null,
) : PagedUiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}