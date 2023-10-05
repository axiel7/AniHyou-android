package com.axiel7.anihyou.data.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.SearchStaffQuery
import com.axiel7.anihyou.StaffCharacterQuery
import com.axiel7.anihyou.StaffDetailsQuery
import com.axiel7.anihyou.StaffMediaQuery
import javax.inject.Inject

class StaffApi @Inject constructor(
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

    fun staffMediaQuery(
        staffId: Int,
        onList: Boolean,
        page: Int,
        perPage: Int,
    ) = client
        .query(
            StaffMediaQuery(
                staffId = Optional.present(staffId),
                onList = if (onList) Optional.present(true) else Optional.absent(),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )

    fun staffCharacterQuery(
        staffId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = client
        .query(
            StaffCharacterQuery(
                staffId = Optional.present(staffId),
                page = Optional.present(page),
                perPage = Optional.present(perPage)
            )
        )
}