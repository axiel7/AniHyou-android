package com.axiel7.anihyou.ui.screens.home.discover

import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.ui.common.state.UiState

data class DiscoverUiState(
    val nowAnimeSeason: AnimeSeason,
    val nextAnimeSeason: AnimeSeason,
    val airingOnMyList: Boolean? = null,
    val selectedMediaDetails: BasicMediaDetails? = null,
    val selectedMediaListEntry: BasicMediaListEntry? = null,
    val isLoadingAiring: Boolean = true,
    val isLoadingThisSeason: Boolean = true,
    val isLoadingTrendingAnime: Boolean = true,
    val isLoadingNextSeason: Boolean = true,
    val isLoadingTrendingManga: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : UiState<DiscoverUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
