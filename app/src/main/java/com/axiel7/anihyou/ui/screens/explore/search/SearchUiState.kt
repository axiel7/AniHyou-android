package com.axiel7.anihyou.ui.screens.explore.search

import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.data.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.data.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.state.PagedUiState

data class SearchUiState(
    val query: String = "",
    val searchType: SearchType,
    val mediaSort: MediaSort,
    val genresAndTagsForSearch: GenresAndTagsForSearch = GenresAndTagsForSearch(),
    val genresOrTagsChanged: Boolean = false,
    val selectedMediaFormats: List<MediaFormatLocalizable> = emptyList(),
    val mediaFormatsChanged: Boolean = false,
    val selectedMediaStatuses: List<MediaStatusLocalizable> = emptyList(),
    val mediaStatusesChanged: Boolean = false,
    val startYear: Int? = null,
    val endYear: Int? = null,
    val onMyList: Boolean? = null,

    override val page: Int = 0,
    override val hasNextPage: Boolean = false,
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : PagedUiState<SearchUiState> {

    val mediaType
        get() = when (searchType) {
            SearchType.ANIME -> MediaType.ANIME
            SearchType.MANGA -> MediaType.MANGA
            else -> null
        }

    val mediaSortForSearch
        get() = if (
            (genresAndTagsForSearch.genreIn.isNotEmpty()
                    || genresAndTagsForSearch.tagIn.isNotEmpty()
                    || selectedMediaFormats.isNotEmpty()
                    || selectedMediaStatuses.isNotEmpty()
                    || startYear != null
                    || endYear != null)
            && mediaSort == MediaSort.SEARCH_MATCH
        ) {
            MediaSort.POPULARITY_DESC
        } else mediaSort

    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
