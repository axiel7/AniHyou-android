package com.axiel7.anihyou.ui.screens.explore.search

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.genre.SelectableGenre
import com.axiel7.anihyou.data.model.media.MediaSortSearch
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.common.ErrorTextButton
import com.axiel7.anihyou.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.common.TriFilterChip
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchCountryChip
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchFormatChip
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchGenresChips
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchSortChip
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchStatusChip
import com.axiel7.anihyou.ui.screens.explore.search.composables.MediaSearchYearChip
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    initialGenre: String? = null,
    initialTag: String? = null,
    initialFocus: Boolean = false,
    navActionManager: NavActionManager,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var query by rememberSaveable { mutableStateOf("") }
    val performSearch = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(initialFocus) {
        if (initialFocus) focusRequester.requestFocus()
    }

    Surface(
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = modifier
                .statusBarsPadding()
                .fillMaxSize()
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = { Text(text = stringResource(R.string.anime_manga_and_more)) },
                leadingIcon = {
                    BackIconButton(onClick = navActionManager::goBack)
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                query = ""
                                performSearch.value = true
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { performSearch.value = true }
                ),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            SearchContentView(
                query = query,
                performSearch = performSearch,
                initialGenre = initialGenre,
                initialTag = initialTag,
                uiState = uiState,
                event = viewModel,
                navActionManager = navActionManager,
            )
        }//:Column
    }//:Surface
}

@Composable
fun SearchContentView(
    query: String,
    performSearch: MutableState<Boolean>,
    initialGenre: String?,
    initialTag: String?,
    uiState: SearchUiState,
    event: SearchEvent?,
    navActionManager: NavActionManager,
) {
    val listState = rememberLazyListState()
    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }

    var showMoreFilters by rememberSaveable { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    var showEditSheet by remember { mutableStateOf(false) }

    LaunchedEffect(performSearch.value) {
        if (performSearch.value) {
            listState.scrollToItem(0)
            event?.setQuery(query)
            performSearch.value = false
        }
    }

    if (showEditSheet && uiState.selectedMediaItem != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedMediaItem.basicMediaDetails,
            listEntry = uiState.selectedMediaItem.mediaListEntry?.basicMediaListEntry,
            onEntryUpdated = {
                event?.onUpdateListEntry(it)
            },
            onDismissed = { showEditSheet = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(),
        state = listState
    ) {
        item(contentType = 0) {
            Column(
                modifier = Modifier
                    .animateContentSize()
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 16.dp, top = 8.dp, end = 8.dp)
                ) {
                    SearchType.entries.forEach {
                        FilterSelectionChip(
                            selected = uiState.searchType == it,
                            text = it.localized(),
                            onClick = {
                                event?.setSearchType(it)
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
                if (uiState.searchType.isSearchMedia) {
                    MediaSearchSortChip(
                        mediaSortSearch = MediaSortSearch.valueOf(uiState.mediaSort)
                            ?: MediaSortSearch.SEARCH_MATCH,
                        onSortChanged = {
                            event?.setMediaSort(it)
                        }
                    )
                    if (showMoreFilters) {
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MediaSearchFormatChip(
                                mediaType = uiState.mediaType ?: MediaType.ANIME,
                                selectedMediaFormats = uiState.selectedMediaFormats,
                                onMediaFormatsChanged = { event?.setMediaFormats(it) }
                            )

                            MediaSearchStatusChip(
                                selectedMediaStatuses = uiState.selectedMediaStatuses,
                                onMediaStatusesChanged = { event?.setMediaStatuses(it) }
                            )

                            TriFilterChip(
                                text = stringResource(R.string.on_my_list),
                                value = uiState.onMyList,
                                onValueChanged = { event?.setOnMyList(it) },
                            )

                            MediaSearchCountryChip(
                                value = uiState.country,
                                onValueChanged = { event?.setCountry(it) }
                            )
                        }
                        MediaSearchYearChip(
                            startYear = uiState.startYear,
                            endYear = uiState.endYear,
                            onStartYearChanged = { event?.setStartYear(it) },
                            onEndYearChanged = { event?.setEndYear(it) }
                        )
                        MediaSearchGenresChips(
                            externalGenre = initialGenre?.let { SelectableGenre(name = it) },
                            externalTag = initialTag?.let { SelectableGenre(name = it) },
                            onGenreTagStateChanged = { event?.onGenreTagStateChanged(it) },
                        )
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TriFilterChip(
                                text = stringResource(R.string.doujinshi),
                                value = uiState.isDoujin,
                                onValueChanged = { event?.setIsDoujin(it) }
                            )
                            TriFilterChip(
                                text = stringResource(R.string.is_adult),
                                value = uiState.isAdult,
                                onValueChanged = { event?.setIsAdult(it) }
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = { showMoreFilters = !showMoreFilters }) {
                            Text(
                                text = stringResource(
                                    if (showMoreFilters) R.string.hide_filters
                                    else R.string.more_filters
                                )
                            )
                        }
                        ErrorTextButton(
                            text = stringResource(R.string.clear),
                            onClick = { event?.clearFilters() }
                        )
                    }
                }//:media filters
            }//:Column
        }
        when (uiState.searchType) {
            SearchType.ANIME, SearchType.MANGA -> {
                if (uiState.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = uiState.media,
                    contentType = { it }
                ) { item ->
                    MediaItemHorizontal(
                        title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                        imageUrl = item.coverImage?.large,
                        score = item.meanScore ?: 0,
                        format = item.format ?: MediaFormat.UNKNOWN__,
                        year = item.startDate?.year,
                        onClick = {
                            navActionManager.toMediaDetails(item.id)
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            event?.selectMediaItem(item)
                            showEditSheet = true
                        },
                        badgeContent = item.mediaListEntry?.basicMediaListEntry?.status?.let { status ->
                            {
                                Icon(
                                    painter = painterResource(status.icon()),
                                    contentDescription = status.localized()
                                )
                            }
                        }
                    )
                }
            }

            SearchType.CHARACTER -> {
                if (uiState.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = uiState.characters,
                    contentType = { it }
                ) { item ->
                    PersonItemHorizontal(
                        title = item.name?.userPreferred.orEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = item.image?.medium,
                        onClick = {
                            navActionManager.toCharacterDetails(item.id)
                        }
                    )
                }
            }

            SearchType.STAFF -> {
                if (uiState.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = uiState.staff,
                    contentType = { it }
                ) { item ->
                    PersonItemHorizontal(
                        title = item.name?.userPreferred.orEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = item.image?.medium,
                        onClick = {
                            navActionManager.toStaffDetails(item.id)
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
                }
                items(
                    items = uiState.studios,
                    contentType = { it }
                ) { item ->
                    Text(
                        text = item.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navActionManager.toStudioDetails(item.id) }
                            .padding(16.dp)
                    )
                }
            }

            SearchType.USER -> {
                if (uiState.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = uiState.users,
                    contentType = { it }
                ) { item ->
                    PersonItemHorizontal(
                        title = item.name,
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = item.avatar?.medium,
                        onClick = {
                            navActionManager.toUserDetails(item.id)
                        }
                    )
                }
            }
        }
    }//: LazyColumn
}

@Preview
@Composable
fun SearchPreview() {
    AniHyouTheme {
        Surface {
            SearchContentView(
                query = "",
                performSearch = remember { mutableStateOf(false) },
                initialGenre = null,
                initialTag = null,
                uiState = SearchUiState(
                    searchType = SearchType.ANIME,
                    mediaSort = MediaSort.SEARCH_MATCH
                ),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}