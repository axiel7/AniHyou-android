package com.axiel7.anihyou.feature.mediadetails.dubschedule

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.domain.repository.DubScheduleRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DubScheduleViewModel(
    private val dubScheduleRepository: DubScheduleRepository,
) : UiStateViewModel<DubScheduleUiState>(), DubScheduleEvent {

    override val initialState = DubScheduleUiState()

    override fun loadDubSchedule(
        englishTitle: String?,
        romajiTitle: String?,
        season: Int,
    ) {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, tvdbNotFound = false, noApiKey = false) }

            val seriesResult = dubScheduleRepository.findTvdbSeries(englishTitle, romajiTitle)

            if (seriesResult is DataResult.Error || seriesResult !is DataResult.Success) {
                val msg = (seriesResult as? DataResult.Error)?.message ?: ""
                val isKeyMissing = msg == "NO_API_KEY"
                mutableUiState.update {
                    it.copy(
                        isLoading = false,
                        noApiKey = isKeyMissing,
                        tvdbNotFound = !isKeyMissing,
                        error = if (isKeyMissing) null else msg.takeIf { it != "No title available" },
                    )
                }
                return@launch
            }

            val series = seriesResult.data
            val tvdbId = series.tvdb_id?.toIntOrNull()

            if (tvdbId == null) {
                mutableUiState.update { it.copy(isLoading = false, tvdbNotFound = true) }
                return@launch
            }

            mutableUiState.update { it.copy(tvdbSeries = series) }

            val episodesResult = dubScheduleRepository.getDubSchedule(tvdbId, season)

            mutableUiState.update {
                when (episodesResult) {
                    is DataResult.Success -> it.copy(
                        dubEpisodes = episodesResult.data,
                        selectedSeason = season,
                        isLoading = false,
                    )
                    is DataResult.Error -> it.copy(
                        isLoading = false,
                        error = episodesResult.message,
                    )
                    else -> it.copy(isLoading = false)
                }
            }
        }
    }

    override fun changeSeason(season: Int) {
        val tvdbId = uiState.value.tvdbSeries?.tvdb_id?.toIntOrNull() ?: return
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, selectedSeason = season) }
            val result = dubScheduleRepository.getDubSchedule(tvdbId, season)
            mutableUiState.update {
                when (result) {
                    is DataResult.Success -> it.copy(dubEpisodes = result.data, isLoading = false)
                    is DataResult.Error -> it.copy(isLoading = false, error = result.message)
                    else -> it.copy(isLoading = false)
                }
            }
        }
    }

    override fun onErrorDisplayed() {
        mutableUiState.update { it.copy(error = null) }
    }
}
