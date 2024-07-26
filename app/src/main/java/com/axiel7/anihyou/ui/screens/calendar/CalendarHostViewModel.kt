package com.axiel7.anihyou.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarHostViewModel @Inject constructor(
    private val defaultPreferencesRepository: DefaultPreferencesRepository
): ViewModel() {

    val onMyList = defaultPreferencesRepository.calendarOnMyList

    fun onMyListChanged(value: Boolean?) = viewModelScope.launch {
        defaultPreferencesRepository.setCalendarOnMyList(value)
    }
}