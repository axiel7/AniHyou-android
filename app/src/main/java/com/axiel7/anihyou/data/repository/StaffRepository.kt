package com.axiel7.anihyou.data.repository

import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.StaffCharacterQuery
import com.axiel7.anihyou.StaffDetailsQuery
import com.axiel7.anihyou.StaffMediaQuery
import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.data.repository.BaseRepository.getError
import com.axiel7.anihyou.data.repository.BaseRepository.tryQuery
import kotlinx.coroutines.flow.flow

object StaffRepository {

    fun getStaffDetails(staffId: Int) = flow {
        emit(DataResult.Loading)

        val response = StaffDetailsQuery(
            staffId = Optional.present(staffId)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(DataResult.Error(message = error))
        else {
            val staff = response?.data?.Staff
            if (staff != null) emit(DataResult.Success(data = staff))
            else emit(DataResult.Error(message = "Error"))
        }
    }

    fun getStaffMediaPage(
        staffId: Int,
        onList: Boolean = false,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = StaffMediaQuery(
            staffId = Optional.present(staffId),
            onList = if (onList) Optional.present(true) else Optional.absent(),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val media = response?.data?.Staff?.staffMedia?.edges?.filterNotNull()
            val pageInfo = response?.data?.Staff?.staffMedia?.pageInfo
            if (media != null) {
                // group media to display staff roles joined
                val mediaGroupMap = mutableMapOf<Int, StaffMediaGrouped>()
                media.groupBy { it.node?.id ?: 0 }.forEach { (mediaId, value) ->
                    mediaGroupMap[mediaId] = StaffMediaGrouped(
                        value = value[0],
                        staffRoles = value.map { it.staffRole ?: "" }
                    )
                }
                emit(
                    PagedResult.Success(
                        data = mediaGroupMap.toList(),
                        nextPage = if (pageInfo?.hasNextPage == true)
                            pageInfo.currentPage?.plus(1)
                        else null
                    )
                )
            }
            else emit(PagedResult.Error(message = "Error"))
        }
    }

    fun getStaffCharactersPage(
        staffId: Int,
        page: Int = 1,
        perPage: Int = 25,
    ) = flow {
        emit(PagedResult.Loading)

        val response = StaffCharacterQuery(
            staffId = Optional.present(staffId),
            page = Optional.present(page),
            perPage = Optional.present(perPage)
        ).tryQuery()

        val error = response.getError()
        if (error != null) emit(PagedResult.Error(message = error))
        else {
            val characters = response?.data?.Staff?.characterMedia?.edges?.filterNotNull()
            val pageInfo = response?.data?.Staff?.characterMedia?.pageInfo
            if (characters != null) emit(PagedResult.Success(
                data = characters,
                nextPage = if (pageInfo?.hasNextPage == true)
                    pageInfo.currentPage?.plus(1)
                else null
            ))
            else emit(PagedResult.Error(message = "Error"))
        }
    }
}