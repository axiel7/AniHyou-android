package com.axiel7.anihyou.feature.usermedialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
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
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.ErrorTextButton
import com.axiel7.anihyou.core.ui.composables.common.singleClick
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.editmedia.composables.SetScoreDialog
import com.axiel7.anihyou.feature.genrestags.composables.SearchGenresTagsChips
import com.axiel7.anihyou.feature.usermedialist.composables.ListSelectSheet
import com.axiel7.anihyou.feature.usermedialist.composables.NotesDialog
import com.axiel7.anihyou.feature.usermedialist.search.TopSearchBar
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinActivityViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.milliseconds

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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val hasScrolledUp by remember {
        derivedStateOf {
            scrollBehavior.state.collapsedFraction > 0.5f || listState.firstVisibleItemIndex == 0
        }
    }
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var showListsSheet by rememberSaveable { mutableStateOf(false) }
    var showEditSheet by rememberSaveable { mutableStateOf(false) }
    var isSearchFocused by rememberSaveable { mutableStateOf(false) }

    val textFieldState = rememberTextFieldState()
    val currentUiState by rememberUpdatedState(uiState)


    @OptIn(FlowPreview::class)
    LaunchedEffect(textFieldState) {
        snapshotFlow {
            textFieldState.text.toString() to listOf(
                currentUiState.mediaFormat,
                currentUiState.mediaStatus,
                currentUiState.country,
                currentUiState.year,
                currentUiState.genresAndTagsForSearch,
                currentUiState.clearedFilters
            )
        }
            .distinctUntilChanged()
            .debounce(300.milliseconds)
            .collectLatest { (query, _) ->
                event?.onSearch(query)
            }
    }

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
            TopSearchBar(
                uiState = uiState,
                event = event,
                textFieldState = textFieldState,
                isSearchFocused = isSearchFocused,
                onFocusChange = { isSearchFocused = it },
                scrollBehavior = scrollBehavior
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
                nestedScrollConnection = scrollBehavior.nestedScrollConnection,
                navActionManager = navActionManager,
                onShowEditSheet = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    event?.selectItem(it)
                    showEditSheet = true
                },
                lazyListState = listState,
                lazyGridState = gridState,
                stickyHeaderContent = {
                    FilterBlock(
                        uiState = uiState,
                        isSearchFocused = isSearchFocused,
                        event = event
                    )
                }
            )
        }//: Column
    }//: Scaffold
}


@Composable
private fun FilterBlock(
    uiState: UserMediaListUiState,
    isSearchFocused: Boolean,
    event: UserMediaListEvent?
    ){
    val fastSize = spring<IntSize>(stiffness = Spring.StiffnessMedium)
    val fastAlpha = tween<Float>(100)
    val isPreview = LocalInspectionMode.current
    Column(
            modifier = Modifier.animateContentSize(animationSpec = fastSize)
        ) {
            val chipEnter = fadeIn(fastAlpha) + expandHorizontally(fastSize)
            val chipExit = fadeOut(fastAlpha) + shrinkHorizontally(fastSize)

            val rowEnter = fadeIn(fastAlpha) + expandVertically(fastSize)
            val rowExit = fadeOut(fastAlpha) + shrinkVertically(fastSize)


            AnimatedVisibility(
                visible = isSearchFocused || uiState.filterCount > 0,
                enter = rowEnter,
                exit = rowExit
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .animateContentSize(animationSpec = fastSize),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    AnimatedVisibility(
                        visible = isSearchFocused || uiState.mediaFormat != null,
                        enter = chipEnter, exit = chipExit
                    ) {
                        FilterChipWithMenu(
                            title = stringResource(R.string.format),
                            values = if (uiState.mediaType == MediaType.ANIME) MediaFormatLocalizable.animeEntries
                            else MediaFormatLocalizable.mangaEntries,
                            selectedValue = uiState.mediaFormat,
                            onValueSelected = { event?.setMediaFormat(it) },
                            valueString = { it.localized() },
                        )
                    }

                    AnimatedVisibility(
                        visible = isSearchFocused || uiState.mediaStatus != null,
                        enter = chipEnter, exit = chipExit
                    ) {
                        FilterChipWithMenu(
                            title = stringResource(R.string.media_status),
                            values = MediaStatus.knownEntries,
                            selectedValue = uiState.mediaStatus,
                            onValueSelected = { event?.setMediaStatus(it) },
                            valueString = { it.localized() },
                        )
                    }

                    AnimatedVisibility(
                        visible = isSearchFocused || uiState.country != null,
                        enter = chipEnter, exit = chipExit
                    ) {
                        FilterChipWithMenu(
                            title = stringResource(R.string.country),
                            values = CountryOfOrigin.entries,
                            selectedValue = uiState.country,
                            onValueSelected = { event?.setCountry(it) },
                            valueString = { it.localized() },
                        )
                    }

                    AnimatedVisibility(
                        visible = isSearchFocused || uiState.year != null,
                        enter = chipEnter, exit = chipExit
                    ) {
                        FilterChipWithMenu(
                            title = stringResource(R.string.year),
                            values = DateUtils.seasonYears,
                            selectedValue = uiState.year,
                            onValueSelected = { event?.setYear(it) },
                            valueString = { it.toString() },
                        )
                    }

                    AnimatedVisibility(
                        visible = isSearchFocused,
                        enter = chipEnter, exit = chipExit
                    ) {
                        FilterChip(
                            selected = false,
                            onClick = singleClick { event?.getRandomEntry() },
                            label = { Text(text = stringResource(R.string.random)) },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.shuffle_24),
                                    contentDescription = stringResource(R.string.random),
                                    tint = MaterialTheme.colorScheme.onSurface,
                                )
                            },
                            enabled = !uiState.isLoadingRandom,
                        )
                    }
                }
            }

            val hasGenreTags = uiState.genresAndTagsForSearch.genreIn.isNotEmpty() ||
                    uiState.genresAndTagsForSearch.genreNot.isNotEmpty() ||
                    uiState.genresAndTagsForSearch.tagIn.isNotEmpty() ||
                    uiState.genresAndTagsForSearch.tagNot.isNotEmpty()

            AnimatedVisibility(
                visible = !isPreview && (isSearchFocused || hasGenreTags),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                SearchGenresTagsChips(
                    clearedFilters = uiState.clearedFilters,
                    onGenreTagStateChanged = { event?.onGenreTagStateChanged(it) },
                )
            }

            AnimatedVisibility(
                visible = uiState.filterCount > 0,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ErrorTextButton(
                    text = stringResource(R.string.clear),
                    onClick = { event?.clearFilters() },
                )
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