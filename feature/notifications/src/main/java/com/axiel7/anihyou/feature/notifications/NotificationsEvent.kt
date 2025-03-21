package com.axiel7.anihyou.feature.notifications

import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.core.ui.common.event.PagedEvent

interface NotificationsEvent : PagedEvent {
    fun setType(value: NotificationTypeGroup)
}