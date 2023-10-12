package com.axiel7.anihyou.ui.screens.profile.stats

import com.axiel7.anihyou.UserStatsAnimeOverviewQuery
import com.axiel7.anihyou.UserStatsMangaOverviewQuery
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.state.UiState

data class UserStatsUiState(
    val userId: Int? = null,
    val type: UserStatType = UserStatType.OVERVIEW,
    val mediaType: MediaType = MediaType.ANIME,
    val scoreCountType: ScoreStatCountType = ScoreStatCountType.TITLES,
    val animeOverview: UserStatsAnimeOverviewQuery.Anime? = null,
    val mangaOverview: UserStatsMangaOverviewQuery.Manga? = null,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState<UserStatsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}