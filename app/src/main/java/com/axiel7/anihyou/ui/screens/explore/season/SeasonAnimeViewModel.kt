package com.axiel7.anihyou.ui.screens.explore.season

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.data.api.response.PagedResult
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.repository.ListPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.viewmodel.PagedUiStateViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SeasonAnimeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
    private val listPreferencesRepository: ListPreferencesRepository,
) : PagedUiStateViewModel<SeasonAnimeUiState>(), SeasonAnimeEvent {

    private val arguments = savedStateHandle.toRoute<SeasonAnime>()
    private val season = MediaSeason.safeValueOf(arguments.season)

    override val initialState = SeasonAnimeUiState(
        season = AnimeSeason(season = season, year = arguments.year)
    )

    override fun setSeason(value: AnimeSeason) {
        mutableUiState.update {
            it.copy(season = value, page = 1, hasNextPage = true, isLoading = true)
        }
    }

    override fun onChangeSort(value: MediaSort) {
        mutableUiState.update {
            it.copy(sort = value, page = 1, hasNextPage = true, isLoading = true)
        }
    }

    override fun selectItem(value: SeasonalAnimeQuery.Medium?) {
        mutableUiState.update {
            it.copy(selectedItem = value)
        }
    }

    override fun onUpdateListEntry(newListEntry: BasicMediaListEntry?) {
        uiState.value.run {
            selectedItem?.let { selectedItem ->
                val index = animeSeasonal.indexOf(selectedItem)
                if (index != -1) {
                    animeSeasonal[index] = selectedItem.copy(
                        mediaListEntry = newListEntry?.let {
                            SeasonalAnimeQuery.MediaListEntry(
                                __typename = "SeasonalAnimeQuery.MediaListEntry",
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

    override fun onChangeListStyle(value: ListStyle) {
        viewModelScope.launch {
            listPreferencesRepository.setSeasonalListStyle(value)
        }
    }

    init {
        listPreferencesRepository.seasonalListStyle
            .distinctUntilChanged()
            .onEach { value ->
                mutableUiState.update { it.copy(listStyle = value) }
            }
            .launchIn(viewModelScope)

        uiState
            .filter { it.season != null && it.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.page == new.page
                        && old.season == new.season
                        && old.sort == new.sort
            }
            .flatMapLatest {
                mediaRepository.getSeasonalAnimePage(
                    animeSeason = it.season!!,
                    sort = listOf(it.sort),
                    page = it.page
                )
            }
            .onEach { result ->
                mutableUiState.update {
                    if (result is PagedResult.Success) {
                        if (it.page == 1) it.animeSeasonal.clear()
                        it.animeSeasonal.addAll(result.list)
                        it.copy(
                            hasNextPage = result.hasNextPage,
                            isLoading = false,
                        )
                    } else {
                        result.toUiState(loadingWhen = it.page == 1)
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}