package com.axiel7.anihyou.ui.screens.calendar

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.ui.base.UiState
import com.axiel7.anihyou.utils.DateUtils.thisWeekdayTimestamp
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime

class CalendarViewModel : BaseViewModel() {

    private val now: LocalDateTime = LocalDateTime.now()

    private var page = 1
    var hasNextPage = false
    val weeklyAnime = mutableStateListOf<AiringAnimesQuery.AiringSchedule>()
    var onMyList = false

    suspend fun getAiringAnime(weekday: Int) = viewModelScope.launch {
        val weekdayStartTimestamp = now.thisWeekdayTimestamp(
            dayOfWeek = DayOfWeek.of(weekday),
            isEndOfDay = false
        )
        val weekdayEndTimestamp = now.thisWeekdayTimestamp(
            dayOfWeek = DayOfWeek.of(weekday),
            isEndOfDay = true
        )

        MediaRepository.getAiringAnimePage(
            airingAtGreater = weekdayStartTimestamp,
            airingAtLesser = weekdayEndTimestamp,
            page = page,
            perPage = 25
        ).collect { uiState ->
            isLoading = page == 1 && uiState is UiState.Loading

            if (uiState is UiState.Success) {
                uiState.data.airingSchedules?.filterNotNull()?.let { anime ->
                    if (onMyList) weeklyAnime.addAll(anime.filter { it.media?.mediaListEntry != null })
                    else weeklyAnime.addAll(anime)
                }
                hasNextPage = uiState.data.pageInfo?.hasNextPage ?: false
                page = uiState.data.pageInfo?.currentPage?.plus(1) ?: page++
            }
        }
    }

    fun resetPage() {
        page = 1
        hasNextPage = false
        weeklyAnime.clear()
    }
}