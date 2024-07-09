package com.axiel7.anihyou.data.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.apolloStore
import com.axiel7.anihyou.SearchStudioQuery
import com.axiel7.anihyou.StudioDetailsQuery
import com.axiel7.anihyou.StudioMediaQuery
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudioApi @Inject constructor(
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