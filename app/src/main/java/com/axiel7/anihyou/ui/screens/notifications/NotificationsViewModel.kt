package com.axiel7.anihyou.ui.screens.notifications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.App
import com.axiel7.anihyou.data.PreferencesDataStore.LAST_NOTIFICATION_CREATED_AT_PREFERENCE_KEY
import com.axiel7.anihyou.data.model.notification.GenericNotification
import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.data.repository.NotificationRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class NotificationsViewModel(
    private val initialUnreadCount: Int = 0
) : BaseViewModel() {

    var type by mutableStateOf(NotificationTypeGroup.ALL)
    private var resetCount = true

    val notifications = mutableStateListOf<GenericNotification>()
    var page = 1
    var hasNextPage = false

    fun getNotifications() = viewModelScope.launch(dispatcher) {
        NotificationRepository.getNotificationsPage(
            type = type,
            resetCount = resetCount,
            page = page,
        ).collect { result ->
            isLoading = result is PagedResult.Loading

            if (result is PagedResult.Success) {
                if (resetCount) {
                    App.dataStore.edit {
                        result.data.first().createdAt?.let { createdAt ->
                            it[LAST_NOTIFICATION_CREATED_AT_PREFERENCE_KEY] = createdAt
                        }
                    }
                    resetCount = false
                    val unreads = result.data.take(initialUnreadCount)
                        .map { it.copy(isUnread = true) }
                    val readCount = result.data.size - initialUnreadCount
                    val reads = if (readCount > 0) result.data.takeLast(readCount) else emptyList()
                    notifications.addAll(unreads + reads)
                } else {
                    notifications.addAll(result.data)
                }
                page = result.nextPage ?: page
                hasNextPage = result.nextPage != null
            }
        }
    }

    fun resetPage() {
        page = 1
        hasNextPage = false
        notifications.clear()
        getNotifications()
    }
}