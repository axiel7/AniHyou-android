package com.axiel7.anihyou.ui.screens.explore.season

import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.ui.common.UiState

data class SeasonAnimeUiState(
    val season: AnimeSeason,
    override val isLoading: Boolean = false,
    override val error: String? = null,
): UiState<SeasonAnimeUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
