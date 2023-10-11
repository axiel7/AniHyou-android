package com.axiel7.anihyou.ui.screens.studiodetails

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.data.model.DataResult
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.repository.FavoriteRepository
import com.axiel7.anihyou.data.repository.StudioRepository
import com.axiel7.anihyou.fragment.CommonStudioMedia
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StudioDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    studioRepository: StudioRepository,
    private val favoriteRepository: FavoriteRepository,
) : PagedUiStateViewModel<StudioDetailsUiState>() {

    val studioId =
        savedStateHandle.getStateFlow<Int?>(STUDIO_ID_ARGUMENT.removeFirstAndLast(), null)

    override val mutableUiState = MutableStateFlow(StudioDetailsUiState())
    override val uiState = mutableUiState.asStateFlow()

    val studioMedia = mutableStateListOf<CommonStudioMedia.Node>()

    fun toggleFavorite() {
        studioId.value?.let { studioId ->
            favoriteRepository.toggleFavorite(studioId = studioId)
                .onEach { result ->
                    if (result is DataResult.Success && result.data != null) {
                        mutableUiState.update {
                            it.copy(
                                details = it.details?.copy(isFavourite = !it.details.isFavourite)
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    init {
        // studio details
        // it also gets the first media page in this query to avoid two consecutive api calls
        studioId
            .filterNotNull()
            .flatMapLatest { studioId ->
                studioRepository.getStudioDetails(studioId)
            }
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is DataResult.Success) {
                        result.data?.media?.commonStudioMedia?.nodes?.filterNotNull()?.let {
                            studioMedia.addAll(it)
                        }
                        uiState.copy(
                            isLoading = false,
                            details = result.data,
                            hasNextPage = result.data?.media?.pageInfo?.commonPage?.hasNextPage == true,
                            //page = data?.media?.pageInfo?.commonPage?.currentPage ?: it.page
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }
            .launchIn(viewModelScope)

        // next media pages
        mutableUiState
            .filter { it.hasNextPage && it.details != null }
            .distinctUntilChangedBy { it.page }
            .combine(studioId.filterNotNull(), ::Pair)
            .flatMapLatest { (uiState, studioId) ->
                studioRepository.getStudioMediaPage(
                    studioId = studioId,
                    page = uiState.page
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    studioMedia.addAll(result.list)
                }
            }
            .launchIn(viewModelScope)
    }
}