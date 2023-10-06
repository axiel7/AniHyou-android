package com.axiel7.anihyou.ui.screens.explore.charts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.ui.common.UiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class MediaChartViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
) : UiStateViewModel<MediaChartUiState>() {

    private val initialType = ChartType.valueOf(
        savedStateHandle[CHART_TYPE_ARGUMENT.removeFirstAndLast()]!!
    )

    override val mutableUiState = MutableStateFlow(MediaChartUiState(chartType = initialType))
    override val uiState = mutableUiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val mediaChart = uiState
        .flatMapLatest {
            mediaRepository.getMediaChartPage(it.chartType)
        }
        .cachedIn(viewModelScope)
}