package com.axiel7.anihyou.ui.calendar

import androidx.compose.runtime.mutableStateListOf
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.type.AiringSort
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.utils.DateUtils.thisWeekdayTimestamp
import java.time.DayOfWeek
import java.time.LocalDateTime

class CalendarViewModel : BaseViewModel() {

    private val now: LocalDateTime = LocalDateTime.now()

    var page = 1
    var hasNextPage = false
    val weeklyAnime = mutableStateListOf<AiringAnimesQuery.AiringSchedule>()

    suspend fun getAiringAnime(weekday: Int, onMyList: Boolean) {
        isLoading = page == 1
        val weekdayStartTimestamp = now.thisWeekdayTimestamp(
            dayOfWeek = DayOfWeek.of(weekday),
            isEndOfDay = false
        )
        val weekdayEndTimestamp = now.thisWeekdayTimestamp(
            dayOfWeek = DayOfWeek.of(weekday),
            isEndOfDay = true
        )
        val response = AiringAnimesQuery(
            page = Optional.present(page),
            perPage = Optional.present(25),
            sort = Optional.present(listOf(AiringSort.TIME)),
            airingAtGreater = Optional.present(weekdayStartTimestamp.toInt()),
            airingAtLesser = Optional.present(weekdayEndTimestamp.toInt())
        ).tryQuery()

        response?.data?.Page?.airingSchedules?.filterNotNull()?.let { anime ->
            if (onMyList) weeklyAnime.addAll(anime.filter { it.media?.mediaListEntry != null })
            else weeklyAnime.addAll(anime)
        }
        hasNextPage = response?.data?.Page?.pageInfo?.hasNextPage ?: false
        page = response?.data?.Page?.pageInfo?.currentPage?.plus(1) ?: page
        isLoading = false
    }

    fun resetPage() {
        page = 1
        hasNextPage = false
        weeklyAnime.clear()
    }
}