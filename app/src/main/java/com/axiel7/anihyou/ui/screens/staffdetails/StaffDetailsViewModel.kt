package com.axiel7.anihyou.ui.screens.staffdetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.StaffCharacterQuery
import com.axiel7.anihyou.StaffDetailsQuery
import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.data.repository.StaffRepository
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class StaffDetailsViewModel(
    private val staffId: Int
) : BaseViewModel() {

    var staffDetails by mutableStateOf<StaffDetailsQuery.Staff?>(null)

    suspend fun getStaffDetails() = viewModelScope.launch {
        StaffRepository.getStaffDetails(staffId).collect { result ->
            isLoading = result is DataResult.Loading

            if (result is DataResult.Success) {
                staffDetails = result.data
            }
            else if (result is DataResult.Error) {
                message = result.message
            }
        }
    }

    suspend fun toggleFavorite() = viewModelScope.launch {
        staffDetails?.let { details ->
            FavoriteRepository.toggleFavorite(staffId = staffId).collect { result ->
                if (result is DataResult.Success) {
                    if (result.data) {
                        staffDetails = details.copy(isFavourite = !details.isFavourite)
                    }
                }
            }
        }
    }

    var mediaOnMyList by mutableStateOf(false)
    private var pageMedia = 1
    var hasNextPageMedia = true
    var staffMedia = mutableStateListOf<Pair<Int, StaffMediaGrouped>>()

    suspend fun getStaffMedia() = viewModelScope.launch {
        StaffRepository.getStaffMediaPage(
            staffId = staffId,
            onList = mediaOnMyList,
            page = pageMedia
        ).collect { result ->
            isLoading = pageMedia == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                staffMedia.addAll(result.data)
                hasNextPageMedia = result.nextPage != null
                pageMedia = result.nextPage ?: pageMedia
            }
            else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }

    suspend fun refreshStaffMedia() {
        hasNextPageMedia = false
        pageMedia = 1
        staffMedia.clear()
        getStaffMedia()
    }

    private var pageCharacter = 1
    var hasNextPageCharacter = true
    var staffCharacters = mutableStateListOf<StaffCharacterQuery.Edge>()

    suspend fun getStaffCharacters() = viewModelScope.launch {
        StaffRepository.getStaffCharactersPage(
            staffId = staffId,
            page = pageCharacter
        ).collect { result ->
            isLoading = pageCharacter == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                staffCharacters.addAll(result.data)
                hasNextPageCharacter = result.nextPage != null
                pageCharacter = result.nextPage ?: pageCharacter
            }
            else if (result is PagedResult.Error) {
                message = result.message
            }
        }
    }
}