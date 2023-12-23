package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.screens.usermedialist.composables.ListStatusSheet
import com.axiel7.anihyou.ui.screens.usermedialist.composables.NotesDialog
import com.axiel7.anihyou.ui.screens.usermedialist.composables.SortMenu
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

@Composable
fun UserMediaListHostView(
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val viewModel: UserMediaListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(isCompactScreen) {
        viewModel.setIsCompactScreen(isCompactScreen)
    }

    UserMediaListHostContent(
        media = viewModel.media,
        uiState = uiState,
        event = viewModel,
        modifier = modifier,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserMediaListHostContent(
    media: List<UserMediaListQuery.MediaList>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val statusSheetState = rememberModalBottomSheetState()
    val editSheetState = rememberModalBottomSheetState()

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val isFabVisible by remember {
        derivedStateOf {
            topAppBarScrollBehavior.state.heightOffset != topAppBarScrollBehavior.state.heightOffsetLimit
        }
    }
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    if (uiState.openNotesDialog) {
        NotesDialog(
            note = uiState.selectedItem?.basicMediaListEntry?.notes.orEmpty(),
            onDismiss = { event?.toggleNotesDialog(false) }
        )
    }

    if (statusSheetState.isVisible) {
        ListStatusSheet(
            selectedStatus = uiState.status,
            mediaType = uiState.mediaType,
            sheetState = statusSheetState,
            bottomPadding = bottomBarPadding,
            onStatusChanged = { event?.setStatus(it) }
        )
    }

    if (uiState.isMyList && editSheetState.isVisible && uiState.selectedItem != null) {
        EditMediaSheet(
            sheetState = editSheetState,
            mediaDetails = uiState.selectedItem!!.media!!.basicMediaDetails,
            listEntry = uiState.selectedItem!!.basicMediaListEntry,
            bottomPadding = bottomBarPadding,
            onDismiss = { updatedListEntry ->
                scope.launch {
                    event?.onUpdateListEntry(updatedListEntry)
                    editSheetState.hide()
                }
            }
        )
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = if (uiState.mediaType == MediaType.ANIME) stringResource(R.string.anime_list)
        else stringResource(R.string.manga_list),
        modifier = modifier,
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                modifier = Modifier.sizeIn(minWidth = 80.dp, minHeight = 56.dp),
                enter = slideInVertically(initialOffsetY = { it * 2 }),
                exit = slideOutVertically(targetOffsetY = { it * 2 }),
            ) {
                ExtendedFloatingActionButton(
                    onClick = { scope.launch { statusSheetState.show() } }
                ) {
                    Icon(
                        painter = painterResource(uiState.status.icon()),
                        contentDescription = stringResource(R.string.list_status),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = uiState.status.localized(mediaType = uiState.mediaType))
                }
            }
        },
        navigationIcon = {
            if (!uiState.isMyList) {
                BackIconButton(onClick = navActionManager::goBack)
            }
        },
        actions = {
            if (uiState.isMyList) {
                IconButton(
                    onClick = { navActionManager.toSearchOnMyList(uiState.mediaType) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search_24),
                        contentDescription = stringResource(R.string.search)
                    )
                }
            }
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopStart)
            ) {
                IconButton(onClick = { event?.toggleSortMenu(true) }) {
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
        scrollBehavior = topAppBarScrollBehavior,
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
                mediaList = media,
                uiState = uiState,
                event = event,
                contentPadding = if (!uiState.isMyList)
                    PaddingValues(bottom = padding.calculateBottomPadding())
                else PaddingValues(bottom = 8.dp),
                nestedScrollConnection = topAppBarScrollBehavior.nestedScrollConnection,
                navActionManager = navActionManager,
                onShowEditSheet = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    event?.selectItem(it)
                    scope.launch { editSheetState.show() }
                },
            )
        }//: Column
    }//: Scaffold
}

@Preview
@Composable
fun UserMediaListViewPreview() {
    AniHyouTheme {
        Surface {
            UserMediaListHostContent(
                media = emptyList(),
                uiState = UserMediaListUiState(
                    mediaType = MediaType.ANIME
                ),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}