package com.axiel7.anihyou.ui.screens.profile.stats

import com.axiel7.anihyou.data.model.stats.StatDistributionType
import com.axiel7.anihyou.data.model.stats.overview.OverviewStats
import com.axiel7.anihyou.fragment.GenreStat
import com.axiel7.anihyou.fragment.StaffStat
import com.axiel7.anihyou.fragment.StudioStat
import com.axiel7.anihyou.fragment.TagStat
import com.axiel7.anihyou.fragment.VoiceActorStat
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
    val tagsType: StatDistributionType = StatDistributionType.TITLES,
    val animeTags: List<TagStat>? = null,
    val mangaTags: List<TagStat>? = null,
    val staffType: StatDistributionType = StatDistributionType.TITLES,
    val animeStaff: List<StaffStat>? = null,
    val mangaStaff: List<StaffStat>? = null,
    val voiceActorsType: StatDistributionType = StatDistributionType.TITLES,
    val voiceActors: List<VoiceActorStat>? = null,
    val studiosType: StatDistributionType = StatDistributionType.TITLES,
    val studios: List<StudioStat>? = null,
    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState<UserStatsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}