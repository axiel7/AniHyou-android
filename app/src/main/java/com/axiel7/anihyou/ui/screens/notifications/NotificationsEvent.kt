package com.axiel7.anihyou.ui.screens.notifications

import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.ui.common.event.PagedEvent

interface NotificationsEvent : PagedEvent {
    fun setType(value: NotificationTypeGroup)
    fun markAllAsRead()
}