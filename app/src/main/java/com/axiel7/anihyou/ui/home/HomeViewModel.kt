package com.axiel7.anihyou.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.MediaSortedQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.type.AiringSort
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.utils.DateUtils.currentAnimeSeason
import com.axiel7.anihyou.utils.DateUtils.nextAnimeSeason
import java.time.LocalDateTime

class HomeViewModel : BaseViewModel() {

    private val now = LocalDateTime.now()
    var nowAnimeSeason by mutableStateOf(now.currentAnimeSeason())
    var nextAnimeSeason = now.nextAnimeSeason()

    val airingAnime = mutableStateListOf<AiringAnimesQuery.AiringSchedule>()
    var isLoadingAiring by mutableStateOf(false)

    suspend fun getAiringAnime() {
        isLoadingAiring = true
        val todayTimestamp = System.currentTimeMillis() / 1000

        val response = AiringAnimesQuery(
            page = Optional.present(1),
            perPage = Optional.present(10),
            sort = Optional.present(listOf(AiringSort.TIME)),
            airingAtGreater = Optional.present(todayTimestamp.toInt())
        ).tryQuery()

        airingAnime.clear()
        response?.data?.Page?.airingSchedules?.filterNotNull()?.let { airingAnime.addAll(it) }
        isLoadingAiring = false
    }

    val thisSeasonAnime = mutableStateListOf<SeasonalAnimeQuery.Medium>()
    var isLoadingSeasonAnime by mutableStateOf(false)

    suspend fun getThisSeasonAnime() {
        isLoadingSeasonAnime = true
        val response = SeasonalAnimeQuery(
            page = Optional.present(1),
            perPage = Optional.present(10),
            season = Optional.present(nowAnimeSeason.season),
            seasonYear = Optional.present(nowAnimeSeason.year),
            sort = Optional.present(listOf(MediaSort.POPULARITY_DESC))
        ).tryQuery()

        thisSeasonAnime.clear()
        response?.data?.Page?.media?.filterNotNull()?.let { thisSeasonAnime.addAll(it) }
        isLoadingSeasonAnime = false
    }

    val trendingAnime = mutableStateListOf<MediaSortedQuery.Medium>()
    var isLoadingTrendingAnime by mutableStateOf(false)

    suspend fun getTrendingAnime() {
        isLoadingTrendingAnime = true
        val response = MediaSortedQuery(
            page = Optional.present(1),
            perPage = Optional.present(10),
            type = Optional.present(MediaType.ANIME),
            sort = Optional.present(listOf(MediaSort.TRENDING_DESC))
        ).tryQuery()

        trendingAnime.clear()
        response?.data?.Page?.media?.filterNotNull()?.let { trendingAnime.addAll(it) }
        isLoadingTrendingAnime = false
    }

    val nextSeasonAnime = mutableStateListOf<SeasonalAnimeQuery.Medium>()
    var isLoadingNextSeason by mutableStateOf(false)

    suspend fun getNextSeasonAnime() {
        isLoadingNextSeason = true
        val response = SeasonalAnimeQuery(
            page = Optional.present(1),
            perPage = Optional.present(10),
            season = Optional.present(nextAnimeSeason.season),
            seasonYear = Optional.present(nextAnimeSeason.year),
            sort = Optional.present(listOf(MediaSort.POPULARITY_DESC))
        ).tryQuery()

        nextSeasonAnime.clear()
        response?.data?.Page?.media?.filterNotNull()?.let { nextSeasonAnime.addAll(it) }
        isLoadingNextSeason = false
    }

    val trendingManga = mutableStateListOf<MediaSortedQuery.Medium>()
    var isLoadingTrendingManga by mutableStateOf(false)

    suspend fun getTrendingManga() {
        isLoadingTrendingManga = true
        val response = MediaSortedQuery(
            page = Optional.present(1),
            perPage = Optional.present(10),
            type = Optional.present(MediaType.MANGA),
            sort = Optional.present(listOf(MediaSort.TRENDING_DESC))
        ).tryQuery()

        trendingManga.clear()
        response?.data?.Page?.media?.filterNotNull()?.let { trendingManga.addAll(it) }
        isLoadingTrendingManga = false
    }
}