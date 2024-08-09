package com.axiel7.anihyou.ui.screens.explore.charts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.MediaChartQuery
import com.axiel7.anihyou.data.api.response.PagedResult
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
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
) : PagedUiStateViewModel<MediaChartUiState>(), MediaChartEvent {

    private val arguments = savedStateHandle.toRoute<MediaChartList>()
    private val chartType = ChartType.valueOf(arguments.type)

    override val initialState = MediaChartUiState(
        chartType = chartType,
        hasNextPage = true
    )

    override fun selectItem(value: MediaChartQuery.Medium?) {
        mutableUiState.update {
            it.copy(selectedItem = value)
        }
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        mutableUiState.value.run {
            selectedItem?.let { selectedItem ->
                val index = media.indexOf(selectedItem)
                if (index != -1) {
                    media[index] = selectedItem.copy(
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
    }

    init {
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
                        if (it.page == 1) it.media.clear()
                        it.media.addAll(result.list)

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