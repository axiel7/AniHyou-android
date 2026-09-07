package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.axiel7.anihyou.core.network.NotificationsQuery
import com.axiel7.anihyou.core.network.type.NotificationType

class NotificationsApi (
    private val client: ApolloClient
) {
    fun notificationsQuery(
        typeIn: List<NotificationType>?,
        resetCount: Boolean,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            NotificationsQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                typeIn = Optional.presentIfNotNull(typeIn),
                resetCount = Optional.present(resetCount)
            )
        )

    fun resetNotificationCount() = client
        .query(
            NotificationsQuery(
                page = Optional.present(1),
                perPage = Optional.present(1),
                resetCount = Optional.present(true),
            )
        )
}