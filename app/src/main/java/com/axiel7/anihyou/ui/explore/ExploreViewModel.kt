package com.axiel7.anihyou.ui.explore

import androidx.compose.runtime.mutableStateListOf
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel

class ExploreViewModel : BaseViewModel() {

    private val perPage = 25
    var page = 1
    var hasNextPage = true

    var mediaChart = mutableStateListOf<MediaChartQuery.Medium>()

    suspend fun getMediaChart(type: MediaType, sort: MediaSort) {
        isLoading = page == 1
        val response = MediaChartQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            sort = Optional.present(listOf(sort)),
            type = Optional.present(type)
        ).tryQuery()

        response?.data?.Page?.media?.filterNotNull()?.let { mediaChart.addAll(it) }
        page = response?.data?.Page?.pageInfo?.currentPage?.plus(1) ?: page
        hasNextPage = if (page > 100 / perPage) false //limit 100
        else response?.data?.Page?.pageInfo?.hasNextPage ?: false
        isLoading = false
    }

    var animeSeasonal = mutableStateListOf<SeasonalAnimeQuery.Medium>()

    suspend fun getAnimeSeasonal(
        season: MediaSeason,
        year: Int,
        resetPage: Boolean = false
    ) {
        isLoading = true
        if (resetPage) {
            page = 1
            hasNextPage = true
            animeSeasonal.clear()
        }
        val response = SeasonalAnimeQuery(
            page = Optional.present(page),
            perPage = Optional.present(perPage),
            season = Optional.present(season),
            seasonYear = Optional.present(year),
            sort = Optional.present(listOf(MediaSort.POPULARITY_DESC))
        ).tryQuery()

        response?.data?.Page?.media?.filterNotNull()?.let { animeSeasonal.addAll(it) }
        page = response?.data?.Page?.pageInfo?.currentPage?.plus(1) ?: page
        hasNextPage = response?.data?.Page?.pageInfo?.hasNextPage ?: false
        isLoading = false
    }
}