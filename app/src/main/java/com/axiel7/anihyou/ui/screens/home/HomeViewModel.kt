package com.axiel7.anihyou.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userRepository: UserRepository,
    defaultPreferencesRepository: DefaultPreferencesRepository
) : ViewModel() {

    val unreadNotificationCount = userRepository.getUnreadNotificationCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val defaultHomeTab = defaultPreferencesRepository.defaultHomeTab
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}