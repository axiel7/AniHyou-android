package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.axiel7.anihyou.data.api.NotificationsApi
import com.axiel7.anihyou.data.model.asPagedResult
import com.axiel7.anihyou.data.model.notification.GenericNotification.Companion.toGenericNotifications
import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val api: NotificationsApi,
) {
    fun getNotificationsPage(
        type: NotificationTypeGroup,
        resetCount: Boolean,
        initialUnreadCount: Int,
        page: Int,
        perPage: Int = 25,
    ) = api
        .notificationsQuery(
            typeIn = type.values?.toList(),
            resetCount = resetCount,
            page = page,
            perPage = perPage
        )
        .watch()
        .asPagedResult(page = { it.Page?.pageInfo?.commonPage }) { data ->
            var notifications = data.Page?.notifications?.filterNotNull().orEmpty()
                .toGenericNotifications()

            if (page == 1) {
                // manually set unread state
                val unreads =
                    notifications.take(initialUnreadCount).map { it.copy(isUnread = true) }
                val readCount = notifications.size - initialUnreadCount
                val reads = if (readCount > 0) notifications.takeLast(readCount) else emptyList()
                notifications = unreads + reads
            }
            notifications
        }
}