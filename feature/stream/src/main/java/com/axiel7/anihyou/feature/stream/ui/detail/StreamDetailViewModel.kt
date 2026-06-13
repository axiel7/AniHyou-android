package com.axiel7.anihyou.feature.stream.ui.detail

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.feature.stream.data.model.AnimeInfoResponse
import com.axiel7.anihyou.feature.stream.data.model.AudioType
import com.axiel7.anihyou.feature.stream.data.model.Episode
import com.axiel7.anihyou.feature.stream.data.model.EpisodeListResponse
import com.axiel7.anihyou.feature.stream.data.repository.StreamPreferencesRepository
import com.axiel7.anihyou.feature.stream.data.repository.StreamRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StreamDetailUiState(
    val animeId: Int = 0,
    val info: AnimeInfoResponse? = null,
    val episodeData: EpisodeListResponse? = null,
    val selectedProvider: String = "kiwi",
    val selectedAudio: AudioType = AudioType.SUB,
    val availableProviders: List<String> = emptyList(),
    val episodeList: List<Episode> = emptyList(),
    val watchedEpisodes: Set<Int> = emptySet(),
    val resumeEpisode: Int? = null,
    val resumePosition: Long = 0L,
    // Note dialog
    val noteDialogEpisode: Int? = null,
    val noteDialogText: String = "",
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}

class StreamDetailViewModel(
    private val streamRepository: StreamRepository,
    private val prefs: StreamPreferencesRepository,
) : UiStateViewModel<StreamDetailUiState>() {

    override val initialState = StreamDetailUiState()

    val watchedEpisodesFlow get() = mutableUiState.value.let { state ->
        prefs.getWatchedEpisodes(state.animeId)
    }

    fun load(animeId: Int) {
        mutableUiState.update { it.copy(animeId = animeId, isLoading = true) }

        viewModelScope.launch {
            // Load default provider preference
            prefs.defaultProvider.collect { provider ->
                mutableUiState.update { it.copy(selectedProvider = provider) }
            }
        }

        viewModelScope.launch {
            prefs.audioType.collect { audio ->
                mutableUiState.update { it.copy(selectedAudio = audio) }
                refreshEpisodeList()
            }
        }

        viewModelScope.launch {
            prefs.getProgress(animeId).collect { progress ->
                mutableUiState.update {
                    it.copy(
                        resumeEpisode = progress?.first,
                        resumePosition = progress?.second ?: 0L,
                    )
                }
            }
        }

        viewModelScope.launch {
            prefs.getWatchedEpisodes(animeId).collect { watched ->
                mutableUiState.update { it.copy(watchedEpisodes = watched) }
            }
        }

        viewModelScope.launch {
            // Load info and episodes in parallel
            launch {
                when (val r = streamRepository.getAnimeInfo(animeId)) {
                    is DataResult.Success -> mutableUiState.update { it.copy(info = r.data) }
                    is DataResult.Error -> mutableUiState.update { it.copy(error = r.message) }
                    else -> {}
                }
            }

            launch {
                when (val r = streamRepository.getEpisodes(animeId)) {
                    is DataResult.Success -> {
                        val providers = r.data.providers.keys.toList()
                        mutableUiState.update { state ->
                            val defaultProvider = state.selectedProvider
                                .takeIf { it in providers } ?: providers.firstOrNull() ?: "kiwi"
                            state.copy(
                                episodeData = r.data,
                                availableProviders = providers,
                                selectedProvider = defaultProvider,
                            )
                        }
                        refreshEpisodeList()
                    }
                    is DataResult.Error -> mutableUiState.update { it.copy(error = r.message) }
                    else -> {}
                }
            }

            mutableUiState.update { it.copy(isLoading = false) }
        }
    }

    fun selectProvider(provider: String) {
        mutableUiState.update { it.copy(selectedProvider = provider) }
        viewModelScope.launch { prefs.setDefaultProvider(provider) }
        refreshEpisodeList()
    }

    fun selectAudio(audio: AudioType) {
        mutableUiState.update { it.copy(selectedAudio = audio) }
        viewModelScope.launch { prefs.setAudioType(audio) }
        refreshEpisodeList()
    }

    private fun refreshEpisodeList() {
        val state = mutableUiState.value
        val epData = state.episodeData ?: return
        val providerData = epData.providers[state.selectedProvider] ?: return
        val episodes = when (state.selectedAudio) {
            AudioType.DUB -> providerData.episodes.dub.ifEmpty { providerData.episodes.sub }
            AudioType.SUB -> providerData.episodes.sub
        }
        mutableUiState.update { it.copy(episodeList = episodes) }
    }

    // ── Note management ───────────────────────────────────────────────────────

    fun openNoteDialog(episodeNumber: Int) {
        viewModelScope.launch {
            prefs.getNote(mutableUiState.value.animeId, episodeNumber).collect { note ->
                mutableUiState.update {
                    it.copy(noteDialogEpisode = episodeNumber, noteDialogText = note)
                }
            }
        }
    }

    fun onNoteTextChanged(text: String) {
        mutableUiState.update { it.copy(noteDialogText = text) }
    }

    fun saveNote() {
        val state = mutableUiState.value
        val ep = state.noteDialogEpisode ?: return
        viewModelScope.launch {
            prefs.saveNote(state.animeId, ep, state.noteDialogText)
            mutableUiState.update { it.copy(noteDialogEpisode = null) }
        }
    }

    fun dismissNoteDialog() {
        mutableUiState.update { it.copy(noteDialogEpisode = null) }
    }

    // ── Watch tracking ────────────────────────────────────────────────────────

    fun markWatched(episodeNumber: Int) {
        viewModelScope.launch {
            prefs.markEpisodeWatched(mutableUiState.value.animeId, episodeNumber)
        }
    }

    fun markUnwatched(episodeNumber: Int) {
        viewModelScope.launch {
            prefs.markEpisodeUnwatched(mutableUiState.value.animeId, episodeNumber)
        }
    }
}
