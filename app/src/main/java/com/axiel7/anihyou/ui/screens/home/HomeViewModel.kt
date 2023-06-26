package com.axiel7.anihyou.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UnreadNotificationCountQuery
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.data.repository.BaseRepository.asStateFlow
import com.axiel7.anihyou.data.repository.MediaRepository
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
    val nowAnimeSeason = now.currentAnimeSeason()
    val nextAnimeSeason = now.nextAnimeSeason()

    val airingAnime = MediaRepository.getAiringAnime().asStateFlow(viewModelScope)

    val airingAnimeOnMyList = MediaRepository.getAiringAnimeOnMyList().asStateFlow(viewModelScope)

    val thisSeasonAnime = MediaRepository.getSeasonalAnime(nowAnimeSeason).asStateFlow(viewModelScope)

    val trendingAnime = MediaRepository.getMediaSorted(
        mediaType = MediaType.ANIME,
        sort = listOf(MediaSort.TRENDING_DESC)
    ).asStateFlow(viewModelScope)

    val nextSeasonAnime = MediaRepository.getSeasonalAnime(nowAnimeSeason).asStateFlow(viewModelScope)

    val trendingManga = MediaRepository.getMediaSorted(
        mediaType = MediaType.MANGA,
        sort = listOf(MediaSort.TRENDING_DESC)
    ).asStateFlow(viewModelScope)

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