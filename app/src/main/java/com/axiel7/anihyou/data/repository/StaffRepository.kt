package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.cache.normalized.watch
import com.apollographql.apollo3.exception.ApolloException
import com.axiel7.anihyou.data.api.StaffApi
import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import javax.inject.Inject

class StaffRepository @Inject constructor(
    private val api: StaffApi
) {

    fun getStaffDetails(staffId: Int) = api
        .staffDetailsQuery(staffId)
        .watch()
        .asDataResult {
            it.Staff
        }

    @Throws(ApolloException::class)
    suspend fun getStaffMediaPage(
        staffId: Int,
        onList: Boolean = false,
        page: Int = 1,
        perPage: Int = 25,
    ): PageResult<Pair<Int, StaffMediaGrouped>>? {
        val data = api
            .staffMediaQuery(staffId, onList, page, perPage)
            .execute()
            .dataOrThrow()

        val media = data.Staff?.staffMedia?.edges?.filterNotNull()
        val pageInfo = data.Staff?.staffMedia?.pageInfo
        return if (media != null) {
            // group media to display staff roles joined
            val mediaGroupMap = mutableMapOf<Int, StaffMediaGrouped>()
            media.groupBy { it.node?.id ?: 0 }.forEach { (mediaId, value) ->
                mediaGroupMap[mediaId] = StaffMediaGrouped(
                    value = value[0],
                    staffRoles = value.map { it.staffRole ?: "" }
                )
            }
            PageResult(
                list = mediaGroupMap.toList(),
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            )
        } else null
    }

    suspend fun getStaffCharactersPage(
        staffId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = api
        .staffCharacterQuery(staffId, page, perPage)
        .execute()
}