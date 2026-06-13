package com.axiel7.anihyou.feature.stream.ui.player

import androidx.lifecycle.viewModelScope
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.common.viewmodel.UiStateViewModel
import com.axiel7.anihyou.feature.stream.data.model.SkipInterval
import com.axiel7.anihyou.feature.stream.data.model.StreamSource
import com.axiel7.anihyou.feature.stream.data.model.StreamSourcesResponse
import com.axiel7.anihyou.feature.stream.data.model.Subtitle
import com.axiel7.anihyou.feature.stream.data.repository.StreamPreferencesRepository
import com.axiel7.anihyou.feature.stream.data.repository.StreamRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayerUiState(
    val animeId: Int = 0,
    val provider: String = "",
    val category: String = "sub",
    val episodeSlug: String = "",
    val episodeNumber: Int = 0,
    val totalEpisodes: Int = 0,

    // Sources
    val sourcesResponse: StreamSourcesResponse? = null,
    val hlsSources: List<StreamSource> = emptyList(),
    val selectedQuality: String = "1080p",
    val activeStreamUrl: String? = null,
    val activeReferer: String? = null,
    val subtitles: List<Subtitle> = emptyList(),

    // Skip
    val intro: SkipInterval? = null,
    val outro: SkipInterval? = null,
    val showSkipIntro: Boolean = false,
    val showSkipOutro: Boolean = false,

    // Playback state
    val resumePositionMs: Long = 0L,
    val currentPositionMs: Long = 0L,
    val autoPlay: Boolean = true,
    val autoNext: Boolean = true,
    val autoSkipIntro: Boolean = false,
    val autoSkipOutro: Boolean = false,

    // Quality options available from stream sources
    val availableQualities: List<String> = emptyList(),

    override val isLoading: Boolean = false,
    override val error: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)

    val hasNextEpisode: Boolean get() = totalEpisodes > 0 && episodeNumber < totalEpisodes
    val hasPreviousEpisode: Boolean get() = episodeNumber > 1
}

class PlayerViewModel(
    private val streamRepository: StreamRepository,
    private val prefs: StreamPreferencesRepository,
) : UiStateViewModel<PlayerUiState>() {

    override val initialState = PlayerUiState()

    fun load(
        animeId: Int,
        provider: String,
        category: String,
        episodeSlug: String,
        episodeNumber: Int,
        totalEpisodes: Int,
        resumePositionMs: Long = 0L,
    ) {
        viewModelScope.launch {
            val autoPlay = prefs.autoPlay.first()
            val autoNext = prefs.autoNext.first()
            val autoSkipIntro = prefs.autoSkipIntro.first()
            val autoSkipOutro = prefs.autoSkipOutro.first()
            val quality = prefs.preferredQuality.first()

            mutableUiState.update {
                it.copy(
                    animeId = animeId,
                    provider = provider,
                    category = category,
                    episodeSlug = episodeSlug,
                    episodeNumber = episodeNumber,
                    totalEpisodes = totalEpisodes,
                    resumePositionMs = resumePositionMs,
                    selectedQuality = quality,
                    autoPlay = autoPlay,
                    autoNext = autoNext,
                    autoSkipIntro = autoSkipIntro,
                    autoSkipOutro = autoSkipOutro,
                    isLoading = true,
                )
            }

            fetchSources(animeId, provider, category, episodeSlug)
        }
    }

    private suspend fun fetchSources(
        animeId: Int,
        provider: String,
        category: String,
        slug: String,
    ) {
        when (val result = streamRepository.getSources(provider, animeId, category, slug)) {
            is DataResult.Success -> {
                val response = result.data
                val hlsSources = response.streams.filter { it.type == "hls" }
                val qualities = hlsSources.mapNotNull { it.quality }.distinct()

                val preferred = mutableUiState.value.selectedQuality
                val bestSource = hlsSources.firstOrNull { it.quality == preferred }
                    ?: hlsSources.firstOrNull { it.isActive }
                    ?: hlsSources.firstOrNull()

                mutableUiState.update { state ->
                    state.copy(
                        sourcesResponse = response,
                        hlsSources = hlsSources,
                        availableQualities = qualities,
                        activeStreamUrl = bestSource?.url,
                        activeReferer = bestSource?.referer,
                        subtitles = response.subtitles,
                        intro = response.intro,
                        outro = response.outro,
                        isLoading = false,
                    )
                }
            }
            is DataResult.Error -> mutableUiState.update {
                it.copy(error = result.message, isLoading = false)
            }
            else -> {}
        }
    }

    // ── Quality selection ─────────────────────────────────────────────────────

    fun selectQuality(quality: String) {
        val source = mutableUiState.value.hlsSources.firstOrNull { it.quality == quality }
            ?: return
        viewModelScope.launch { prefs.setPreferredQuality(quality) }
        mutableUiState.update {
            it.copy(
                selectedQuality = quality,
                activeStreamUrl = source.url,
                activeReferer = source.referer,
            )
        }
    }

    // ── Skip controls ─────────────────────────────────────────────────────────

    /**
     * Called on every player position tick.
     * Updates showSkipIntro / showSkipOutro flags and auto-skips if enabled.
     * Returns the seek target in ms if an auto-skip should happen, else null.
     */
    fun onPositionChanged(positionMs: Long): Long? {
        val state = mutableUiState.value
        val posSeconds = (positionMs / 1000).toInt()

        val inIntro = state.intro?.let { posSeconds in it.start..it.end } ?: false
        val inOutro = state.outro?.let { posSeconds in it.start..it.end } ?: false

        mutableUiState.update {
            it.copy(
                currentPositionMs = positionMs,
                showSkipIntro = inIntro && !state.autoSkipIntro,
                showSkipOutro = inOutro && !state.autoSkipOutro,
            )
        }

        // Auto-skip
        if (inIntro && state.autoSkipIntro) {
            return (state.intro!!.end + 1).toLong() * 1000
        }
        if (inOutro && state.autoSkipOutro) {
            return (state.outro!!.end + 1).toLong() * 1000
        }
        return null
    }

    fun skipIntro(): Long? {
        val end = mutableUiState.value.intro?.end ?: return null
        mutableUiState.update { it.copy(showSkipIntro = false) }
        return (end + 1).toLong() * 1000
    }

    fun skipOutro(): Long? {
        val end = mutableUiState.value.outro?.end ?: return null
        mutableUiState.update { it.copy(showSkipOutro = false) }
        return (end + 1).toLong() * 1000
    }

    // ── Progress persistence ──────────────────────────────────────────────────

    fun saveProgress(positionMs: Long) {
        val state = mutableUiState.value
        if (state.animeId == 0) return
        viewModelScope.launch {
            prefs.saveProgress(state.animeId, state.episodeNumber, positionMs / 1000)
        }
    }

    fun markWatched() {
        val state = mutableUiState.value
        if (state.animeId == 0) return
        viewModelScope.launch {
            prefs.markEpisodeWatched(state.animeId, state.episodeNumber)
            // Clear resume position when episode completes
            prefs.saveProgress(state.animeId, state.episodeNumber + 1, 0L)
        }
    }

    // ── Settings toggles ──────────────────────────────────────────────────────

    fun toggleAutoPlay() {
        val new = !mutableUiState.value.autoPlay
        mutableUiState.update { it.copy(autoPlay = new) }
        viewModelScope.launch { prefs.setAutoPlay(new) }
    }

    fun toggleAutoNext() {
        val new = !mutableUiState.value.autoNext
        mutableUiState.update { it.copy(autoNext = new) }
        viewModelScope.launch { prefs.setAutoNext(new) }
    }

    fun toggleAutoSkipIntro() {
        val new = !mutableUiState.value.autoSkipIntro
        mutableUiState.update { it.copy(autoSkipIntro = new) }
        viewModelScope.launch { prefs.setAutoSkipIntro(new) }
    }

    fun toggleAutoSkipOutro() {
        val new = !mutableUiState.value.autoSkipOutro
        mutableUiState.update { it.copy(autoSkipOutro = new) }
        viewModelScope.launch { prefs.setAutoSkipOutro(new) }
    }
}
