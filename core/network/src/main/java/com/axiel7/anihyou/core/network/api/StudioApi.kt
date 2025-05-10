package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.apolloStore
import com.axiel7.anihyou.core.network.SearchStudioQuery
import com.axiel7.anihyou.core.network.StudioDetailsQuery
import com.axiel7.anihyou.core.network.StudioMediaQuery

class StudioApi (
    private val client: ApolloClient
) {
    fun searchStudioQuery(
        query: String,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            SearchStudioQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                search = Optional.present(query)
            )
        )

    fun studioDetailsQuery(
        studioId: Int,
        perPage: Int
    ) = client
        .query(
            StudioDetailsQuery(
                studioId = Optional.present(studioId),
                perPage = Optional.present(perPage)
            )
        )

    suspend fun updateStudioDetailsCache(data: StudioDetailsQuery.Data) {
        client.apolloStore
            .writeOperation(
                operation = StudioDetailsQuery(
                    studioId = Optional.presentIfNotNull(data.Studio?.id)
                ),
                operationData = data
            )
    }

    fun studioMediaQuery(
        studioId: Int,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            StudioMediaQuery(
                studioId = Optional.present(studioId),
                page = Optional.present(page),
                perPage = Optional.present(perPage),
            )
        )
}