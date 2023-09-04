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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.OnMyListChip
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
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
    initialMediaSort: MediaSort?,
    initialGenre: String?,
    initialTag: String?,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToStudioDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
) {
    val viewModel = viewModel {
        SearchViewModel(
            initialMediaSort = initialMediaSort,
            initialMediaType = initialMediaType,
            initialGenre = initialGenre,
            initialTag = initialTag
        )
    }
    val listState = rememberLazyListState()
    val searchByGenre = remember { mutableStateOf(initialMediaType != null) }

    LaunchedEffect(performSearch.value) {
        if (performSearch.value) {
            if (query.isNotBlank() || searchByGenre.value
                || initialGenre != null || initialTag != null
                || viewModel.mediaSort != MediaSort.SEARCH_MATCH
            ) {
                listState.scrollToItem(0)
                viewModel.runSearch(query)
                searchByGenre.value = false
            }
            performSearch.value = false
        }
    }

    listState.OnBottomReached(buffer = 2) {
        if ((viewModel.searchType == SearchType.ANIME || viewModel.searchType == SearchType.MANGA)
            && viewModel.searchedMedia.isNotEmpty()
        ) {
            viewModel.searchMedia(
                mediaType = if (viewModel.searchType == SearchType.ANIME) MediaType.ANIME
                else MediaType.MANGA,
                query = query,
                resetPage = false
            )
        }
    }

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
                        selected = viewModel.searchType == it,
                        text = it.localized(),
                        onClick = {
                            viewModel.onSearchTypeChanged(it)
                            performSearch.value = true
                        }
                    )
                }
            }
            if (viewModel.searchType == SearchType.ANIME || viewModel.searchType == SearchType.MANGA) {
                MediaSearchSortChip(
                    viewModel = viewModel,
                    performSearch = performSearch
                )
                MediaSearchGenresChips(
                    viewModel = viewModel,
                    performSearch = performSearch,
                    searchByGenre = searchByGenre,
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(modifier = Modifier.size(0.dp))

                    MediaSearchFormatChip(viewModel = viewModel)

                    MediaSearchStatusChip(viewModel = viewModel)

                    MediaSearchYearChip(viewModel = viewModel)

                    OnMyListChip(
                        selected = viewModel.onMyList,
                        onClick = {
                            viewModel.onMyListChanged(!viewModel.onMyList)
                        }
                    )
                }
            }
        }
        when (viewModel.searchType) {
            SearchType.ANIME, SearchType.MANGA -> {
                items(
                    items = viewModel.searchedMedia,
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
                if (viewModel.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                }
            }

            SearchType.CHARACTER -> {
                items(
                    items = viewModel.searchedCharacters,
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
                if (viewModel.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                }
            }

            SearchType.STAFF -> {
                items(
                    items = viewModel.searchedStaff,
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
                if (viewModel.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                }
            }

            SearchType.STUDIO -> {
                items(
                    items = viewModel.searchedStudios,
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
                if (viewModel.isLoading) {
                    items(10) {
                        Text(
                            text = "Loading placeholder",
                            modifier = Modifier
                                .padding(16.dp)
                                .defaultPlaceholder(visible = true)
                        )
                    }
                }
            }

            SearchType.USER -> {
                items(
                    items = viewModel.searchedUsers,
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
                if (viewModel.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                }
            }
        }
    }//: LazyColumn
}