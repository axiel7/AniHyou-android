package com.axiel7.anihyou.ui.screens.calendar

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.utils.DateUtils.thisWeekdayTimestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    private val mediaRepository: MediaRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : PagedUiStateViewModel<CalendarUiState>() {

    private val displayAdult = defaultPreferencesRepository.displayAdult

    override val mutableUiState = MutableStateFlow(CalendarUiState())
    override val uiState = mutableUiState.asStateFlow()

    private val now: LocalDateTime = LocalDateTime.now()

    fun setOnMyList(value: Boolean?) = mutableUiState.update {
        it.copy(onMyList = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun setWeekday(value: Int) = mutableUiState.update {
        it.copy(weekday = value, page = 1, hasNextPage = true, isLoading = true)
    }

    fun selectItem(value: AiringAnimesQuery.AiringSchedule?) = mutableUiState.update {
        it.copy(selectedItem = value)
    }

    val weeklyAnime = mutableStateListOf<AiringAnimesQuery.AiringSchedule>()

    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        uiState.value.selectedItem?.let { selectedItem ->
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
                        if (it.page == 1) weeklyAnime.clear()
                        weeklyAnime.addAll(result.list)
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