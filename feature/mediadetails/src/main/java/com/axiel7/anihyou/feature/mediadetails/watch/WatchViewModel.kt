package com.axiel7.anihyou.feature.mediadetails.watch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.streaming.model.Episode
import com.axiel7.anihyou.core.streaming.model.PlaybackInfo
import com.axiel7.anihyou.core.streaming.model.StreamingSource
import com.axiel7.anihyou.core.streaming.repository.StreamingRepository
import com.axiel7.anihyou.core.streaming.repository.StreamResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WatchUiState(
    val episodes: List<Episode> = emptyList(),
    val selectedEpisode: Episode? = null,
    val playbackInfo: PlaybackInfo? = null,
    val isDub: Boolean = false,
    val isLoadingEpisodes: Boolean = false,
    val isLoadingStream: Boolean = false,
    val error: String? = null,
    val source: StreamingSource = StreamingSource.ALL_ANIME,
)

class WatchViewModel(
    private val streamingRepository: StreamingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchUiState())
    val uiState: StateFlow<WatchUiState> = _uiState.asStateFlow()

    fun loadEpisodes(anilistId: Int, title: String, isDub: Boolean = _uiState.value.isDub) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingEpisodes = true, error = null) }
            streamingRepository.getEpisodes(
                anilistId = anilistId,
                title = title,
                isDub = isDub,
                source = _uiState.value.source,
            ).collect { result ->
                when (result) {
                    is StreamResult.Loading -> _uiState.update { it.copy(isLoadingEpisodes = true) }
                    is StreamResult.Error -> _uiState.update { it.copy(isLoadingEpisodes = false, error = result.message) }
                    is StreamResult.Success -> {
                        val episodes = result.data.episodes
                        _uiState.update {
                            it.copy(
                                isLoadingEpisodes = false,
                                episodes = episodes,
                                error = if (episodes.isEmpty()) "No episodes found on ${it.source.displayName}" else null,
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectEpisode(episode: Episode) {
        _uiState.update { it.copy(selectedEpisode = episode, playbackInfo = null, isLoadingStream = true) }
        viewModelScope.launch {
            streamingRepository.getPlaybackInfo(
                episodeId = episode.sourceEpisodeId,
                source = _uiState.value.source,
            ).collect { result ->
                when (result) {
                    is StreamResult.Loading -> _uiState.update { it.copy(isLoadingStream = true) }
                    is StreamResult.Error -> _uiState.update { it.copy(isLoadingStream = false, error = result.message) }
                    is StreamResult.Success -> _uiState.update {
                        it.copy(isLoadingStream = false, playbackInfo = result.data)
                    }
                }
            }
        }
    }

    fun toggleDub(anilistId: Int, title: String, isDub: Boolean) {
        _uiState.update { it.copy(isDub = isDub, episodes = emptyList(), selectedEpisode = null, playbackInfo = null) }
        loadEpisodes(anilistId, title, isDub)
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
