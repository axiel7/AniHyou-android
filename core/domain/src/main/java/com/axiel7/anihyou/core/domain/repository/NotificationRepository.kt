package com.axiel7.anihyou.core.domain.repository

import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.apollographql.apollo.cache.normalized.watch
import com.axiel7.anihyou.core.network.api.NotificationsApi
import com.axiel7.anihyou.core.model.notification.GenericNotification.Companion.toGenericNotifications
import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup

class NotificationRepository(
    private val api: NotificationsApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {
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

    suspend fun getNewNotifications(unreadCount: Int) = api
        .notificationsQuery(
            typeIn = null,
            resetCount = false,
            page = 1,
            perPage = unreadCount
        )
        .fetchPolicy(FetchPolicy.NetworkFirst)
        .execute()
        .asDataResult { it.Page?.notifications?.filterNotNull()?.toGenericNotifications() }
}