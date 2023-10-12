package com.axiel7.anihyou.ui.screens.explore.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.SelectableGenre
import com.axiel7.anihyou.data.model.media.MediaSortSearch
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.OnMyListChip
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchFormatChip
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchGenresChips
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchSortChip
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchStatusChip
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchYearChip

@Composable
fun SearchView(
    query: String,
    performSearch: MutableState<Boolean>,
    initialMediaType: MediaType?,
    initialGenre: String?,
    initialTag: String?,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToStudioDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    var searchByGenre by remember { mutableStateOf(initialMediaType != null) }

    LaunchedEffect(performSearch.value) {
        if (performSearch.value) {
            if (query.isNotBlank() || searchByGenre
                || initialGenre != null || initialTag != null
                || uiState.mediaSort != MediaSort.SEARCH_MATCH
            ) {
                listState.scrollToItem(0)
                viewModel.setQuery(query)
                searchByGenre = false
            }
            performSearch.value = false
        }
    }

    listState.OnBottomReached(buffer = 3, onLoadMore = viewModel::loadNextPage)

    LazyColumn(
        state = listState
    ) {
        item(contentType = 0) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp)
            ) {
                SearchType.entries.forEach {
                    FilterSelectionChip(
                        selected = uiState.searchType == it,
                        text = it.localized(),
                        onClick = {
                            viewModel.setSearchType(it)
                            //performSearch.value = true
                        }
                    )
                }
            }
            if (uiState.searchType.isSearchMedia) {
                MediaSearchSortChip(
                    mediaSortSearch = MediaSortSearch.valueOf(uiState.mediaSort)
                        ?: MediaSortSearch.SEARCH_MATCH,
                    onSortChanged = {
                        viewModel.setMediaSort(it)
                    }
                )
                MediaSearchGenresChips(
                    externalGenre = initialGenre?.let { SelectableGenre(name = it) },
                    externalTag = initialTag?.let { SelectableGenre(name = it) },
                    onGenreTagSelected = viewModel::setSelectedGenresAndTags,
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.size(0.dp))

                    MediaSearchFormatChip(
                        mediaType = uiState.mediaType ?: MediaType.ANIME,
                        selectedMediaFormats = uiState.selectedMediaFormats,
                        onMediaFormatsChanged = viewModel::setMediaFormats
                    )

                    MediaSearchStatusChip(
                        selectedMediaStatuses = uiState.selectedMediaStatuses,
                        onMediaStatusesChanged = viewModel::setMediaStatuses
                    )

                    MediaSearchYearChip(
                        selectedYear = uiState.selectedYear,
                        onYearChanged = viewModel::setYear
                    )

                    OnMyListChip(
                        selected = uiState.onMyList,
                        onClick = {
                            viewModel.setOnMyList(uiState.onMyList.not())
                        }
                    )
                }
            }
        }
        when (uiState.searchType) {
            SearchType.ANIME, SearchType.MANGA -> {
                if (uiState.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                } else items(
                    items = viewModel.media,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    MediaItemHorizontal(
                        title = item.title?.userPreferred ?: "",
                        imageUrl = item.coverImage?.large,
                        score = item.meanScore ?: 0,
                        format = item.format ?: MediaFormat.UNKNOWN__,
                        year = item.startDate?.year,
                        onClick = {
                            navigateToMediaDetails(item.id)
                        }
                    )
                }
            }

            SearchType.CHARACTER -> {
                if (uiState.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                } else items(
                    items = viewModel.characters,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    PersonItemHorizontal(
                        title = item.name?.userPreferred ?: "",
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = item.image?.medium,
                        onClick = {
                            navigateToCharacterDetails(item.id)
                        }
                    )
                }
            }

            SearchType.STAFF -> {
                if (uiState.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                } else items(
                    items = viewModel.staff,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    PersonItemHorizontal(
                        title = item.name?.userPreferred ?: "",
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = item.image?.medium,
                        onClick = {
                            navigateToStaffDetails(item.id)
                        }
                    )
                }
            }

            SearchType.STUDIO -> {
                if (uiState.isLoading) {
                    items(10) {
                        Text(
                            text = "Loading placeholder",
                            modifier = Modifier
                                .padding(16.dp)
                                .defaultPlaceholder(visible = true)
                        )
                    }
                } else items(
                    items = viewModel.studios,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    Text(
                        text = item.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navigateToStudioDetails(item.id) }
                            .padding(16.dp)
                    )
                }
            }

            SearchType.USER -> {
                if (uiState.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                } else items(
                    items = viewModel.users,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    PersonItemHorizontal(
                        title = item.name,
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = item.avatar?.medium,
                        onClick = {
                            navigateToUserDetails(item.id)
                        }
                    )
                }
            }
        }
    }//: LazyColumn
}