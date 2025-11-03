package com.axiel7.anihyou.feature.notifications

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.core.base.event.PagedEvent

@Immutable
interface NotificationsEvent : PagedEvent {
    fun setType(value: NotificationTypeGroup)
}