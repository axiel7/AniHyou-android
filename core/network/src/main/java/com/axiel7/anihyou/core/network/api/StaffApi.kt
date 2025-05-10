package com.axiel7.anihyou.core.network.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.apolloStore
import com.axiel7.anihyou.core.network.SearchStaffQuery
import com.axiel7.anihyou.core.network.StaffCharacterQuery
import com.axiel7.anihyou.core.network.StaffDetailsQuery
import com.axiel7.anihyou.core.network.StaffMediaQuery

class StaffApi (
    private val client: ApolloClient
) {
    fun searchStaffQuery(
        query: String,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            SearchStaffQuery(
                page = Optional.present(page),
                perPage = Optional.present(perPage),
                search = Optional.present(query)
            )
        )

    fun staffDetailsQuery(staffId: Int) = client
        .query(
            StaffDetailsQuery(
                staffId = Optional.present(staffId)
            )
        )

    suspend fun updateStaffDetailsCache(data: StaffDetailsQuery.Data) {
        client.apolloStore
            .writeOperation(
                operation = StaffDetailsQuery(
                    staffId = Optional.presentIfNotNull(data.Staff?.id)
                ),
                operationData = data
            )
    }

    fun staffMediaQuery(
        staffId: Int,
        onList: Boolean?,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            StaffMediaQuery(
                staffId = Optional.present(staffId),
                onList = Optional.presentIfNotNull(onList),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun staffCharacterQuery(
        staffId: Int,
        onList: Boolean?,
        page: Int = 1,
        perPage: Int = 25,
    ) = client
        .query(
            StaffCharacterQuery(
                staffId = Optional.present(staffId),
                onList = Optional.presentIfNotNull(onList),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )
}