package com.axiel7.anihyou.feature.calendar

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.utils.DateUtils.toTimestamp
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.ExploreMedia
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(
    private val mediaRepository: MediaRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository
) : PagedUiStateViewModel<CalendarUiState>(), CalendarEvent {

    override val initialState = CalendarUiState()

    val onMyList = defaultPreferencesRepository.calendarOnMyList
    private val displayAdult = defaultPreferencesRepository.displayAdult

    fun onMyListChanged(value: Boolean?) = viewModelScope.launch {
        defaultPreferencesRepository.setCalendarOnMyList(value)
    }

    override fun onUpdateListEntry(viewListEntry: BasicMediaListEntry?) {
        mutableUiState.value.run {
            selectedItem?.let { selectedItem ->
                weeklyAnime.forEach { (date, list) ->
                    val index = list.indexOf(selectedItem)
                    if (index != -1) {
                        val updatedList = list.toMutableList()
                        updatedList[index] = selectedItem.copy(
                            mediaListEntry = viewListEntry?.let {
                                ExploreMedia.MediaListEntry(
                                    __typename = "ExploreMedia.MediaListEntry",
                                    id = viewListEntry.id,
                                    mediaId = viewListEntry.mediaId,
                                    basicMediaListEntry = viewListEntry
                                )
                            }
                        )
                        val updatedMap = weeklyAnime.toMutableMap()
                        updatedMap[date] = updatedList
                        mutableUiState.update { it.copy(weeklyAnime = updatedMap) }
                    }
                }
            }
        }
    }

    override fun setOnMyList(value: Boolean?) {
        mutableUiState.update {
            it.copy(onMyList = value, page = 1, hasNextPage = true, isLoading = true, weeklyAnime = mutableMapOf())
        }
    }

    override fun selectItem(value: ExploreMedia?) {
        mutableUiState.update {
            it.copy(selectedItem = value)
        }
    }

    override fun onLoadMore() {
        if (uiState.value.isLoading) return
        if (uiState.value.hasNextPage) {
            mutableUiState.update { it.copy(page = it.page + 1, isLoading = true) }
        } else {
            nextDay()
        }
    }

    override fun nextDay() {
        mutableUiState.update {
            it.copy(
                day = uiState.value.day.plusDays(1),
                page = 1,
                hasNextPage = true,
                isLoading = true,
            )
        }
    }

    init {
        onMyList.onEach { onMyListVal ->
            if (mutableUiState.value.onMyList != onMyListVal) {
                mutableUiState.update {
                    it.copy(
                        onMyList = onMyListVal,
                        weeklyAnime = mutableMapOf(),
                        day = LocalDateTime.now(),
                        page = 1,
                        hasNextPage = true,
                        isLoading = true
                    )
                }
            }
        }.launchIn(viewModelScope)

        mutableUiState
            .filter { it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.day == new.day
                        && old.onMyList == new.onMyList
            }
            .combine(displayAdult, ::Pair)
            .flatMapLatest { (uiState, displayAdult) ->
                val start = uiState.day.toTimestamp(
                    isEndOfDay = false
                )
                val end = uiState.day.toTimestamp(
                    isEndOfDay = true
                )
                mediaRepository.getAiringAnimesPage(
                    airingAtGreater = start,
                    airingAtLesser = end,
                    onMyList = uiState.onMyList,
                    isAdult = displayAdult == true,
                    page = uiState.page,
                    perPage = 25,
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    mutableUiState.update { state ->
                        val localeDate = state.day.toLocalDate()
                        val currentList = if (state.page == 1) {
                            emptyList()
                        } else {
                            state.weeklyAnime[localeDate] ?: emptyList()
                        }
                        val updatedList = currentList + result.list
                        val updatedMap = state.weeklyAnime.toMutableMap()
                        if (updatedList.isNotEmpty() || state.onMyList != true) {
                            updatedMap[localeDate] = updatedList
                        }
                        state.copy(
                            weeklyAnime = updatedMap,
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
