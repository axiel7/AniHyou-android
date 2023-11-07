package com.axiel7.anihyou.ui.screens.profile.stats

import com.axiel7.anihyou.data.model.stats.StatDistributionType
import com.axiel7.anihyou.data.model.stats.overview.OverviewStats
import com.axiel7.anihyou.fragment.GenreStat
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.state.UiState

data class UserStatsUiState(
    val userId: Int? = null,
    val type: UserStatType = UserStatType.OVERVIEW,
    val mediaType: MediaType = MediaType.ANIME,
    val scoreType: StatDistributionType = StatDistributionType.TITLES,
    val lengthType: StatDistributionType = StatDistributionType.TITLES,
    val releaseYearType: StatDistributionType = StatDistributionType.TITLES,
    val startYearType: StatDistributionType = StatDistributionType.TITLES,
    val animeOverview: OverviewStats? = null,
    val mangaOverview: OverviewStats? = null,
    val genresType: StatDistributionType = StatDistributionType.TITLES,
    val animeGenres: List<GenreStat>? = null,
    val mangaGenres: List<GenreStat>? = null,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState<UserStatsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}