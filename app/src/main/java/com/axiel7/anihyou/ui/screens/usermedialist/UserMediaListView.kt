package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.App
import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.PreferencesDataStore.GENERAL_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.GRID_ITEMS_PER_ROW_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.USE_GENERAL_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.media.ListType
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.user.UserMediaListSort
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.base.ListStyle
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.CompactUserMediaListItem
import com.axiel7.anihyou.ui.composables.media.GridUserMediaListItem
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_MEDIUM_WIDTH
import com.axiel7.anihyou.ui.composables.media.MinimalUserMediaListItem
import com.axiel7.anihyou.ui.composables.media.StandardUserMediaListItem
import com.axiel7.anihyou.ui.screens.explore.MEDIA_TYPE_ARGUMENT
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.screens.profile.USER_ID_ARGUMENT
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserMediaListView(
    mediaList: List<UserMediaListQuery.MediaList>,
    status: MediaListStatus,
    mediaType: MediaType,
    isMyList: Boolean,
    isLoading: Boolean,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    nestedScrollConnection: NestedScrollConnection,
    navigateToDetails: (mediaId: Int) -> Unit,
    onLoadMore: suspend () -> Unit,
    onRefresh: () -> Unit,
    onShowEditSheet: (UserMediaListQuery.MediaList) -> Unit,
    onUpdateProgress: (BasicMediaListEntry) -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onRefresh
    )
    val useGeneralListStyle by rememberPreference(
        USE_GENERAL_LIST_STYLE_PREFERENCE_KEY,
        App.useGeneralListStyle
    )
    val generalListStyle by rememberPreference(
        GENERAL_LIST_STYLE_PREFERENCE_KEY,
        App.generalListStyle.name
    )
    val listStyle = if (useGeneralListStyle == true) generalListStyle
    else ListType(status, mediaType).styleGlobalAppVariable.name

    val scoreFormatPreference by rememberPreference(
        SCORE_FORMAT_PREFERENCE_KEY,
        App.scoreFormat.name
    )
    val scoreFormat by remember {
        derivedStateOf { ScoreFormat.valueOf(scoreFormatPreference ?: App.scoreFormat.name) }
    }

    Box(
        modifier = Modifier
            .clipToBounds()
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        val listModifier = Modifier
            .fillMaxWidth()
            .nestedScroll(nestedScrollConnection)
        if (listStyle == ListStyle.GRID.name) {
            val itemsPerRow by rememberPreference(
                GRID_ITEMS_PER_ROW_PREFERENCE_KEY,
                App.gridItemsPerRow
            )
            val listState = rememberLazyGridState()
            listState.OnBottomReached(buffer = 3, onLoadMore = onLoadMore)

            LazyVerticalGrid(
                columns = if (itemsPerRow != null && itemsPerRow!! > 0) GridCells.Fixed(itemsPerRow!!)
                else GridCells.Adaptive(minSize = (MEDIA_POSTER_MEDIUM_WIDTH + 8).dp),
                modifier = listModifier,
                state = listState,
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                items(
                    items = mediaList,
                    //key = { it.basicMediaListEntry.id },
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    GridUserMediaListItem(
                        item = item,
                        scoreFormat = scoreFormat,
                        onClick = { navigateToDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) }
                    )
                }
            }
        } else {
            val listState = rememberLazyListState()
            listState.OnBottomReached(buffer = 3, onLoadMore = onLoadMore)

            LazyColumn(
                modifier = listModifier,
                state = listState,
                contentPadding = contentPadding,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (listStyle) {
                    ListStyle.STANDARD.name -> {
                        items(
                            items = mediaList,
                            key = { it.basicMediaListEntry.id },
                            contentType = { it.basicMediaListEntry }
                        ) { item ->
                            StandardUserMediaListItem(
                                item = item,
                                status = status,
                                scoreFormat = scoreFormat,
                                isMyList = isMyList,
                                onClick = { navigateToDetails(item.mediaId) },
                                onLongClick = { onShowEditSheet(item) },
                                onClickPlus = {
                                    onUpdateProgress(item.basicMediaListEntry)
                                }
                            )
                        }
                    }

                    ListStyle.COMPACT.name -> {
                        items(
                            items = mediaList,
                            key = { it.basicMediaListEntry.id },
                            contentType = { it.basicMediaListEntry }
                        ) { item ->
                            CompactUserMediaListItem(
                                item = item,
                                status = status,
                                scoreFormat = scoreFormat,
                                isMyList = isMyList,
                                onClick = { navigateToDetails(item.mediaId) },
                                onLongClick = { onShowEditSheet(item) },
                                onClickPlus = {
                                    onUpdateProgress(item.basicMediaListEntry)
                                }
                            )
                        }
                    }

                    ListStyle.MINIMAL.name -> {
                        items(
                            items = mediaList,
                            key = { it.basicMediaListEntry.id },
                            contentType = { it.basicMediaListEntry }
                        ) { item ->
                            MinimalUserMediaListItem(
                                item = item,
                                status = status,
                                scoreFormat = scoreFormat,
                                isMyList = isMyList,
                                onClick = { navigateToDetails(item.mediaId) },
                                onLongClick = { onShowEditSheet(item) },
                                onClickPlus = {
                                    onUpdateProgress(item.basicMediaListEntry)
                                }
                            )
                        }
                    }
                }
            }//: LazyColumn
        }
        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopCenter)
        )
    }//: Box
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListStatusSheet(
    selectedStatus: MediaListStatus,
    sheetState: SheetState,
    bottomPadding: Dp,
    onStatusChanged: (MediaListStatus) -> Unit,
    onDismiss: () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier.padding(bottom = 8.dp + bottomPadding)
        ) {
            MediaListStatus.knownValues().forEach {
                val isSelected = selectedStatus == it
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStatusChanged(it) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(it.icon()),
                        contentDescription = "check",
                        tint = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = it.localized(),
                        modifier = Modifier.padding(start = 8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
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