package com.axiel7.anihyou.ui.screens.profile.stats

import com.axiel7.anihyou.data.model.stats.LengthDistribution
import com.axiel7.anihyou.data.model.stats.OverviewStats
import com.axiel7.anihyou.data.model.stats.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.YearDistribution
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.state.UiState

data class UserStatsUiState(
    val userId: Int? = null,
    val type: UserStatType = UserStatType.OVERVIEW,
    val mediaType: MediaType = MediaType.ANIME,
    val scoreType: ScoreDistribution.Type = ScoreDistribution.Type.TITLES,
    val lengthType: LengthDistribution.Type = LengthDistribution.Type.TITLES,
    val releaseYearType: YearDistribution.Type = YearDistribution.Type.TITLES,
    val startYearType: YearDistribution.Type = YearDistribution.Type.TITLES,
    val animeOverview: OverviewStats? = null,
    val mangaOverview: OverviewStats? = null,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState<UserStatsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}