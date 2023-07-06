package com.axiel7.anihyou.ui.screens.calendar

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.ui.base.BaseViewModel
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

    fun getAiringAnime(weekday: Int) = viewModelScope.launch(dispatcher) {
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
        ).collect { result ->
            isLoading = page == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                if (onMyList) weeklyAnime.addAll(result.data.filter { it.media?.mediaListEntry != null })
                else weeklyAnime.addAll(result.data)
                hasNextPage = result.nextPage != null
                page = result.nextPage ?: page
            }
        }
    }

    fun resetPage() {
        page = 1
        hasNextPage = false
        weeklyAnime.clear()
    }
}