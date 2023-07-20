package com.axiel7.anihyou.ui.screens.explore.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.base.GenericLocalizable
import com.axiel7.anihyou.data.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.data.model.media.MediaSortSearch
import com.axiel7.anihyou.data.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.DialogWithCheckboxSelection
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.composables.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.OnMyListChip
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontalPlaceholder
import com.axiel7.anihyou.utils.DateUtils
import kotlinx.coroutines.launch

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
                SearchType.values().forEach {
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

@Composable
fun MediaSearchSortChip(
    viewModel: SearchViewModel,
    performSearch: MutableState<Boolean>,
) {
    var openDialog by remember { mutableStateOf(false) }
    var selectedSort by remember {
        mutableStateOf(MediaSortSearch.valueOf(viewModel.mediaSort) ?: MediaSortSearch.SEARCH_MATCH)
    }
    var isDescending by remember { mutableStateOf(true) }

    if (openDialog) {
        DialogWithRadioSelection(
            values = MediaSortSearch.values(),
            defaultValue = selectedSort,
            title = stringResource(R.string.sort),
            isDeselectable = false,
            onConfirm = {
                selectedSort = it!!
                viewModel.onMediaSortChanged(if (isDescending) it.desc else it.asc)
                openDialog = false
                performSearch.value = true
            },
            onDismiss = { openDialog = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = { openDialog = !openDialog },
            label = { Text(text = selectedSort.localized()) },
            modifier = Modifier.padding(8.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.sort_24),
                    contentDescription = stringResource(R.string.sort)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.arrow_drop_down_24),
                    contentDescription = "dropdown",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        )

        if (viewModel.mediaSort != MediaSort.SEARCH_MATCH) {
            AssistChip(
                onClick = {
                    isDescending = !isDescending
                    viewModel.onMediaSortChanged(if (isDescending) selectedSort.desc else selectedSort.asc)
                    performSearch.value = true
                },
                label = {
                    Text(
                        text = if (isDescending) stringResource(R.string.descending)
                        else stringResource(R.string.ascending)
                    )
                },
                modifier = Modifier.padding(8.dp),
            )
        }
    }//: Row
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MediaSearchGenresChips(
    viewModel: SearchViewModel,
    performSearch: MutableState<Boolean>,
    searchByGenre: MutableState<Boolean>
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    if (sheetState.isVisible) {
        GenresTagsSheet(
            viewModel = viewModel,
            sheetState = sheetState,
            bottomPadding = bottomBarPadding,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    if (viewModel.selectedGenres.isNotEmpty() || viewModel.selectedTags.isNotEmpty()) {
                        searchByGenre.value = true
                        performSearch.value = true
                    }
                }
            }
        )
    }

    FlowRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        viewModel.selectedGenres.forEach { (genre, isSelected) ->
            if (isSelected) {
                InputChip(
                    selected = false,
                    onClick = { viewModel.genreCollection[genre] = false },
                    label = { Text(text = genre) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close_20),
                            contentDescription = "remove"
                        )
                    }
                )
            }
        }
        viewModel.selectedTags.forEach { (tag, isSelected) ->
            if (isSelected) {
                InputChip(
                    selected = false,
                    onClick = { viewModel.tagCollection[tag] = false },
                    label = { Text(text = tag) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close_20),
                            contentDescription = "remove"
                        )
                    }
                )
            }
        }
        AssistChip(
            onClick = { scope.launch { sheetState.show() } },
            label = { Text(text = stringResource(R.string.add_genre)) },
            leadingIcon = {
                Icon(painter = painterResource(R.drawable.add_24), contentDescription = "add")
            }
        )
    }//: FlowRow
}

@Composable
fun MediaSearchFormatChip(
    viewModel: SearchViewModel
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithCheckboxSelection(
            values = MediaFormatLocalizable.values(),
            defaultValues = viewModel.selectedMediaFormats.toTypedArray(),
            title = stringResource(R.string.format),
            onConfirm = {
                openDialog = false
                viewModel.onMediaFormatChanged(it)
            },
            onDismiss = { openDialog = false }
        )
    }

    AssistChip(
        onClick = { openDialog = true },
        label = { Text(text = stringResource(R.string.format)) },
    )
}

@Composable
fun MediaSearchStatusChip(
    viewModel: SearchViewModel
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithCheckboxSelection(
            values = MediaStatusLocalizable.values(),
            defaultValues = viewModel.selectedMediaStatuses.toTypedArray(),
            title = stringResource(R.string.media_status),
            onConfirm = {
                openDialog = false
                viewModel.onMediaStatusChanged(it)
            },
            onDismiss = { openDialog = false }
        )
    }

    AssistChip(
        onClick = { openDialog = true },
        label = { Text(text = stringResource(R.string.media_status)) },
    )
}

@Composable
fun MediaSearchYearChip(
    viewModel: SearchViewModel
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithRadioSelection(
            values = DateUtils.seasonYears.map { GenericLocalizable(it) }.toTypedArray(),
            defaultValue = GenericLocalizable(viewModel.selectedYear),
            title = stringResource(R.string.year),
            isDeselectable = true,
            onConfirm = {
                openDialog = false
                viewModel.onYearChanged(it?.value)
            },
            onDismiss = { openDialog = false }
        )
    }

    AssistChip(
        onClick = { openDialog = true },
        label = { Text(text = stringResource(R.string.year)) }
    )
}