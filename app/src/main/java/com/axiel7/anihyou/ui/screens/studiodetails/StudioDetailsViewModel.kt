package com.axiel7.anihyou.ui.screens.studiodetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.StudioDetailsQuery
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.data.repository.StudioRepository
import com.axiel7.anihyou.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class StudioDetailsViewModel(
    private val studioId: Int
) : BaseViewModel() {

    var page = 1
    var hasNextPage = true
    var studioDetails by mutableStateOf<StudioDetailsQuery.Studio?>(null)
    val studioMedia = mutableStateListOf<StudioDetailsQuery.Node>()

    fun getStudioDetails() = viewModelScope.launch(dispatcher) {
        StudioRepository.getStudioDetails(
            studioId = studioId,
            page = page
        ).collect { result ->
            isLoading = page == 1 && result is PagedResult.Loading

            if (result is PagedResult.Success) {
                studioDetails = result.data
                result.data.media?.nodes?.filterNotNull()?.let { studioMedia.addAll(it) }
                hasNextPage = result.nextPage != null
                page = result.nextPage ?: page
            }
        }
    }

    fun toggleFavorite() = viewModelScope.launch(dispatcher) {
        FavoriteRepository.toggleFavorite(studioId = studioId).collect { result ->
            if (result is DataResult.Success && result.data) {
                studioDetails = studioDetails?.copy(
                    isFavourite = studioDetails?.isFavourite?.not() ?: false
                )
            }
        }
    }
}