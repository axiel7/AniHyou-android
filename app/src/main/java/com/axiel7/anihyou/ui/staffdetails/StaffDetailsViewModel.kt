package com.axiel7.anihyou.ui.staffdetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.apollographql.apollo3.api.Optional
import com.axiel7.anihyou.StaffCharacterQuery
import com.axiel7.anihyou.StaffDetailsQuery
import com.axiel7.anihyou.StaffMediaQuery
import com.axiel7.anihyou.ui.base.BaseViewModel

class StaffDetailsViewModel : BaseViewModel() {

    var staffDetails by mutableStateOf<StaffDetailsQuery.Staff?>(null)

    suspend fun getStaffDetails(staffId: Int) {
        isLoading = true
        val response = StaffDetailsQuery(
            staffId = Optional.present(staffId)
        ).tryQuery()

        response?.data?.Staff?.let { staffDetails = it }
        isLoading = false
    }

    var pageMedia = 1
    var hasNextPageMedia = true
    var staffMedia = mutableStateListOf<StaffMediaQuery.Edge>()

    suspend fun getStaffMedia(staffId: Int) {
        isLoading = pageMedia == 1
        val response = StaffMediaQuery(
            staffId = Optional.present(staffId),
            page = Optional.present(pageMedia),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.Staff?.staffMedia?.edges?.filterNotNull()?.let { staffMedia.addAll(it) }
        pageMedia = response?.data?.Staff?.staffMedia?.pageInfo?.currentPage?.plus(1) ?: pageMedia
        hasNextPageMedia = response?.data?.Staff?.staffMedia?.pageInfo?.hasNextPage ?: false
        isLoading = false
    }

    var pageCharacter = 1
    var hasNextPageCharacter = true
    var staffCharacters = mutableStateListOf<StaffCharacterQuery.Edge>()

    suspend fun getStaffCharacters(staffId: Int) {
        isLoading = pageCharacter == 1
        val response = StaffCharacterQuery(
            staffId = Optional.present(staffId),
            page = Optional.present(pageCharacter),
            perPage = Optional.present(25)
        ).tryQuery()

        response?.data?.Staff?.characterMedia?.edges?.filterNotNull()?.let { staffCharacters.addAll(it) }
        pageCharacter = response?.data?.Staff?.characterMedia?.pageInfo?.currentPage?.plus(1) ?: pageCharacter
        hasNextPageCharacter = response?.data?.Staff?.characterMedia?.pageInfo?.hasNextPage ?: false
        isLoading = false
    }
}