package com.axiel7.anihyou.ui.screens.calendar

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.filter
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.ui.common.UiStateViewModel
import com.axiel7.anihyou.utils.DateUtils.thisWeekdayTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : UiStateViewModel<CalendarUiState>() {

    override val mutableUiState = MutableStateFlow(CalendarUiState())
    override val uiState = mutableUiState.asStateFlow()

    private val now: LocalDateTime = LocalDateTime.now()

    fun setOnMyList(value: Boolean) = mutableUiState.update { it.copy(onMyList = value) }

    fun setWeekday(value: Int) = mutableUiState.update { it.copy(weekday = value) }

    @OptIn(ExperimentalCoroutinesApi::class)
    val weeklyAnime =
        uiState
            .flatMapLatest { uiState ->
                val start = now.thisWeekdayTimestamp(
                    dayOfWeek = DayOfWeek.of(uiState.weekday),
                    isEndOfDay = false
                )
                val end = now.thisWeekdayTimestamp(
                    dayOfWeek = DayOfWeek.of(uiState.weekday),
                    isEndOfDay = true
                )
                mediaRepository.getAiringAnimesPage(
                    airingAtGreater = start,
                    airingAtLesser = end,
                ).map { pagingData ->
                    if (uiState.onMyList) pagingData.filter { it.media?.mediaListEntry != null }
                    else pagingData
                }
            }
            .cachedIn(viewModelScope)
}