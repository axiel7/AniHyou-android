package com.axiel7.anihyou.feature.explore.search

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.axiel7.anihyou.core.model.SearchType
import com.axiel7.anihyou.core.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.core.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.core.network.SearchCharacterQuery
import com.axiel7.anihyou.core.network.SearchMediaQuery
import com.axiel7.anihyou.core.network.SearchStaffQuery
import com.axiel7.anihyou.core.network.SearchStudioQuery
import com.axiel7.anihyou.core.network.SearchUserQuery
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.ui.common.state.PagedUiState

@Stable
data class SearchUiState(
    val query: String = "",
    val searchType: SearchType,
    val media: SnapshotStateList<SearchMediaQuery.Medium> = mutableStateListOf(),
    val characters: SnapshotStateList<SearchCharacterQuery.Character> = mutableStateListOf(),
    val staff: SnapshotStateList<SearchStaffQuery.Staff> = mutableStateListOf(),
    val studios: SnapshotStateList<SearchStudioQuery.Studio> = mutableStateListOf(),
    val users: SnapshotStateList<SearchUserQuery.User> = mutableStateListOf(),
    val mediaSort: MediaSort,
    val genresAndTagsForSearch: GenresAndTagsForSearch = GenresAndTagsForSearch(),
    val genresOrTagsChanged: Boolean = false,
    val selectedMediaFormats: List<MediaFormatLocalizable> = emptyList(),
    val mediaFormatsChanged: Boolean = false,
    val selectedMediaStatuses: List<MediaStatusLocalizable> = emptyList(),
    val mediaStatusesChanged: Boolean = false,
    val startYear: Int? = null,
    val endYear: Int? = null,
    val season: MediaSeason? = null,
    val onMyList: Boolean? = null,
    val isDoujin: Boolean? = null,
    val isAdult: Boolean? = null,
    val country: CountryOfOrigin? = null,
    val selectedMediaItem: SearchMediaQuery.Medium? = null,
    val clearedFilters: Boolean = false,
    override val page: Int = 0,
    override val hasNextPage: Boolean = false,
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : PagedUiState() {

    val mediaType = when (searchType) {
        SearchType.ANIME -> MediaType.ANIME
        SearchType.MANGA -> MediaType.MANGA
        else -> null
    }

    private val hasGenreOrTagFilter = genresAndTagsForSearch.genreIn.isNotEmpty()
            || genresAndTagsForSearch.tagIn.isNotEmpty()

    private val hasMediaFormatFilter = selectedMediaFormats.isNotEmpty()

    private val hasMediaStatusFilter = selectedMediaStatuses.isNotEmpty()

    private val hasDateFilter = startYear != null || endYear != null || season != null

    val hasFiltersApplied = hasGenreOrTagFilter
            || hasMediaFormatFilter
            || hasMediaStatusFilter
            || hasDateFilter

    val mediaSortForSearch = if (mediaSort == MediaSort.SEARCH_MATCH && hasFiltersApplied) {
        MediaSort.POPULARITY_DESC
    } else {
        mediaSort
    }

    override fun setError(value: String?) = copy(error = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}
