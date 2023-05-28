package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.MediaCharactersAndStaffQuery
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.MediaRelationsAndRecommendationsQuery
import com.axiel7.anihyou.MediaReviewsQuery
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.data.model.stats.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.Stat
import com.axiel7.anihyou.data.model.stats.StatLocalizableAndColorable
import com.axiel7.anihyou.data.model.stats.StatusDistribution
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.base.BaseViewModel

class MediaDetailsViewModel : BaseViewModel() {

    var mediaDetails by mutableStateOf<MediaDetailsQuery.Media?>(null)

    suspend fun getDetails(mediaId: Int) {
        isLoading = true
        val response = MediaDetailsQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        mediaDetails = response?.data?.Media
        if (mediaDetails != null) isLoading = false
    }

    fun getStudios() = mediaDetails?.studios?.nodes?.filterNotNull()?.filter { it.isAnimationStudio }

    fun getProducers() = mediaDetails?.studios?.nodes?.filterNotNull()?.filter { !it.isAnimationStudio }

    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        if (mediaDetails?.mediaListEntry?.basicMediaListEntry != newListEntry) {
            mediaDetails = mediaDetails?.copy(
                mediaListEntry =
                if (newListEntry != null)
                    mediaDetails?.mediaListEntry?.copy(basicMediaListEntry = newListEntry)
                else null
            )
        }
    }

    var isLoadingStaffCharacter by mutableStateOf(true)
    var mediaStaff = mutableStateListOf<MediaCharactersAndStaffQuery.Edge1>()
    var mediaCharacters = mutableStateListOf<MediaCharactersAndStaffQuery.Edge>()

    suspend fun getMediaCharactersAndStaff(mediaId: Int) {
        isLoadingStaffCharacter = true
        val response = MediaCharactersAndStaffQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        mediaStaff.clear()
        response?.data?.Media?.staff?.edges?.filterNotNull()?.let { mediaStaff.addAll(it) }

        mediaCharacters.clear()
        response?.data?.Media?.characters?.edges?.filterNotNull()?.let { mediaCharacters.addAll(it) }
        isLoadingStaffCharacter = false
    }

    var isLoadingRelationsRecommendations by mutableStateOf(true)
    var mediaRelated = mutableStateListOf<MediaRelationsAndRecommendationsQuery.Edge>()
    var mediaRecommendations = mutableStateListOf<MediaRelationsAndRecommendationsQuery.Node>()

    suspend fun getMediaRelationsRecommendations(mediaId: Int) {
        isLoadingRelationsRecommendations = true
        val response = MediaRelationsAndRecommendationsQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        mediaRelated.clear()
        response?.data?.Media?.relations?.edges?.filterNotNull()?.let { mediaRelated.addAll(it) }

        mediaRecommendations.clear()
        response?.data?.Media?.recommendations?.nodes?.filterNotNull()?.let { mediaRecommendations.addAll(it) }

        isLoadingRelationsRecommendations = false
    }

    var isLoadingStats by mutableStateOf(true)
    var mediaStatusDistribution = mutableStateListOf<Stat<StatusDistribution>>()
    var mediaScoreDistribution = mutableStateListOf<Stat<ScoreDistribution>>()
    var mediaRankings = mutableStateListOf<MediaStatsQuery.Ranking>()

    suspend fun getMediaStats(mediaId: Int) {
        isLoadingStats = true
        val response = MediaStatsQuery(
            mediaId = Optional.present(mediaId)
        ).tryQuery()

        mediaStatusDistribution.clear()
        response?.data?.Media?.stats?.statusDistribution?.filterNotNull()?.forEach {
            val status = StatusDistribution.valueOf(it.status?.rawValue)
            if (status != null) {
                mediaStatusDistribution.add(
                    StatLocalizableAndColorable(type = status, value = it.amount?.toFloat() ?: 0f)
                )
            }
        }
        mediaScoreDistribution.clear()
        response?.data?.Media?.stats?.scoreDistribution?.filterNotNull()?.forEach {
            mediaScoreDistribution.add(
                StatLocalizableAndColorable(
                    type = ScoreDistribution(score = it.score ?: 0),
                    value = it.amount?.toFloat() ?: 0f
                )
            )
        }
        mediaRankings.clear()
        response?.data?.Media?.rankings?.filterNotNull()?.let { mediaRankings.addAll(it) }
        isLoadingStats = false
    }

    var isLoadingReviews by mutableStateOf(true)
    var mediaReviews = mutableStateListOf<MediaReviewsQuery.Node>()
    var pageReviews = 1
    var hasNextPageReviews = true

    suspend fun getMediaReviews(mediaId: Int) {
        isLoadingReviews = true
        val response = MediaReviewsQuery(
            mediaId = Optional.present(mediaId),
            page = Optional.present(pageReviews),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.Media?.reviews?.nodes?.filterNotNull()?.let { mediaReviews.addAll(it) }
        hasNextPageReviews = response?.data?.Media?.reviews?.pageInfo?.hasNextPage ?: false
        pageReviews = response?.data?.Media?.reviews?.pageInfo?.currentPage?.plus(1) ?: pageReviews
        isLoadingReviews = false
    }
}