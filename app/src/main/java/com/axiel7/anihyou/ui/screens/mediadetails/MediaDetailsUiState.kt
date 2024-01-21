package com.axiel7.anihyou.ui.screens.mediadetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.MediaReviewsQuery
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.MediaThreadsQuery
import com.axiel7.anihyou.data.model.media.MediaCharactersAndStaff
import com.axiel7.anihyou.data.model.media.MediaRelationsAndRecommendations
import com.axiel7.anihyou.data.model.stats.Stat
import com.axiel7.anihyou.data.model.stats.overview.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.overview.StatusDistribution
import com.axiel7.anihyou.ui.common.state.UiState

@Immutable
data class MediaDetailsUiState(
    val isLoggedIn: Boolean = false,

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
) : UiState() {

    val studios = details?.studios?.nodes?.filterNotNull()?.filter { it.isAnimationStudio }
    val producers = details?.studios?.nodes?.filterNotNull()?.filter { !it.isAnimationStudio }

    val isNewEntry = details?.mediaListEntry == null

    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
