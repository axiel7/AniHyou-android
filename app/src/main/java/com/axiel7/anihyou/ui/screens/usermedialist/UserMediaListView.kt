package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_MEDIUM_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.screens.usermedialist.composables.CompactUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.GridUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.MinimalUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.StandardUserMediaListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMediaListView(
    mediaList: List<UserMediaListQuery.MediaList>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    nestedScrollConnection: NestedScrollConnection,
    navActionManager: NavActionManager,
    onShowEditSheet: (UserMediaListQuery.MediaList) -> Unit,
) {
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            event?.refreshList()
        }
    }
    LaunchedEffect(uiState.fetchFromNetwork) {
        if (!uiState.fetchFromNetwork) pullRefreshState.endRefresh()
    }

    Box(
        modifier = Modifier
            .nestedScroll(pullRefreshState.nestedScrollConnection)
            .fillMaxSize()
    ) {
        val listModifier = Modifier
            .fillMaxWidth()
            .nestedScroll(nestedScrollConnection)
        if (uiState.listStyle == ListStyle.GRID) {
            LazyListGrid(
                mediaList = mediaList,
                uiState = uiState,
                event = event,
                modifier = listModifier,
                navActionManager = navActionManager,
                onShowEditSheet = onShowEditSheet,
            )
        } else if (!uiState.isCompactScreen) {
            LazyListTablet(
                mediaList = mediaList,
                uiState = uiState,
                event = event,
                modifier = listModifier,
                contentPadding = contentPadding,
                navActionManager = navActionManager,
                onShowEditSheet = onShowEditSheet,
            )
        } else {
            LazyListPhone(
                mediaList = mediaList,
                uiState = uiState,
                event = event,
                modifier = listModifier,
                contentPadding = contentPadding,
                navActionManager = navActionManager,
                onShowEditSheet = onShowEditSheet,
            )
        }
        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }//: Box
}

@Composable
private fun LazyListGrid(
    mediaList: List<UserMediaListQuery.MediaList>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    modifier: Modifier,
    navActionManager: NavActionManager,
    onShowEditSheet: (UserMediaListQuery.MediaList) -> Unit,
) {
    val listState = rememberLazyGridState()
    listState.OnBottomReached(buffer = 6, onLoadMore = { event?.onLoadMore() })

    LazyVerticalGrid(
        columns = if (uiState.itemsPerRow.value > 0) GridCells.Fixed(uiState.itemsPerRow.value)
        else GridCells.Adaptive(minSize = (MEDIA_POSTER_MEDIUM_WIDTH + 8).dp),
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        if (uiState.isLoading) {
            items(10) {
                MediaItemVerticalPlaceholder()
            }
        }
        items(
            items = mediaList,
            contentType = { it.basicMediaListEntry }
        ) { item ->
            GridUserMediaListItem(
                item = item,
                listStatus = uiState.status,
                scoreFormat = uiState.scoreFormat,
                onClick = { navActionManager.toMediaDetails(item.mediaId) },
                onLongClick = { onShowEditSheet(item) }
            )
        }
    }
}

@Composable
private fun LazyListTablet(
    mediaList: List<UserMediaListQuery.MediaList>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    modifier: Modifier,
    contentPadding: PaddingValues,
    navActionManager: NavActionManager,
    onShowEditSheet: (UserMediaListQuery.MediaList) -> Unit,
) {
    val listState = rememberLazyGridState()
    listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.Center
    ) {
        when (uiState.listStyle) {
            ListStyle.STANDARD -> {
                if (uiState.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    StandardUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = {
                            event?.onClickPlusOne(item.basicMediaListEntry)
                        },
                        onClickNotes = {
                            event?.onClickNotes(item)
                        }
                    )
                }
            }

            ListStyle.COMPACT -> {
                if (uiState.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    CompactUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = {
                            event?.onClickPlusOne(item.basicMediaListEntry)
                        },
                        onClickNotes = {
                            event?.onClickNotes(item)
                        }
                    )
                }
            }

            ListStyle.MINIMAL -> {
                if (uiState.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    MinimalUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = {
                            event?.onClickPlusOne(item.basicMediaListEntry)
                        },
                        onClickNotes = {
                            event?.onClickNotes(item)
                        }
                    )
                }
            }

            else -> {}
        }
    }//: LazyVerticalGrid
}

@Composable
private fun LazyListPhone(
    mediaList: List<UserMediaListQuery.MediaList>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    modifier: Modifier,
    contentPadding: PaddingValues,
    navActionManager: NavActionManager,
    onShowEditSheet: (UserMediaListQuery.MediaList) -> Unit,
) {
    val listState = rememberLazyListState()
    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
    ) {
        when (uiState.listStyle) {
            ListStyle.STANDARD -> {
                if (uiState.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    StandardUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = {
                            event?.onClickPlusOne(item.basicMediaListEntry)
                        },
                        onClickNotes = {
                            event?.onClickNotes(item)
                        }
                    )
                }
            }

            ListStyle.COMPACT -> {
                if (uiState.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    CompactUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = {
                            event?.onClickPlusOne(item.basicMediaListEntry)
                        },
                        onClickNotes = {
                            event?.onClickNotes(item)
                        }
                    )
                }
            }

            ListStyle.MINIMAL -> {
                if (uiState.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                }
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    MinimalUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = {
                            event?.onClickPlusOne(item.basicMediaListEntry)
                        },
                        onClickNotes = {
                            event?.onClickNotes(item)
                        }
                    )
                }
            }

            else -> {}
        }
    }//: LazyColumn
}