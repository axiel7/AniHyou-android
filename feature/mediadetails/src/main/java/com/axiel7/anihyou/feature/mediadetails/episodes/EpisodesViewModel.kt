package com.axiel7.anihyou.feature.mediadetails.episodes

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.core.network.api.TmdbApi
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EpisodesViewModel(
    private val tmdbApi: TmdbApi,
) : UiStateViewModel<EpisodesUiState>() {

    override val initialState = EpisodesUiState()

    fun load(englishTitle: String?, romajiTitle: String?, nativeTitle: String?) {
        viewModelScope.launch {
            mutableUiState.update { it.copy(isLoading = true, notFound = false, noApiKey = false) }

            if (tmdbApi.isKeyMissing()) {
                mutableUiState.update { it.copy(isLoading = false, noApiKey = true) }
                return@launch
            }

            val query = (englishTitle ?: romajiTitle ?: nativeTitle ?: "").trim()
            if (query.isBlank()) {
                mutableUiState.update { it.copy(isLoading = false, notFound = true) }
                return@launch
            }

            val tmdbId = tmdbApi.findShowId(query)
            if (tmdbId == null) {
                mutableUiState.update { it.copy(isLoading = false, notFound = true) }
                return@launch
            }

            val seasons = tmdbApi.getSeasons(tmdbId)
            val firstSeason = seasons.firstOrNull()?.number ?: 1
            mutableUiState.update {
                it.copy(tmdbId = tmdbId, seasons = seasons, selectedSeason = firstSeason)
            }
            loadSeasonEpisodes(tmdbId, firstSeason)
        }
    }

    fun selectSeason(seasonNumber: Int) {
        viewModelScope.launch {
            val id = mutableUiState.value.tmdbId ?: return@launch
            mutableUiState.update { it.copy(selectedSeason = seasonNumber, isLoading = true) }
            loadSeasonEpisodes(id, seasonNumber)
        }
    }

    private suspend fun loadSeasonEpisodes(tmdbId: String, season: Int) {
        val episodes = tmdbApi.getEpisodes(tmdbId, season)
        mutableUiState.update { it.copy(isLoading = false, episodes = episodes) }
    }
}
