package com.axiel7.anihyou.feature.mediadetails.episodes

import com.axiel7.anihyou.core.base.state.UiState

data class TmdbSeason(
    val number: Int,
    val name: String,
    val episodeCount: Int,
    val posterPath: String?,
)

data class TmdbEpisode(
    val number: Int,
    val name: String,
    val overview: String,
    val stillPath: String?,
    val rating: Double?,
    val airDate: String?,
)

data class EpisodesUiState(
    override val error: String? = null,
    override val isLoading: Boolean = false,
    val tmdbId: String? = null,
    val seasons: List<TmdbSeason> = emptyList(),
    val selectedSeason: Int = 1,
    val episodes: List<TmdbEpisode> = emptyList(),
    val noApiKey: Boolean = false,
    val notFound: Boolean = false,
    val source: String = "tmdb",
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}
