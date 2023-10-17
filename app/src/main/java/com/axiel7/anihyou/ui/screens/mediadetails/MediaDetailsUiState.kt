package com.axiel7.anihyou.ui.screens.mediadetails

import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.MediaReviewsQuery
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.MediaThreadsQuery
import com.axiel7.anihyou.data.model.media.MediaCharactersAndStaff
import com.axiel7.anihyou.data.model.media.MediaRelationsAndRecommendations
import com.axiel7.anihyou.data.model.stats.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.Stat
import com.axiel7.anihyou.data.model.stats.StatusDistribution
import com.axiel7.anihyou.ui.common.state.UiState

data class MediaDetailsUiState(
    val details: MediaDetailsQuery.Media? = null,
    val charactersAndStaff: MediaCharactersAndStaff? = null,
    val relationsAndRecommendations: MediaRelationsAndRecommendations? = null,

    val isSuccessStats: Boolean = false,
    val mediaStatusDistribution: List<Stat<StatusDistribution>> = emptyList(),
    val mediaScoreDistribution: List<Stat<ScoreDistribution>> = emptyList(),
    val mediaRankings: List<MediaStatsQuery.Ranking> = emptyList(),

    val isLoadingThreads: Boolean = true,
    val threads: List<MediaThreadsQuery.Thread> = emptyList(),

    val isLoadingReviews: Boolean = true,
    val reviews: List<MediaReviewsQuery.Node> = emptyList(),

    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState<MediaDetailsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)

    val studios
        get() = details?.studios?.nodes?.filterNotNull()?.filter { it.isAnimationStudio }
    val producers
        get() = details?.studios?.nodes?.filterNotNull()?.filter { !it.isAnimationStudio }

    val isNewEntry
        get() = details?.mediaListEntry == null
}
