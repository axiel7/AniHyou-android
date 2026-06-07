package com.axiel7.anihyou.feature.mediadetails.dubschedule

import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.network.api.TvdbEpisode
import com.axiel7.anihyou.core.network.api.TvdbSeriesResult

data class DubScheduleUiState(
    val tvdbSeries: TvdbSeriesResult? = null,
    val dubEpisodes: List<TvdbEpisode> = emptyList(),
    val selectedSeason: Int = 1,
    val isSearchingTvdb: Boolean = false,
    val tvdbNotFound: Boolean = false,
    val noApiKey: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : UiState() {
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setError(value: String?) = copy(error = value)
}
