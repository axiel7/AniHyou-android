package com.axiel7.anihyou.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.AiringOnMyListQuery
import com.axiel7.anihyou.App
import com.axiel7.anihyou.MediaSortedQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.UnreadNotificationCountQuery
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.type.AiringSort
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.utils.DateUtils.currentAnimeSeason
import com.axiel7.anihyou.utils.DateUtils.nextAnimeSeason
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeViewModel : BaseViewModel() {

    val infos = mutableStateListOf(HomeInfo.AIRING, HomeInfo.THIS_SEASON, HomeInfo.TRENDING_ANIME)

    fun addNextInfo() {
        if (infos.size < HomeInfo.values().size)
            infos.add(HomeInfo.values()[infos.size])
    }

    private val now = LocalDateTime.now()
    var nowAnimeSeason by mutableStateOf(now.currentAnimeSeason())
    var nextAnimeSeason = now.nextAnimeSeason()

    val airingAnime = mutableStateListOf<AiringAnimesQuery.AiringSchedule>()
    var isLoadingAiring by mutableStateOf(true)

    suspend fun getAiringAnime() {
        viewModelScope.launch {
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
    }

    val airingAnimeOnMyList = mutableStateListOf<AiringOnMyListQuery.Medium>()

    suspend fun getAiringAnimeOnMyList() {
        viewModelScope.launch {
            isLoadingAiring = true
            val response = AiringOnMyListQuery(
                page = Optional.present(1),
                perPage = Optional.present(25)
            ).tryQuery()

            airingAnime.clear()
            response?.data?.Page?.media?.filterNotNull()
                ?.filter { it.nextAiringEpisode != null }
                ?.sortedBy { it.nextAiringEpisode?.timeUntilAiring }
                ?.let { airingAnimeOnMyList.addAll(it) }
            isLoadingAiring = false
        }
    }

    val thisSeasonAnime = mutableStateListOf<SeasonalAnimeQuery.Medium>()
    var isLoadingThisSeason by mutableStateOf(true)

    suspend fun getThisSeasonAnime() {
        viewModelScope.launch {
            isLoadingThisSeason = true
            val response = SeasonalAnimeQuery(
                page = Optional.present(1),
                perPage = Optional.present(10),
                season = Optional.present(nowAnimeSeason.season),
                seasonYear = Optional.present(nowAnimeSeason.year),
                sort = Optional.present(listOf(MediaSort.POPULARITY_DESC))
            ).tryQuery()

            thisSeasonAnime.clear()
            response?.data?.Page?.media?.filterNotNull()?.let { thisSeasonAnime.addAll(it) }
            isLoadingThisSeason = false
        }
    }

    val trendingAnime = mutableStateListOf<MediaSortedQuery.Medium>()
    var isLoadingTrendingAnime by mutableStateOf(true)

    suspend fun getTrendingAnime() {
        viewModelScope.launch {
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
    }

    val nextSeasonAnime = mutableStateListOf<SeasonalAnimeQuery.Medium>()
    var isLoadingNextSeason by mutableStateOf(true)

    suspend fun getNextSeasonAnime() {
        viewModelScope.launch {
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
    }

    val trendingManga = mutableStateListOf<MediaSortedQuery.Medium>()
    var isLoadingTrendingManga by mutableStateOf(true)

    suspend fun getTrendingManga() {
        viewModelScope.launch {
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

    var unreadNotificationCount by mutableIntStateOf(0)

    suspend fun getUnreadNotificationCount() {
        viewModelScope.launch {
            val accessToken = App.dataStore.data.first()[ACCESS_TOKEN_PREFERENCE_KEY]
            if (accessToken != null) {
                val response = UnreadNotificationCountQuery().tryQuery()
                unreadNotificationCount = response?.data?.Viewer?.unreadNotificationCount ?: 0
            }
        }
    }
}