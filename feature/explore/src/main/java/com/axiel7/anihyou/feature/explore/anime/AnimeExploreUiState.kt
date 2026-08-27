package com.axiel7.anihyou.feature.explore.anime

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.model.media.currentAnimeSeason
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.ExploreMedia
import java.time.LocalDateTime

@Stable
data class AnimeExploreUiState(
    val currentSeason: AnimeSeason = LocalDateTime.now().currentAnimeSeason(),
    val infos: SnapshotStateList<AnimeDiscoverInfo>,
    val airingAnime: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val airingAnimeOnMyList: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val thisSeasonAnime: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val trendingAnime: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val nextSeasonAnime: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val popularAnime: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val newlyAnime: SnapshotStateList<ExploreMedia> = mutableStateListOf(),
    val nowAnimeSeason: AnimeSeason,
    val nextAnimeSeason: AnimeSeason,
    val airingOnMyList: Boolean? = null,
    val displayAdult: Boolean = false,
    val selectedMediaDetails: BasicMediaDetails? = null,
    val selectedMediaListEntry: BasicMediaListEntry? = null,
    val isLoadingAiring: Boolean = true,
    val isLoadingThisSeason: Boolean = true,
    val isLoadingTrendingAnime: Boolean = true,
    val isLoadingNextSeason: Boolean = true,
    val isLoadingPopularAnime: Boolean = true,
    val isLoadingNewlyAnime: Boolean = true,
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : UiState() {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)

    val isAdult = displayAdult.takeIf { !it }

    val allLists
        get() = listOf(
            airingAnimeOnMyList,
            airingAnime,
            thisSeasonAnime,
            trendingAnime,
            nextSeasonAnime,
            popularAnime,
            newlyAnime,
        )
}