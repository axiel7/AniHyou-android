package com.axiel7.anihyou.ui.screens.explore

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.UiStateViewModel
import kotlinx.coroutines.launch

class ExploreViewModel : UiStateViewModel() {

    private val perPage = 25
    var page = 1
    var hasNextPage = true

    var mediaChart = mutableStateListOf<MediaChartQuery.Medium>()

    fun loadMoreChart(type: ChartType) {
        when (type) {
            ChartType.TOP_ANIME ->
                getMediaChart(
                    type = MediaType.ANIME,
                    sort = listOf(MediaSort.SCORE_DESC)
                )

            ChartType.POPULAR_ANIME ->
                getMediaChart(
                    type = MediaType.ANIME,
                    sort = listOf(MediaSort.POPULARITY_DESC)
                )

            ChartType.UPCOMING_ANIME ->
                getMediaChart(
                    type = MediaType.ANIME,
                    sort = listOf(MediaSort.POPULARITY_DESC),
                    status = MediaStatus.NOT_YET_RELEASED
                )

            ChartType.AIRING_ANIME ->
                getMediaChart(
                    type = MediaType.ANIME,
                    sort = listOf(MediaSort.SCORE_DESC),
                    status = MediaStatus.RELEASING
                )

            ChartType.TOP_MANGA ->
                getMediaChart(
                    type = MediaType.MANGA,
                    sort = listOf(MediaSort.SCORE_DESC)
                )

            ChartType.POPULAR_MANGA ->
                getMediaChart(
                    type = MediaType.MANGA,
                    sort = listOf(MediaSort.POPULARITY_DESC)
                )

            ChartType.UPCOMING_MANGA ->
                getMediaChart(
                    type = MediaType.MANGA,
                    sort = listOf(MediaSort.POPULARITY_DESC),
                    status = MediaStatus.NOT_YET_RELEASED
                )

            ChartType.PUBLISHING_MANGA ->
                getMediaChart(
                    type = MediaType.MANGA,
                    sort = listOf(MediaSort.SCORE_DESC),
                    status = MediaStatus.RELEASING
                )
        }
    }

    private fun getMediaChart(
        type: MediaType,
        sort: List<MediaSort>,
        status: MediaStatus? = null,
    ) = viewModelScope.launch(dispatcher) {
        MediaRepository.getMediaChartPage(
            type = type,
            sort = sort,
            status = status,
            page = page,
            perPage = perPage
        ).collect { result ->
            isLoading = page == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                mediaChart.addAll(result.data)
                hasNextPage = page < (100 / perPage) && result.nextPage != null
                page = result.nextPage ?: page
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }

    var animeSeasonal = mutableStateListOf<SeasonalAnimeQuery.Medium>()

    fun getAnimeSeasonal(
        season: MediaSeason,
        year: Int,
        resetPage: Boolean = false
    ) = viewModelScope.launch(dispatcher) {
        if (resetPage) {
            page = 1
            hasNextPage = true
        }
        MediaRepository.getSeasonalAnimePage(
            animeSeason = AnimeSeason(year, season),
            page = page,
            perPage = perPage
        ).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                if (resetPage) animeSeasonal.clear()
                animeSeasonal.addAll(result.data)
                hasNextPage = result.nextPage != null
                page = result.nextPage ?: page
            } else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }
}