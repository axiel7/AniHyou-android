package com.axiel7.anihyou.data.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.axiel7.anihyou.NotificationsQuery
import com.axiel7.anihyou.type.NotificationType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationsApi @Inject constructor(
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
}