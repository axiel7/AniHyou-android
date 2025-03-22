package com.axiel7.anihyou.feature.home.discover

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.network.AiringAnimesQuery
import com.axiel7.anihyou.core.network.AiringOnMyListQuery
import com.axiel7.anihyou.core.network.MediaSortedQuery
import com.axiel7.anihyou.core.network.SeasonalAnimeQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.base.state.UiState

@Stable
data class DiscoverUiState(
    val infos: SnapshotStateList<DiscoverInfo>,
    val airingAnime: SnapshotStateList<AiringAnimesQuery.AiringSchedule> = mutableStateListOf(),
    val airingAnimeOnMyList: SnapshotStateList<AiringOnMyListQuery.Medium> = mutableStateListOf(),
    val thisSeasonAnime: SnapshotStateList<SeasonalAnimeQuery.Medium> = mutableStateListOf(),
    val trendingAnime: SnapshotStateList<MediaSortedQuery.Medium> = mutableStateListOf(),
    val nextSeasonAnime: SnapshotStateList<SeasonalAnimeQuery.Medium> = mutableStateListOf(),
    val trendingManga: SnapshotStateList<MediaSortedQuery.Medium> = mutableStateListOf(),
    val newlyAnime: SnapshotStateList<MediaSortedQuery.Medium> = mutableStateListOf(),
    val newlyManga: SnapshotStateList<MediaSortedQuery.Medium> = mutableStateListOf(),
    val nowAnimeSeason: AnimeSeason,
    val nextAnimeSeason: AnimeSeason,
    val airingOnMyList: Boolean? = null,
    val selectedMediaDetails: BasicMediaDetails? = null,
    val selectedMediaListEntry: BasicMediaListEntry? = null,
    val isLoadingAiring: Boolean = true,
    val isLoadingThisSeason: Boolean = true,
    val isLoadingTrendingAnime: Boolean = true,
    val isLoadingNextSeason: Boolean = true,
    val isLoadingTrendingManga: Boolean = true,
    val isLoadingNewlyAnime: Boolean = true,
    val isLoadingNewlyManga: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
