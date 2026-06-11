package com.axiel7.anihyou.feature.mediadetails.episodes

import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.network.api.TmdbEpisode
import com.axiel7.anihyou.core.network.api.TmdbSeason

data class EpisodesUiState(
    override val error: String? = null,
    override val isLoading: Boolean = false,
    val tmdbId: String? = null,
    val seasons: List<TmdbSeason> = emptyList(),
    val selectedSeason: Int = 1,
    val episodes: List<TmdbEpisode> = emptyList(),
    val noApiKey: Boolean = false,
    val notFound: Boolean = false,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}
