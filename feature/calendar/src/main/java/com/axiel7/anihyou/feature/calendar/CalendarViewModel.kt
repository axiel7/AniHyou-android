package com.axiel7.anihyou.feature.calendar

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.utils.DateUtils.thisWeekdayTimestamp
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.network.AiringAnimesQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val mediaRepository: MediaRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : PagedUiStateViewModel<CalendarUiState>(), CalendarEvent {

    override val initialState = CalendarUiState()

    private val displayAdult = defaultPreferencesRepository.displayAdult

    private val now: LocalDateTime = LocalDateTime.now()

    fun setOnMyList(value: Boolean?) = mutableUiState.update {
        it.copy(onMyList = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setWeekday(value: Int) = mutableUiState.update {
        it.copy(weekday = value)
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        mutableUiState.value.run {
            selectedItem?.let { selectedItem ->
                val index = weeklyAnime.indexOf(selectedItem)
                if (index != -1) {
                    weeklyAnime[index] = selectedItem.copy(
                        media = selectedItem.media?.copy(
                            mediaListEntry = newListEntry?.let {
                                AiringAnimesQuery.MediaListEntry(
                                    __typename = "AiringAnimesQuery.MediaListEntry",
                                    id = newListEntry.id,
                                    mediaId = newListEntry.mediaId,
                                    basicMediaListEntry = newListEntry
                                )
                            }
                        ),
                    )
                }
            }
        }
    }

    override fun selectItem(value: AiringAnimesQuery.AiringSchedule?) {
        mutableUiState.update {
            it.copy(selectedItem = value)
        }
    }

    init {
        mutableUiState
            .filter { it.hasNextPage && it.weekday != 0 }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.weekday == new.weekday
                        && old.onMyList == new.onMyList
            }
            .combine(displayAdult, ::Pair)
            .flatMapLatest { (uiState, displayAdult) ->
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
                    displayAdult = displayAdult == true,
                    page = uiState.page
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    mutableUiState.update {
                        if (it.page == 1) it.weeklyAnime.clear()
                        it.weeklyAnime.addAll(result.list)
                        it.copy(
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
    }
}