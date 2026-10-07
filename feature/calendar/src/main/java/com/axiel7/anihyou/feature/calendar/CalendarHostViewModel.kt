package com.axiel7.anihyou.feature.calendar

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.ListPreferencesRepository
import com.axiel7.anihyou.core.model.ListStyle
import kotlinx.coroutines.launch

@Stable
class CalendarHostViewModel(
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val listPreferencesRepository: ListPreferencesRepository,
): ViewModel() {

    val onMyList = defaultPreferencesRepository.calendarOnMyList

    val listStyle = listPreferencesRepository.calendarListStyle

    fun onMyListChanged(value: Boolean?) = viewModelScope.launch {
        defaultPreferencesRepository.setCalendarOnMyList(value)
    }

    fun onChangeListStyle(value: ListStyle) {
        viewModelScope.launch {
            listPreferencesRepository.setCalendarListStyle(value)
        }
    }
}