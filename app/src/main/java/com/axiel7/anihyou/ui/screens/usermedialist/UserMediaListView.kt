package com.axiel7.anihyou.ui.screens.usermedialist

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_MEDIUM_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.screens.usermedialist.composables.CompactUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.GridUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.MinimalUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.RandomEntryButton
import com.axiel7.anihyou.ui.screens.usermedialist.composables.StandardUserMediaListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMediaListView(
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    nestedScrollConnection: NestedScrollConnection,
    navActionManager: NavActionManager,
    onShowEditSheet: (CommonMediaListEntry) -> Unit,
) {
    val context = LocalContext.current
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(uiState.randomEntryId) {
        uiState.randomEntryId?.let { id ->
            event?.onRandomEntryOpened()
            navActionManager.toMediaDetails(id)
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            event?.onErrorDisplayed()
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.fetchFromNetwork,
        onRefresh = { event?.refreshList() },
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
    ) {
        val listModifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
        if (uiState.listStyle == ListStyle.GRID) {
            LazyListGrid(
                mediaList = uiState.media,
                uiState = uiState,
                event = event,
                modifier = listModifier,
                navActionManager = navActionManager,
                onShowEditSheet = onShowEditSheet,
            )
        } else if (!uiState.isCompactScreen) {
            LazyListTablet(
                mediaList = uiState.media,
                uiState = uiState,
                event = event,
                modifier = listModifier,
                contentPadding = contentPadding,
                navActionManager = navActionManager,
                onShowEditSheet = onShowEditSheet,
            )
        } else {
            LazyListPhone(
                mediaList = uiState.media,
                uiState = uiState,
                event = event,
                modifier = listModifier,
                contentPadding = contentPadding,
                navActionManager = navActionManager,
                onShowEditSheet = onShowEditSheet,
            )
        }
    }//: Box
}

@Composable
private fun LazyListGrid(
    mediaList: List<CommonMediaListEntry>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    modifier: Modifier,
    navActionManager: NavActionManager,
    onShowEditSheet: (CommonMediaListEntry) -> Unit,
) {
    LazyVerticalGrid(
        columns = if (uiState.itemsPerRow.value > 0) GridCells.Fixed(uiState.itemsPerRow.value)
        else GridCells.Adaptive(minSize = (MEDIA_POSTER_MEDIUM_WIDTH + 8).dp),
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        if (uiState.status == MediaListStatus.PLANNING) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Row {
                    RandomEntryButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = { event?.getRandomPlannedEntry() }
                    )
                }
            }
        }
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
        item(contentType = { 0 }) {
            if (uiState.hasNextPage) {
                Box {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                LaunchedEffect(uiState.isLoading) {
                    if (!uiState.isLoading) event?.onLoadMore()
                }
            }
        }
    }
}

@Composable
private fun LazyListTablet(
    mediaList: List<CommonMediaListEntry>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    modifier: Modifier,
    contentPadding: PaddingValues,
    navActionManager: NavActionManager,
    onShowEditSheet: (CommonMediaListEntry) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.Center
    ) {
        if (uiState.status == MediaListStatus.PLANNING) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Row {
                    RandomEntryButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = { event?.getRandomPlannedEntry() }
                    )
                }
            }
        }
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

        item(contentType = { 0 }) {
            if (uiState.hasNextPage) {
                Box {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                LaunchedEffect(uiState.isLoading) {
                    if (!uiState.isLoading) event?.onLoadMore()
                }
            }
        }
    }//: LazyVerticalGrid
}

@Composable
private fun LazyListPhone(
    mediaList: List<CommonMediaListEntry>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    modifier: Modifier,
    contentPadding: PaddingValues,
    navActionManager: NavActionManager,
    onShowEditSheet: (CommonMediaListEntry) -> Unit,
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
        if (uiState.status == MediaListStatus.PLANNING) {
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    RandomEntryButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = { event?.getRandomPlannedEntry() }
                    )
                }
            }
        }
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