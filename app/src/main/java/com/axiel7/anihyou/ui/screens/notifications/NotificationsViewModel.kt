package com.axiel7.anihyou.ui.screens.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.notification.GenericNotification
import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.data.repository.NotificationRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class NotificationsViewModel : BaseViewModel() {

    var type by mutableStateOf(NotificationTypeGroup.ALL)

    val notifications = mutableStateListOf<GenericNotification>()
    var page = 1
    var hasNextPage = false

    fun getNotifications() = viewModelScope.launch(dispatcher) {
        NotificationRepository.getNotificationsPage(
            type = type,
            page = page,
        ).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                notifications.addAll(result.data)
                hasNextPage = result.nextPage != null
                page = result.nextPage ?: page
            }
        }
    }

    fun resetPage() {
        page = 1
        hasNextPage = true
        notifications.clear()
    }
}