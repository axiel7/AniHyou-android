package com.axiel7.anihyou.ui.screens.explore.season

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.ui.common.UiStateViewModel
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SeasonAnimeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mediaRepository: MediaRepository,
) : UiStateViewModel<SeasonAnimeUiState>() {

    private val initialYear: Int = savedStateHandle[YEAR_ARGUMENT.removeFirstAndLast()]!!
    private val initialSeason = MediaSeason.valueOf(
        savedStateHandle[(SEASON_ARGUMENT.removeFirstAndLast())]!!
    )

    override val mutableUiState = MutableStateFlow(
        SeasonAnimeUiState(season = AnimeSeason(initialYear, initialSeason))
    )
    override val uiState = mutableUiState.asStateFlow()

    fun setSeason(value: AnimeSeason) = mutableUiState.update { it.copy(season = value) }

    @OptIn(ExperimentalCoroutinesApi::class)
    var animeSeasonal = uiState
        .flatMapLatest {
            mediaRepository.getSeasonalAnimePage(
                animeSeason = it.season
            )
        }
        .cachedIn(viewModelScope)
}