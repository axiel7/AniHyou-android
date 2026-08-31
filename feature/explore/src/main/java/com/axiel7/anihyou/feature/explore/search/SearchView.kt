package com.axiel7.anihyou.feature.explore.search

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.animateFloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.SearchType
import com.axiel7.anihyou.core.model.genre.Genre
import com.axiel7.anihyou.core.model.genre.Tag
import com.axiel7.anihyou.core.model.media.MediaSortSearch
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Route
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.ErrorTextButton
import com.axiel7.anihyou.core.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.core.ui.composables.common.TriFilterChip
import com.axiel7.anihyou.core.ui.composables.common.singleClick
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
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchView(
    arguments: Route.Search,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier,
) {
    val navActionManager = LocalNavActionManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel: SearchViewModel = koinViewModel(parameters = { parametersOf(arguments, isLoggedIn) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldState = rememberTextFieldState()
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
                state = textFieldState,
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
                    IconButton(
                        onClick = singleClick(navActionManager::goBack),
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24),
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textFieldState.clearText()
                                performSearch.value = true
                            },
                            shapes = IconButtonDefaults.shapes()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                onKeyboardAction = KeyboardActionHandler { defaultAction ->
                    performSearch.value = true
                    keyboardController?.hide()
                    defaultAction()
                },
                lineLimits = TextFieldLineLimits.SingleLine,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            SearchContentView(
                textFieldState = textFieldState,
                performSearch = performSearch,
                initialGenre = arguments.genre,
                initialTag = arguments.tag,
                uiState = uiState,
                event = viewModel,
            )
        }//:Column
    }//:Surface
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchContentView(
    textFieldState: TextFieldState,
    performSearch: MutableState<Boolean>,
    initialGenre: String?,
    initialTag: String?,
    uiState: SearchUiState,
    event: SearchEvent?,
) {
    val navActionManager = LocalNavActionManager.current
    val blurAdult = LocalBlurAdult.current
    val scope = rememberCoroutineScope()
    val snackbarManager = rememberSnackbarManager()
    val listState = rememberLazyListState()
    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    var showMoreFilters by rememberSaveable { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    var showEditSheet by rememberSaveable { mutableStateOf(false) }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    LaunchedEffect(performSearch.value) {
        if (performSearch.value) {
            listState.scrollToItem(0)
            event?.setQuery(textFieldState.text.toString())
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
        snackbarHost = snackbarManager::SnackbarHost,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
                modifier = Modifier.animateFloatingActionButton(
                    visible = !isAtTop,
                    alignment = Alignment.BottomEnd,
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_upward_24),
                    contentDescription = null,
                )
            }
        },
        containerColor = Color.Transparent,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            contentPadding = padding,
            state = listState
        ) {
            item(key = "filters", contentType = "header") {
                Column {
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
                        AnimatedVisibility(showMoreFilters) {
                            Column {
                                MoreFilters(uiState, event, initialGenre, initialTag)
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
                        items(10, key = { "placeholder_$it" }, contentType = { "media_placeholder" }) {
                            MediaItemHorizontalPlaceholder()
                        }
                    }
                    items(
                        items = uiState.media,
                        key = { it.id },
                        contentType = { "media" }
                    ) { item ->
                        MediaItemHorizontal(
                            title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                            imageUrl = item.coverImage?.large,
                            blurImage = blurAdult && item.basicMediaDetails.isAdult == true,
                            score = item.meanScore ?: 0,
                            format = item.format ?: MediaFormat.UNKNOWN__,
                            year = item.startDate?.year,
                            mediaStatus = item.status,
                            episodes = item.episodes,
                            chapters = item.chapters,
                            duration = item.duration,
                            genres = item.genres?.filterNotNull(),
                            onClick = {
                                navActionManager.toMediaDetails(item.id)
                            },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                event?.selectMediaItem(item)
                                if (uiState.isLoggedIn) {
                                    showEditSheet = true
                                } else {
                                    snackbarManager.showNotLoggedInSnackbar()
                                }
                            },
                            status = item.mediaListEntry?.basicMediaListEntry?.status,
                        )
                    }
                }

                SearchType.CHARACTER -> {
                    if (uiState.isLoading) {
                        items(10, key = { "char_placeholder_$it" }, contentType = { "char_placeholder" }) {
                            PersonItemHorizontalPlaceholder()
                        }
                    }
                    items(
                        items = uiState.characters,
                        key = { it.id },
                        contentType = { "character" }
                    ) { item ->
                        PersonItemHorizontal(
                            title = item.name?.userPreferred.orEmpty(),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            imageUrl = item.image?.medium,
                            onClick = {
                                navActionManager.toCharacterDetails(item.id)
                            }
                        )
                    }
                }

                SearchType.STAFF -> {
                    if (uiState.isLoading) {
                        items(10, key = { "staff_placeholder_$it" }, contentType = { "staff_placeholder" }) {
                            PersonItemHorizontalPlaceholder()
                        }
                    }
                    items(
                        items = uiState.staff,
                        key = { it.id },
                        contentType = { "staff" }
                    ) { item ->
                        PersonItemHorizontal(
                            title = item.name?.userPreferred.orEmpty(),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            imageUrl = item.image?.medium,
                            onClick = {
                                navActionManager.toStaffDetails(item.id)
                            }
                        )
                    }
                }

                SearchType.STUDIO -> {
                    if (uiState.isLoading) {
                        items(10, key = { "studio_placeholder_$it" }, contentType = { "studio_placeholder" }) {
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
                        key = { it.id },
                        contentType = { "studio" }
                    ) { item ->
                        Surface(
                            onClick = { navActionManager.toStudioDetails(item.id) },
                            shape = MaterialTheme.shapes.large,
                            color = Color.Transparent,
                        ) {
                            Text(
                                text = item.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    }
                }

                SearchType.USER -> {
                    if (uiState.isLoading) {
                        items(10, key = { "user_placeholder_$it" }, contentType = { "user_placeholder" }) {
                            PersonItemHorizontalPlaceholder()
                        }
                    }
                    items(
                        items = uiState.users,
                        key = { it.id },
                        contentType = { "user" }
                    ) { item ->
                        PersonItemHorizontal(
                            title = item.name,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
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
        externalGenre = initialGenre?.let { Genre(it) },
        externalTag = initialTag?.let { Tag(it) },
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
            onValueChanged = { event?.setIsAdult(it) },
            enabled = uiState.isLoggedIn,
        )
    }
}

@Preview
@Composable
private fun SearchPreview() {
    AniHyouTheme {
        Surface {
            SearchContentView(
                textFieldState = rememberTextFieldState(),
                performSearch = remember { mutableStateOf(false) },
                initialGenre = null,
                initialTag = null,
                uiState = SearchUiState(
                    searchType = SearchType.ANIME,
                    mediaSort = MediaSort.SEARCH_MATCH,
                    isLoggedIn = false
                ),
                event = null,
            )
        }
    }
}
