package com.axiel7.anihyou.ui.screens.home.discover

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.utils.DateUtils.currentAnimeSeason
import com.axiel7.anihyou.utils.DateUtils.nextAnimeSeason
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

class DiscoverViewModel : BaseViewModel() {

    val infos = mutableStateListOf(DiscoverInfo.AIRING, DiscoverInfo.THIS_SEASON, DiscoverInfo.TRENDING_ANIME)

    fun addNextInfo() {
        if (infos.size < DiscoverInfo.entries.size)
            infos.add(DiscoverInfo.entries[infos.size])
    }

    private val now = LocalDateTime.now()
    val nowAnimeSeason = now.currentAnimeSeason()
    val nextAnimeSeason = now.nextAnimeSeason()

    val airingAnime = MediaRepository.getAiringAnimePage(
        airingAtGreater = System.currentTimeMillis() / 1000
    ).pagedResultStateInViewModel()

    val airingAnimeOnMyList =
        MediaRepository.getAiringAnimeOnMyListPage().pagedResultStateInViewModel()

    val thisSeasonAnime =
        MediaRepository.getSeasonalAnimePage(nowAnimeSeason).pagedResultStateInViewModel()

    val trendingAnime = MediaRepository.getMediaSortedPage(
        mediaType = MediaType.ANIME,
        sort = listOf(MediaSort.TRENDING_DESC)
    ).pagedResultStateInViewModel()

    val nextSeasonAnime =
        MediaRepository.getSeasonalAnimePage(nextAnimeSeason).pagedResultStateInViewModel()

    val trendingManga = MediaRepository.getMediaSortedPage(
        mediaType = MediaType.MANGA,
        sort = listOf(MediaSort.TRENDING_DESC)
    ).pagedResultStateInViewModel()

    val unreadNotificationCount = UserRepository.getUnreadNotificationCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}