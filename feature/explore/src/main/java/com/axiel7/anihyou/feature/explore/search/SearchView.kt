package com.axiel7.anihyou.feature.explore.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.SearchType
import com.axiel7.anihyou.core.model.genre.SelectableGenre
import com.axiel7.anihyou.core.model.media.MediaSortSearch
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorTextButton
import com.axiel7.anihyou.core.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.core.ui.composables.common.TriFilterChip
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.core.ui.composables.person.PersonItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.explore.search.composables.MediaSearchCountryChip
import com.axiel7.anihyou.feature.explore.search.composables.MediaSearchDateChip
import com.axiel7.anihyou.feature.explore.search.composables.MediaSearchDurationChip
import com.axiel7.anihyou.feature.explore.search.composables.MediaSearchFormatChip
import com.axiel7.anihyou.feature.explore.search.composables.MediaSearchGenresChips
import com.axiel7.anihyou.feature.explore.search.composables.MediaSearchSortChip
import com.axiel7.anihyou.feature.explore.search.composables.MediaSearchSourcesChip
import com.axiel7.anihyou.feature.explore.search.composables.MediaSearchStatusChip
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SearchView(
    arguments: Routes.Search,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val viewModel: SearchViewModel = koinViewModel(parameters = { parametersOf(arguments) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var query by rememberSaveable { mutableStateOf("") }
    val performSearch = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(arguments.focus) {
        if (arguments.focus) focusRequester.requestFocus()
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
                placeholder = {
                    if (arguments.onList == true && uiState.onMyList == true) {
                        Text(text = stringResource(R.string.search_my_list))
                    } else {
                        Text(text = stringResource(R.string.anime_manga_and_more))
                    }
                },
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
                initialGenre = arguments.genre,
                initialTag = arguments.tag,
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
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    var showMoreFilters by rememberSaveable { mutableStateOf(true) }

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

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = !isAtTop,
                enter = fadeIn() + scaleIn(),
                exit = scaleOut() + fadeOut(),
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_upward_24),
                        contentDescription = null,
                    )
                }
            }
        },
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
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
                            MoreFilters(uiState, event, initialGenre, initialTag)
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
                            status = item.mediaListEntry?.basicMediaListEntry?.status,
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
}

@Composable
private fun MoreFilters(
    uiState: SearchUiState,
    event: SearchEvent?,
    initialGenre: String?,
    initialTag: String?,
) {
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

        MediaSearchSourcesChip(
            selectedSources = uiState.selectedSources,
            onSourcesChanged = { event?.setSources(it) }
        )
    }
    MediaSearchDateChip(
        startYear = uiState.startYear,
        endYear = uiState.endYear,
        season = uiState.season,
        onStartYearChanged = { event?.setStartYear(it) },
        onEndYearChanged = { event?.setEndYear(it) },
        onSeasonChanged = { event?.setSeason(it) },
    )
    MediaSearchDurationChip(
        mediaType = uiState.mediaType ?: MediaType.UNKNOWN__,
        minEpCh = uiState.minEpCh,
        maxEpCh = uiState.maxEpCh,
        minDuration = uiState.minDuration,
        maxDuration = uiState.maxDuration,
        setEpCh = { event?.setEpCh(it) },
        setDuration = { event?.setDuration(it) },
    )
    MediaSearchGenresChips(
        externalGenre = initialGenre?.let { SelectableGenre(name = it) },
        externalTag = initialTag?.let { SelectableGenre(name = it) },
        clearedFilters = uiState.clearedFilters,
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