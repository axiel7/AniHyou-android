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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.user.UserMediaListSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.screens.explore.MEDIA_TYPE_ARGUMENT
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.screens.profile.USER_ID_ARGUMENT
import com.axiel7.anihyou.ui.screens.usermedialist.composables.ListStatusSheet
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

const val USER_MEDIA_LIST_DESTINATION = "media_list/$USER_ID_ARGUMENT/$MEDIA_TYPE_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMediaListHostView(
    mediaType: MediaType,
    modifier: Modifier = Modifier,
    userId: Int? = null,
    navigateToMediaDetails: (mediaId: Int) -> Unit,
    navigateBack: (() -> Unit)? = null,
) {
    val viewModel: UserMediaListViewModel = viewModel {
        UserMediaListViewModel(mediaType, userId)
    }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var openSortDialog by remember { mutableStateOf(false) }

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
    val isMyList = remember { userId == null }
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    if (openSortDialog) {
        DialogWithRadioSelection(
            values = UserMediaListSort.values(),
            defaultValue = UserMediaListSort.valueOf(viewModel.sort),
            title = stringResource(R.string.sort),
            isDeselectable = false,
            onConfirm = {
                openSortDialog = false
                viewModel.onSortChanged(it!!.value)
            },
            onDismiss = { openSortDialog = false }
        )
    }

    if (statusSheetState.isVisible) {
        ListStatusSheet(
            selectedStatus = viewModel.status,
            sheetState = statusSheetState,
            bottomPadding = bottomBarPadding,
            onStatusChanged = viewModel::onStatusChanged
        )
    }

    if (isMyList && editSheetState.isVisible && viewModel.selectedItem != null) {
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
        title = if (mediaType == MediaType.ANIME) stringResource(R.string.anime_list)
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
                        painter = painterResource(viewModel.status.icon()),
                        contentDescription = "status",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = viewModel.status.localized())
                }
            }
        },
        navigationIcon = {
            if (navigateBack != null) {
                BackIconButton(onClick = navigateBack)
            }
        },
        actions = {
            IconButton(onClick = { openSortDialog = true }) {
                Icon(painter = painterResource(R.drawable.sort_24), contentDescription = "sort")
            }
        },
        scrollBehavior = topAppBarScrollBehavior,
        contentWindowInsets = if (!isMyList) WindowInsets.systemBars
        else WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = if (!isMyList) 0.dp
                    else padding.calculateBottomPadding()
                )
        ) {
            UserMediaListView(
                mediaList = viewModel.mediaList,
                status = viewModel.status,
                mediaType = mediaType,
                isMyList = isMyList,
                isLoading = viewModel.isLoading,
                contentPadding = if (!isMyList)
                    PaddingValues(top = 8.dp, bottom = padding.calculateBottomPadding())
                else PaddingValues(vertical = 8.dp),
                nestedScrollConnection = topAppBarScrollBehavior.nestedScrollConnection,
                navigateToDetails = navigateToMediaDetails,
                onLoadMore = {
                    if (viewModel.hasNextPage) viewModel.getUserMediaList()
                },
                onRefresh = {
                    scope.launch { viewModel.refreshList(refreshCache = true) }
                },
                onShowEditSheet = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.selectedItem = it
                    scope.launch { editSheetState.show() }
                },
                onUpdateProgress = { entry ->
                    scope.launch {
                        viewModel.updateEntryProgress(
                            entryId = entry.id,
                            progress = (entry.progress ?: 0) + 1
                        )
                    }
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
                mediaType = MediaType.ANIME,
                navigateToMediaDetails = {}
            )
        }
    }
}