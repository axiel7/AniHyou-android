package com.axiel7.anihyou.ui.screens.explore.charts

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.data.model.PagedResult
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.NavArgument
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
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
        savedStateHandle.getStateFlow<String?>(NavArgument.ChartType.name, null)

    override val mutableUiState = MutableStateFlow(MediaChartUiState())
    override val uiState = mutableUiState.asStateFlow()

    fun selectItem(value: MediaChartQuery.Medium?) = mutableUiState.update {
        it.copy(selectedItem = value)
    }

    val mediaChart = mutableStateListOf<MediaChartQuery.Medium>()

    fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        uiState.value.selectedItem?.let { selectedItem ->
            val index = mediaChart.indexOf(selectedItem)
            if (index != -1) {
                mediaChart[index] = selectedItem.copy(
                    mediaListEntry = newListEntry?.let {
                        MediaChartQuery.MediaListEntry(
                            __typename = "MediaChartQuery.MediaListEntry",
                            id = newListEntry.id,
                            mediaId = newListEntry.mediaId,
                            basicMediaListEntry = newListEntry
                        )
                    }
                )
            }
        }
    }

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
                    page = uiState.page,
                    perPage = MediaChartUiState.PER_PAGE
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) mediaChart.clear()
                        mediaChart.addAll(result.list)

                        val hasNextPage = when (it.chartType) {
                            ChartType.TOP_ANIME, ChartType.TOP_MANGA, ChartType.TOP_MOVIES -> {
                                // limit top 100
                                it.page * MediaChartUiState.PER_PAGE < 100 && result.hasNextPage
                            }

                            else -> result.hasNextPage
                        }
                        it.copy(
                            isLoading = false,
                            hasNextPage = hasNextPage
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}