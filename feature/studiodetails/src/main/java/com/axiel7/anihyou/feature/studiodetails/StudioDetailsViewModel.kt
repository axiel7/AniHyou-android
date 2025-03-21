package com.axiel7.anihyou.feature.studiodetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.core.common.DataResult
import com.axiel7.anihyou.core.common.PagedResult
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.domain.repository.StudioRepository
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.common.viewmodel.PagedUiStateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
class StudioDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val studioRepository: StudioRepository,
    private val favoriteRepository: FavoriteRepository,
) : PagedUiStateViewModel<StudioDetailsUiState>(), StudioDetailsEvent {

    private val arguments = savedStateHandle.toRoute<Routes.StudioDetails>()

    override val initialState = StudioDetailsUiState()

    override fun toggleFavorite() {
        favoriteRepository.toggleFavorite(studioId = arguments.id)
            .onEach { result ->
                if (result is DataResult.Success && result.data != null) {
                    mutableUiState.update { state ->
                        val newDetails = state.details
                            ?.copy(isFavourite = !state.details.isFavourite)
                            ?.also {
                                studioRepository.updateStudioDetailsCache(it)
                            }
                        state.copy(
                            details = newDetails
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    init {
        // studio details
        // it also gets the first media page in this query to avoid two consecutive api calls
        studioRepository.getStudioDetails(arguments.id)
            .onEach { result ->
                mutableUiState.update { uiState ->
                    if (result is DataResult.Success) {
                        result.data?.media?.commonStudioMedia?.nodes?.filterNotNull()?.let {
                            uiState.media.addAll(it)
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
            .flatMapLatest { uiState ->
                studioRepository.getStudioMediaPage(
                    studioId = arguments.id,
                    page = uiState.page
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    mutableUiState.value.media.addAll(result.list)
                }
            }
            .launchIn(viewModelScope)
    }
}