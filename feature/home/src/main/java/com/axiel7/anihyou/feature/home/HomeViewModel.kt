package com.axiel7.anihyou.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.UserRepository
import com.axiel7.anihyou.core.model.HomeTab
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(
    userRepository: UserRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository
) : ViewModel() {

    val unreadNotificationCount = userRepository.getUnreadNotificationCount()
        .map { it ?: 0 }

    fun saveHomeTab(value: Int) {
        viewModelScope.launch {
            HomeTab.valueOf(value)?.let {
                defaultPreferencesRepository.setDefaultHomeTab(it)
            }
        }
    }
}