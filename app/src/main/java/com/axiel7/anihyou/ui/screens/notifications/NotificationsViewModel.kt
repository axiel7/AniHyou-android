package com.axiel7.anihyou.ui.screens.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.notification.GenericNotification
import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.data.repository.NotificationRepository
import com.axiel7.anihyou.data.repository.NotificationRepository.toGenericNotifications
import com.axiel7.anihyou.ui.base.BaseViewModel
import com.axiel7.anihyou.ui.base.UiState
import kotlinx.coroutines.launch

class NotificationsViewModel : BaseViewModel() {

    var type by mutableStateOf(NotificationTypeGroup.ALL)

    val notifications = mutableStateListOf<GenericNotification>()
    var page = 1
    var hasNextPage = false

    suspend fun getNotifications() = viewModelScope.launch {
        NotificationRepository.getNotificationsPage(
            type = type,
            page = page,
        ).collect { uiState ->
            isLoading = uiState is UiState.Loading

            if (uiState is UiState.Success) {
                uiState.data.notifications?.filterNotNull()?.toGenericNotifications()?.let {
                    notifications.addAll(it)
                }
                hasNextPage = uiState.data.pageInfo?.hasNextPage ?: false
                page = uiState.data.pageInfo?.currentPage?.plus(1) ?: page++
            }
        }
    }

    fun resetPage() {
        page = 1
        hasNextPage = true
        notifications.clear()
    }
}