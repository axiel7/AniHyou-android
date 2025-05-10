package com.axiel7.anihyou.core.domain.repository

import com.apollographql.apollo.cache.normalized.watch
import com.axiel7.anihyou.core.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.core.network.StaffDetailsQuery
import com.axiel7.anihyou.core.network.api.StaffApi

class StaffRepository(
    private val api: StaffApi,
    defaultPreferencesRepository: DefaultPreferencesRepository,
) : BaseNetworkRepository(defaultPreferencesRepository) {

    fun getStaffDetails(staffId: Int) = api
        .staffDetailsQuery(staffId)
        .watch()
        .asDataResult {
            it.Staff
        }

    suspend fun updateStaffDetailsCache(details: StaffDetailsQuery.Staff) {
        api.updateStaffDetailsCache(
            data = StaffDetailsQuery.Data(details)
        )
    }

    fun getStaffMediaPage(
        staffId: Int,
        onList: Boolean? = null,
        page: Int,
        perPage: Int = 25,
    ) = api
        .staffMediaQuery(staffId, onList, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Staff?.staffMedia?.pageInfo?.commonPage }) { data ->
            val media = data.Staff?.staffMedia?.edges?.filterNotNull().orEmpty()

            // group media to display staff roles joined
            val mediaGroupMap = mutableMapOf<Int, StaffMediaGrouped>()
            media.groupBy { it.node?.id ?: 0 }.forEach { (mediaId, value) ->
                mediaGroupMap[mediaId] = StaffMediaGrouped(
                    value = value[0],
                    staffRoles = value.map { it.staffRole.orEmpty() }
                )
            }
            mediaGroupMap.toList()
        }

    fun getStaffCharactersPage(
        staffId: Int,
        onList: Boolean? = null,
        page: Int,
        perPage: Int = 25,
    ) = api
        .staffCharacterQuery(staffId, onList, page, perPage)
        .toFlow()
        .asPagedResult(page = { it.Staff?.characterMedia?.pageInfo?.commonPage }) {
            it.Staff?.characterMedia?.edges?.filterNotNull().orEmpty()
        }
}