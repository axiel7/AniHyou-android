package com.axiel7.anihyou.feature.usermedialist

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.appBarWithSearchColors
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.common.utils.DateUtils
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.core.model.media.icon
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.network.type.MediaStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Route
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.chip.FilterChipWithMenu
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.ErrorTextButton
import com.axiel7.anihyou.core.ui.composables.common.singleClick
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.editmedia.composables.SetScoreDialog
import com.axiel7.anihyou.feature.usermedialist.composables.ListSelectSheet
import com.axiel7.anihyou.feature.usermedialist.composables.NotesDialog
import com.axiel7.anihyou.feature.usermedialist.composables.SortMenu
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun UserMediaListHostView(
    arguments: Route.UserMediaList,
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
) {
    val key = "${arguments.mediaType}${arguments.userId}"
    val viewModel: UserMediaListViewModel = if (arguments.userId == 0)
        koinActivityViewModel(key = key) { parametersOf(arguments) }
    else koinViewModel(key = key) { parametersOf(arguments) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UserMediaListHostContent(
        uiState = uiState,
        event = viewModel,
        isCompactScreen = isCompactScreen,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun UserMediaListHostContent(
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
) {
    val navActionManager = LocalNavActionManager.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val snackbarManager = rememberSnackbarManager(scope)
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val searchBarScrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
    val hasScrolledUp by remember {
        derivedStateOf {
            searchBarScrollBehavior.scrollState.scrollOffset != searchBarScrollBehavior.scrollState.scrollOffsetLimit
        }
    }
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var showListsSheet by rememberSaveable { mutableStateOf(false) }
    var showEditSheet by rememberSaveable { mutableStateOf(false) }

    if (uiState.openNotesDialog) {
        NotesDialog(
            note = uiState.selectedItem?.basicMediaListEntry?.notes.orEmpty(),
            onDismiss = { event?.toggleNotesDialog(false) }
        )
    }

    if (uiState.openSetScoreDialog) {
        SetScoreDialog(
            onDismiss = { event?.toggleScoreDialog(false) },
            onConfirm = { event?.setScore(it) },
        )
    }

    if (showListsSheet) {
        ListSelectSheet(
            uiState = uiState,
            scope = scope,
            bottomPadding = bottomBarPadding,
            onListChanged = { event?.onChangeList(it) },
            onDismiss = { showListsSheet = false }
        )
    }

    if (showEditSheet && uiState.isMyList && uiState.selectedItem?.media != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedItem.media!!.basicMediaDetails,
            listEntry = uiState.selectedItem.basicMediaListEntry,
            bottomPadding = bottomBarPadding,
            scope = scope,
            onEntryUpdated = {
                event?.onUpdateListEntry(it)
            },
            onDismissed = { showEditSheet = false }
        )
    }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    SideEffect(uiState.errorId) {
        uiState.errorId?.let(snackbarManager::showMessage)
    }

    SideEffect(uiState.randomEntryId) {
        uiState.randomEntryId?.let { id ->
            event?.onRandomEntryOpened()
            navActionManager.toMediaDetails(id)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopBar(
                uiState = uiState,
                event = event,
                scrollBehavior = searchBarScrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                uiState = uiState,
                hasScrolledUp = hasScrolledUp,
                showListsSheet = { showListsSheet = true },
                scrollToTop = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                        gridState.animateScrollToItem(0)
                    }
                }
            )
        },
        snackbarHost = snackbarManager::SnackbarHost,
        contentWindowInsets = if (!uiState.isMyList) WindowInsets.systemBars
        else WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = if (!uiState.isMyList) 0.dp
                    else padding.calculateBottomPadding()
                )
        ) {
            UserMediaListView(
                uiState = uiState,
                event = event,
                isCompactScreen = isCompactScreen,
                contentPadding = if (!uiState.isMyList)
                    PaddingValues(bottom = 58.dp + padding.calculateBottomPadding())
                else PaddingValues(bottom = 58.dp),
                nestedScrollConnection = searchBarScrollBehavior.nestedScrollConnection,
                navActionManager = navActionManager,
                onShowEditSheet = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    event?.selectItem(it)
                    showEditSheet = true
                },
                lazyListState = listState,
                lazyGridState = gridState,
            )
        }//: Column
    }//: Scaffold
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    scrollBehavior: SearchBarScrollBehavior,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val navActionManager = LocalNavActionManager.current
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val appBarWithSearchColors =
        appBarWithSearchColors(
            searchBarColors = SearchBarDefaults.containedColors(state = searchBarState)
        )
    val isSearchExpanded by remember {
        derivedStateOf { searchBarState.currentValue == SearchBarValue.Expanded }
    }

    SideEffect(isSearchExpanded) {
        if (!isSearchExpanded)
        event?.onSearch(textFieldState.text.toString())
    }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = {
                    keyboardController?.hide()
                    scope.launch {
                        searchBarState.animateToCollapsed()
                    }
                },
                placeholder = { Text(text = stringResource(R.string.search_my_list)) },
                leadingIcon = {
                    if (isSearchExpanded) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    textFieldState.clearText()
                                    searchBarState.animateToCollapsed()
                                }
                            },
                            shapes = IconButtonDefaults.shapes()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back_24),
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    } else {
                        BadgedBox(
                            badge = {
                                if (uiState.filterCount > 0) {
                                    Badge {
                                        Text(uiState.filterCount.toString())
                                    }
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.search_24),
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    }
                },
                trailingIcon = {
                    if (isSearchExpanded && textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textFieldState.clearText()
                                event?.onSearch("")
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
            )
        }

    Column {
        AppBarWithSearch(
            state = searchBarState,
            inputField = inputField,
            navigationIcon = {
                if (!uiState.isMyList) {
                    BackIconButton(onClick = navActionManager::goBack)
                }
            },
            actions = {
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopStart)
                ) {
                    IconButton(
                        onClick = { event?.toggleSortMenu(true) },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sort_24),
                            contentDescription = stringResource(R.string.sort)
                        )
                    }
                    SortMenu(
                        expanded = uiState.sortMenuExpanded,
                        sort = uiState.sort,
                        onDismiss = {
                            event?.toggleSortMenu(false)
                            event?.setSort(it)
                        }
                    )
                }
            },
            colors = appBarWithSearchColors,
            contentPadding = WindowInsets.statusBars.asPaddingValues(),
            windowInsets = SearchBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal),
            scrollBehavior = scrollBehavior,
        )
        ExpandedFullScreenContainedSearchBar(
            state = searchBarState,
            inputField = inputField,
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipWithMenu(
                    title = stringResource(R.string.format),
                    values = if (uiState.mediaType == MediaType.ANIME) MediaFormatLocalizable.animeEntries
                    else MediaFormatLocalizable.mangaEntries,
                    selectedValue = uiState.mediaFormat,
                    onValueSelected = { event?.setMediaFormat(it) },
                    valueString = { it.localized() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                FilterChipWithMenu(
                    title = stringResource(R.string.media_status),
                    values = MediaStatus.knownEntries,
                    selectedValue = uiState.mediaStatus,
                    onValueSelected = { event?.setMediaStatus(it) },
                    valueString = { it.localized() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                FilterChipWithMenu(
                    title = stringResource(R.string.country),
                    values = CountryOfOrigin.entries,
                    selectedValue = uiState.country,
                    onValueSelected = { event?.setCountry(it) },
                    valueString = { it.localized() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                FilterChipWithMenu(
                    title = stringResource(R.string.year),
                    values = DateUtils.seasonYears,
                    selectedValue = uiState.year,
                    onValueSelected = { event?.setYear(it) },
                    valueString = { it.toString() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                FilterChip(
                    selected = false,
                    onClick = singleClick { event?.getRandomEntry() },
                    label = { Text(text = stringResource(R.string.random)) },
                    enabled = !uiState.isLoadingRandom,
                )
            }
            if (uiState.filterCount > 0) {
                ErrorTextButton(
                    text = stringResource(R.string.clear),
                    onClick = { event?.clearFilters() },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun FloatingActionButton(
    uiState: UserMediaListUiState,
    hasScrolledUp: Boolean,
    showListsSheet: () -> Unit,
    scrollToTop: () -> Unit,
) {
    ExtendedFloatingActionButton(
        onClick = {
            if (hasScrolledUp) showListsSheet()
            else scrollToTop()
        },
        expanded = hasScrolledUp,
        text = {
            Text(
                text = uiState.status?.localized(uiState.mediaType)
                    ?: uiState.selectedListName ?: stringResource(R.string.all)
            )
        },
        icon = {
            if (!hasScrolledUp) {
                Icon(
                    painter = painterResource(R.drawable.arrow_upward_24),
                    contentDescription = null,
                )
            } else if (uiState.selectedListName == null || uiState.status != null) {
                Icon(
                    painter = painterResource(
                        id = uiState.status?.icon() ?: R.drawable.list_alt_24
                    ),
                    contentDescription = stringResource(R.string.list_status),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    )
}

@Preview
@Composable
private fun UserMediaListViewPreview() {
    AniHyouTheme {
        Surface {
            UserMediaListHostContent(
                uiState = UserMediaListUiState(
                    mediaType = MediaType.ANIME
                ),
                event = null,
                isCompactScreen = true,
            )
        }
    }
}