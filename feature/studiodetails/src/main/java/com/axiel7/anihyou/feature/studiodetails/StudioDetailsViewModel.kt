package com.axiel7.anihyou.feature.studiodetails

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.PagedResult
import com.axiel7.anihyou.core.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.core.domain.repository.FavoriteRepository
import com.axiel7.anihyou.core.domain.repository.StudioRepository
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.ui.common.navigation.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam

@OptIn(ExperimentalCoroutinesApi::class)
class StudioDetailsViewModel(
    @InjectedParam private val arguments: Route.StudioDetails,
    private val studioRepository: StudioRepository,
    private val favoriteRepository: FavoriteRepository,
) : PagedUiStateViewModel<StudioDetailsUiState>(), StudioDetailsEvent {

    override val initialState = StudioDetailsUiState()

    override fun toggleFavorite() {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(studioId = arguments.id).let { result ->
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
        }
    }

    override fun setSort(sort: MediaSort) {
        mutableUiState.update { it.copy(sort = sort, page = 1, hasNextPage = true) }
    }

    override fun toggleOnMyList() {
        mutableUiState.update {
            it.copy(
                onMyList = if (it.onMyList == true) null else true,
                page = 1,
                hasNextPage = true
            )
        }
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
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.onMyList == new.onMyList
                        && old.sort == new.sort
            }
            .flatMapLatest { uiState ->
                studioRepository.getStudioMediaPage(
                    studioId = arguments.id,
                    sort = listOf(uiState.sort),
                    onList = uiState.onMyList,
                    page = uiState.page
                )
            }
            .onEach { result ->
                if (result is PagedResult.Success) {
                    mutableUiState.value.run {
                        if (result.currentPage == 1) media.clear()
                        media.addAll(result.list)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}