package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.user.UserMediaListSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.screens.explore.MEDIA_TYPE_ARGUMENT
import com.axiel7.anihyou.ui.screens.login.LoginView
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.screens.profile.USER_ID_ARGUMENT
import com.axiel7.anihyou.ui.screens.usermedialist.composables.ListStatusSheet
import com.axiel7.anihyou.ui.screens.usermedialist.composables.NotesDialog
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

const val SCORE_FORMAT_ARGUMENT = "{score_format}"
const val USER_MEDIA_LIST_DESTINATION =
    "media_list/$USER_ID_ARGUMENT/$MEDIA_TYPE_ARGUMENT/$SCORE_FORMAT_ARGUMENT"

@Composable
fun UserMediaListHostViewEntry(
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (mediaId: Int) -> Unit,
    navigateBack: (() -> Unit)? = null,
) {
    val viewModel: UserMediaListViewModel = hiltViewModel()
    val accessToken by viewModel.accessToken.collectAsStateWithLifecycle()

    if (accessToken == null) {
        LoginView()
    } else {
        UserMediaListHostView(
            viewModel = viewModel,
            isCompactScreen = isCompactScreen,
            modifier = modifier,
            navigateToMediaDetails = navigateToMediaDetails,
            navigateBack = navigateBack,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserMediaListHostView(
    viewModel: UserMediaListViewModel,
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (mediaId: Int) -> Unit,
    navigateBack: (() -> Unit)? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            note = viewModel.selectedItem?.basicMediaListEntry?.notes.orEmpty(),
            onDismiss = { viewModel.toggleNotesDialog(false) }
        )
    }

    if (uiState.openSortDialog) {
        DialogWithRadioSelection(
            values = UserMediaListSort.entries.toTypedArray(),
            defaultValue = UserMediaListSort.valueOf(uiState.sort),
            title = stringResource(R.string.sort),
            isDeselectable = false,
            onConfirm = {
                viewModel.toggleSortDialog(false)
                viewModel.setSort(it!!.value)
            },
            onDismiss = { viewModel.toggleSortDialog(false) }
        )
    }

    if (statusSheetState.isVisible) {
        ListStatusSheet(
            selectedStatus = uiState.status,
            sheetState = statusSheetState,
            bottomPadding = bottomBarPadding,
            onStatusChanged = viewModel::setStatus
        )
    }

    if (viewModel.isMyList && editSheetState.isVisible && viewModel.selectedItem != null) {
        EditMediaSheet(
            sheetState = editSheetState,
            mediaDetails = viewModel.selectedItem!!.media!!.basicMediaDetails,
            listEntry = viewModel.selectedItem!!.basicMediaListEntry,
            bottomPadding = bottomBarPadding,
            onDismiss = { updatedListEntry ->
                scope.launch {
                    viewModel.onUpdateListEntry(updatedListEntry)
                    editSheetState.hide()
                }
            }
        )
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = if (viewModel.mediaType == MediaType.ANIME) stringResource(R.string.anime_list)
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
                    Text(text = uiState.status.localized())
                }
            }
        },
        navigationIcon = {
            if (navigateBack != null) {
                BackIconButton(onClick = navigateBack)
            }
        },
        actions = {
            IconButton(onClick = { viewModel.toggleSortDialog(true) }) {
                Icon(
                    painter = painterResource(R.drawable.sort_24),
                    contentDescription = stringResource(R.string.sort)
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior,
        contentWindowInsets = if (!viewModel.isMyList) WindowInsets.systemBars
        else WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = if (!viewModel.isMyList) 0.dp
                    else padding.calculateBottomPadding()
                )
        ) {
            UserMediaListView(
                mediaList = viewModel.media,
                status = uiState.status,
                scoreFormat = uiState.scoreFormat,
                listStyle = uiState.listStyle,
                itemsPerRowFlow = viewModel.itemsPerRow,
                isMyList = viewModel.isMyList,
                isLoading = uiState.isLoading,
                isRefreshing = uiState.fetchFromNetwork,
                showAsGrid = !isCompactScreen,
                contentPadding = if (!viewModel.isMyList)
                    PaddingValues(top = 8.dp, bottom = padding.calculateBottomPadding())
                else PaddingValues(vertical = 8.dp),
                nestedScrollConnection = topAppBarScrollBehavior.nestedScrollConnection,
                navigateToDetails = navigateToMediaDetails,
                onLoadMore = viewModel::loadNextPage,
                onRefresh = viewModel::refreshList,
                onShowEditSheet = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.selectItem(it)
                    scope.launch { editSheetState.show() }
                },
                onUpdateProgress = { entry ->
                    viewModel.updateEntryProgress(
                        entryId = entry.id,
                        progress = (entry.progress ?: 0) + 1
                    )
                },
                onClickNotes = {
                    viewModel.selectItem(it)
                    viewModel.toggleNotesDialog(true)
                }
            )
        }//: Column
    }//: Scaffold
}

@Preview
@Composable
fun UserMediaListViewPreview() {
    AniHyouTheme {
        Surface {
            UserMediaListHostView(
                viewModel = hiltViewModel(),
                isCompactScreen = true,
                navigateToMediaDetails = {}
            )
        }
    }
}