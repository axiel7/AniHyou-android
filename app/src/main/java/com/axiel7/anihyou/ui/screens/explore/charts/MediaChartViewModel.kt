package com.axiel7.anihyou.ui.screens.explore.charts

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaChartViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
) : PagedUiStateViewModel<MediaChartUiState>() {

    private val initialType =
        savedStateHandle.getStateFlow<String?>(CHART_TYPE_ARGUMENT.removeFirstAndLast(), null)

    override val mutableUiState = MutableStateFlow(MediaChartUiState())
    override val uiState = mutableUiState.asStateFlow()

    val mediaChart = mutableStateListOf<MediaChartQuery.Medium>()

    init {
        initialType
            .filterNotNull()
            .onEach { type ->
                mutableUiState.update {
                    it.copy(
                        chartType = ChartType.valueOf(type),
                        page = 1,
                        hasNextPage = true,
                    )
                }
            }
            .launchIn(viewModelScope)

        mutableUiState
            .filter { it.hasNextPage && it.chartType != null }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.chartType == new.chartType
            }
            .flatMapLatest { uiState ->
                mediaRepository.getMediaChartPage(
                    type = uiState.chartType!!,
                    page = uiState.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) mediaChart.clear()
                        mediaChart.addAll(result.list)
                        it.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}