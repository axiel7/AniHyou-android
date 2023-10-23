package com.axiel7.anihyou.ui.screens.calendar

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.utils.DateUtils.thisWeekdayTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
) : PagedUiStateViewModel<CalendarUiState>() {

    override val mutableUiState = MutableStateFlow(CalendarUiState())
    override val uiState = mutableUiState.asStateFlow()

    private val now: LocalDateTime = LocalDateTime.now()

    fun setOnMyList(value: Boolean?) = mutableUiState.update { it.copy(onMyList = value) }

    fun setWeekday(value: Int) = mutableUiState.update { it.copy(weekday = value) }

    val weeklyAnime = mutableStateListOf<AiringAnimesQuery.AiringSchedule>()

    init {
        mutableUiState
            .filter { (it.hasNextPage || it.refresh) && it.page != 0 }
            .distinctUntilChanged { old, new ->
                old.page == new.page && !new.refresh
            }
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
                    onMyList = uiState.onMyList,
                    page = uiState.page
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    mutableUiState.update {
                        if (it.refresh) weeklyAnime.clear()
                        weeklyAnime.addAll(result.list)
                        it.copy(
                            refresh = false,
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                        )
                    }
                } else {
                    mutableUiState.update {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)

        mutableUiState
            .distinctUntilChanged { old, new ->
                old.weekday == new.weekday
                        || old.onMyList == new.onMyList
            }
            .filter { it.page != 0 }
            .onEach {
                mutableUiState.update {
                    it.copy(refresh = true, page = 1, isLoading = true)
                }
            }
            .launchIn(viewModelScope)
    }
}