package com.axiel7.anihyou.feature.notifications

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface NotificationsEvent : UiEvent, PagedEvent {
    fun setType(value: NotificationTypeGroup)
}