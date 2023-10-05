package com.axiel7.anihyou.ui.screens.home

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.ui.common.UiStateViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel : UiStateViewModel() {
    val unreadNotificationCount = UserRepository.getUnreadNotificationCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}