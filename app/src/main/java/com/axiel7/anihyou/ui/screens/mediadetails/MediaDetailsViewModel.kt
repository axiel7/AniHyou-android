package com.axiel7.anihyou.ui.screens.mediadetails

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.MediaDetailsQuery
import com.axiel7.anihyou.MediaReviewsQuery
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.MediaThreadsQuery
import com.axiel7.anihyou.data.model.stats.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.Stat
import com.axiel7.anihyou.data.model.stats.StatLocalizableAndColorable
import com.axiel7.anihyou.data.model.stats.StatusDistribution
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.launch

class MediaDetailsViewModel(
    private val mediaId: Int
) : BaseViewModel() {

    var mediaDetails by mutableStateOf<MediaDetailsQuery.Media?>(null)
    val studios by derivedStateOf {
        mediaDetails?.studios?.nodes?.filterNotNull()?.filter { it.isAnimationStudio }
    }
    val producers by derivedStateOf {
        mediaDetails?.studios?.nodes?.filterNotNull()?.filter { !it.isAnimationStudio }
    }

    suspend fun getDetails() = viewModelScope.launch {
        MediaRepository.getMediaDetails(mediaId).collect { uiState ->
            isLoading = uiState is UiState.Loading

            if (uiState is UiState.Success) {
                mediaDetails = uiState.data
            }
            else if (uiState is UiState.Error) {
                message = uiState.message
            }
        }
    }

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

    suspend fun toggleFavorite() = viewModelScope.launch {
        mediaDetails?.let { details ->
            FavoriteRepository.toggleFavorite(
                animeId = if (details.basicMediaDetails.type == MediaType.ANIME)
                    details.id else null,
                mangaId = if (details.basicMediaDetails.type == MediaType.MANGA)
                    details.id else null,
            ).collect { uiState ->
                if (uiState is UiState.Success) {
                    if (uiState.data) {
                        mediaDetails = details.copy(isFavourite = !details.isFavourite)
                    }
                }
                else if (uiState is UiState.Error) {
                    message = uiState.message
                }
            }
        }
    }

    val charactersAndStaff =
        MediaRepository.getMediaCharactersAndStaff(mediaId).stateInViewModel()

    val relationsAndRecommendations =
        MediaRepository.getMediaRelationsRecommendations(mediaId).stateInViewModel()

    var isLoadingStats by mutableStateOf(true)
    var isSuccessStats by mutableStateOf(false)
    var mediaStatusDistribution = mutableStateListOf<Stat<StatusDistribution>>()
    var mediaScoreDistribution = mutableStateListOf<Stat<ScoreDistribution>>()
    var mediaRankings = mutableStateListOf<MediaStatsQuery.Ranking>()

    suspend fun getMediaStats() = viewModelScope.launch {
        MediaRepository.getMediaStats(mediaId).collect { uiState ->
            isLoadingStats = uiState is UiState.Loading

            if (uiState is UiState.Success) {
                isSuccessStats = true
                mediaStatusDistribution.clear()
                uiState.data.stats?.statusDistribution?.filterNotNull()?.forEach {
                    val status = StatusDistribution.valueOf(it.status?.rawValue)
                    if (status != null) {
                        mediaStatusDistribution.add(
                            StatLocalizableAndColorable(
                                type = status,
                                value = it.amount?.toFloat() ?: 0f
                            )
                        )
                    }
                }
                mediaScoreDistribution.clear()
                uiState.data.stats?.scoreDistribution?.filterNotNull()?.forEach {
                    mediaScoreDistribution.add(
                        StatLocalizableAndColorable(
                            type = ScoreDistribution(score = it.score ?: 0),
                            value = it.amount?.toFloat() ?: 0f
                        )
                    )
                }
                mediaRankings.clear()
                uiState.data.rankings?.filterNotNull()?.let { mediaRankings.addAll(it) }
            }
            else if (uiState is UiState.Error) {
                isSuccessStats = false
                message = uiState.message
            }
        }
    }

    var isLoadingReviews by mutableStateOf(true)
    var mediaReviews = mutableStateListOf<MediaReviewsQuery.Node>()
    private var pageReviews = 1
    var hasNextPageReviews = true

    suspend fun getMediaReviews() = viewModelScope.launch {
        MediaRepository.getMediaReviews(
            mediaId = mediaId,
            page = pageReviews
        ).collect { uiState ->
            isLoadingReviews = pageReviews == 1 && uiState is UiState.Loading

            if (uiState is UiState.Success) {
                uiState.data.nodes?.filterNotNull()?.let { mediaReviews.addAll(it) }
                hasNextPageReviews = uiState.data.pageInfo?.hasNextPage ?: false
                pageReviews = uiState.data.pageInfo?.currentPage?.plus(1) ?: pageReviews++
            }
        }
    }

    var isLoadingThreads by mutableStateOf(true)
    var mediaThreads = mutableStateListOf<MediaThreadsQuery.Thread>()
    private var pageThreads = 1
    var hasNextPageThreads = true

    suspend fun getMediaThreads() = viewModelScope.launch {
        MediaRepository.getMediaThreadsPage(
            mediaId = mediaId,
            page = pageReviews
        ).collect { uiState ->
            isLoadingThreads = pageThreads == 1 && uiState is UiState.Loading

            if (uiState is UiState.Success) {
                uiState.data.threads?.filterNotNull()?.let { mediaThreads.addAll(it) }
                hasNextPageThreads = uiState.data.pageInfo?.hasNextPage ?: false
                pageThreads = uiState.data.pageInfo?.currentPage?.plus(1) ?: pageThreads++
            }
        }
    }
}