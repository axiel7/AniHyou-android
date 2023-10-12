package com.axiel7.anihyou.ui.screens.explore.search.genretag

import com.axiel7.anihyou.data.model.SelectableGenre
import com.axiel7.anihyou.ui.common.state.UiState

data class GenresTagsUiState(
    val externalGenre: SelectableGenre? = null,
    val externalTag: SelectableGenre? = null,
    override val error: String? = null,
    override val isLoading: Boolean = true
) : UiState<GenresTagsUiState> {
    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
