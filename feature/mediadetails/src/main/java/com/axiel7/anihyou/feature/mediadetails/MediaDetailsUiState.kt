package com.axiel7.anihyou.feature.mediadetails

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.media.AnimeThemes
import com.axiel7.anihyou.core.model.media.MediaRelationsAndRecommendations
import com.axiel7.anihyou.core.model.stats.Stat
import com.axiel7.anihyou.core.model.stats.overview.ScoreDistribution
import com.axiel7.anihyou.core.model.stats.overview.StatusDistribution
import com.axiel7.anihyou.core.network.MediaDetailsQuery
import com.axiel7.anihyou.core.network.MediaFollowingQuery
import com.axiel7.anihyou.core.network.MediaReviewsQuery
import com.axiel7.anihyou.core.network.MediaStatsQuery
import com.axiel7.anihyou.core.network.MediaThreadsQuery
import com.axiel7.anihyou.core.network.fragment.CommonVoiceActor
import com.axiel7.anihyou.core.network.fragment.ListActivityFragment
import com.axiel7.anihyou.core.network.fragment.MediaCharacter
import com.axiel7.anihyou.core.network.fragment.MediaStaff
import com.axiel7.anihyou.core.base.state.UiState

@Immutable
data class MediaDetailsUiState(
    val isLoggedIn: Boolean = false,

    val details: MediaDetailsQuery.Media? = null,
    val openings: List<AnimeThemes.Theme>? = null,
    val endings: List<AnimeThemes.Theme>? = null,

    val staff: List<MediaStaff>? = null,
    val characters: List<MediaCharacter>? = null,
    val selectedCharacterVoiceActors: List<CommonVoiceActor>? = null,
    val showVoiceActorsSheet: Boolean = false,

    val relationsAndRecommendations: MediaRelationsAndRecommendations? = null,

    val isSuccessStats: Boolean = false,
    val mediaStatusDistribution: List<Stat<StatusDistribution>> = emptyList(),
    val mediaScoreDistribution: List<Stat<ScoreDistribution>> = emptyList(),
    val mediaRankings: List<MediaStatsQuery.Ranking> = emptyList(),
    val following: List<MediaFollowingQuery.MediaList> = emptyList(),

    val isLoadingThreads: Boolean = true,
    val threads: List<MediaThreadsQuery.Thread> = emptyList(),

    val isLoadingReviews: Boolean = true,
    val reviews: List<MediaReviewsQuery.Node> = emptyList(),

    val isLoadingActivity: Boolean = true,
    val activity: List<ListActivityFragment> = emptyList(),

    override val error: String? = null,
    override val isLoading: Boolean = true,
) : UiState() {

    val studios = details?.studios?.nodes?.filterNotNull()?.filter { it.isAnimationStudio }
    val producers = details?.studios?.nodes?.filterNotNull()?.filter { !it.isAnimationStudio }

    val isNewEntry = details?.mediaListEntry == null

    val hasSpoilerTags = details?.tags?.any { it?.isMediaSpoiler == true } ?: false

    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
