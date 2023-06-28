package com.axiel7.anihyou.ui.screens.explore

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class ExploreViewModel : BaseViewModel() {

    private val perPage = 25
    var page = 1
    var hasNextPage = true

    var mediaChart = mutableStateListOf<MediaChartQuery.Medium>()

    suspend fun getMediaChart(type: MediaType, sort: MediaSort) = viewModelScope.launch {
        MediaRepository.getMediaChartPage(
            type = type,
            sort = listOf(sort),
            page = page,
            perPage = perPage
        ).collect { result ->
            isLoading = page == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                mediaChart.addAll(result.data)
                hasNextPage = if (page > 100 / perPage) false //limit 100
                else result.nextPage != null
                page = result.nextPage ?: page
            }
            else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }

    var animeSeasonal = mutableStateListOf<SeasonalAnimeQuery.Medium>()

    suspend fun getAnimeSeasonal(
        season: MediaSeason,
        year: Int,
        resetPage: Boolean = false
    ) = viewModelScope.launch {
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
            }
            else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }
}