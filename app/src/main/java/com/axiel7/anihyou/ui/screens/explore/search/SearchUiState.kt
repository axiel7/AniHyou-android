package com.axiel7.anihyou.ui.screens.explore.search

import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.SelectableGenre
import com.axiel7.anihyou.data.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.data.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.state.PagedUiState

data class SearchUiState(
    val query: String = "",
    val searchType: SearchType,
    val mediaSort: MediaSort,
    val genreCollection: List<SelectableGenre>,
    val tagCollection: List<SelectableGenre>,
    val isLoadingGenres: Boolean = true,
    val selectedMediaFormats: List<MediaFormatLocalizable> = emptyList(),
    val selectedMediaStatuses: List<MediaStatusLocalizable> = emptyList(),
    val selectedYear: Int? = null,
    val onMyList: Boolean = false,

    override val page: Int = 0,
    override val hasNextPage: Boolean = true,
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : PagedUiState<SearchUiState> {

    val mediaType
        get() = when (searchType) {
            SearchType.ANIME -> MediaType.ANIME
            SearchType.MANGA -> MediaType.MANGA
            else -> null
        }

    val selectedGenres get() = genreCollection.filter { it.isSelected }
    val selectedTags get() = tagCollection.filter { it.isSelected }

    // these are used to keep the initial genre or tag selected
    // when getting the entire collection from the api

    val externalGenre
        get() = if (genreCollection.size == 1) genreCollection.firstOrNull { it.isSelected } else null

    val externalTag
        get() = if (tagCollection.size == 1) tagCollection.firstOrNull { it.isSelected } else null

    val mediaSortForSearch
        get() = if (
            (selectedGenres.isNotEmpty()
                    || selectedTags.isNotEmpty()
                    || selectedMediaFormats.isNotEmpty()
                    || selectedMediaStatuses.isNotEmpty()
                    || selectedYear != null)
            && mediaSort == MediaSort.SEARCH_MATCH
        ) {
            MediaSort.POPULARITY_DESC
        } else mediaSort

    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
