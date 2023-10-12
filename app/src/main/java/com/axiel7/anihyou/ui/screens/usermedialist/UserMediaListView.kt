package com.axiel7.anihyou.ui.screens.usermedialist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.ItemsPerRow
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_MEDIUM_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.composables.pullrefresh.PullRefreshIndicator
import com.axiel7.anihyou.ui.composables.pullrefresh.pullRefresh
import com.axiel7.anihyou.ui.composables.pullrefresh.rememberPullRefreshState
import com.axiel7.anihyou.ui.screens.usermedialist.composables.CompactUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.GridUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.MinimalUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.StandardUserMediaListItem
import kotlinx.coroutines.flow.StateFlow

@Composable
fun UserMediaListView(
    mediaList: List<UserMediaListQuery.MediaList>,
    status: MediaListStatus,
    scoreFormat: ScoreFormat,
    listStyle: ListStyle,
    itemsPerRowFlow: StateFlow<ItemsPerRow?>,
    isMyList: Boolean,
    isLoading: Boolean,
    isRefreshing: Boolean,
    showAsGrid: Boolean,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    nestedScrollConnection: NestedScrollConnection,
    navigateToDetails: (mediaId: Int) -> Unit,
    onLoadMore: suspend () -> Unit,
    onRefresh: () -> Unit,
    onShowEditSheet: (UserMediaListQuery.MediaList) -> Unit,
    onClickNotes: (UserMediaListQuery.MediaList) -> Unit,
    onUpdateProgress: (BasicMediaListEntry) -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = onRefresh
    )

    Box(
        modifier = Modifier
            .clipToBounds()
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        val listModifier = Modifier
            .fillMaxWidth()
            .nestedScroll(nestedScrollConnection)
        if (listStyle == ListStyle.GRID) {
            val itemsPerRow by itemsPerRowFlow.collectAsStateWithLifecycle()
            val listState = rememberLazyGridState()
            listState.OnBottomReached(buffer = 6, onLoadMore = onLoadMore)

            LazyVerticalGrid(
                columns = if (itemsPerRow != null && itemsPerRow!!.value > 0)
                    GridCells.Fixed(itemsPerRow!!.value)
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
                if (isLoading) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
            }
        } else if (showAsGrid) {
            val listState = rememberLazyGridState()
            listState.OnBottomReached(buffer = 3, onLoadMore = onLoadMore)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = listModifier,
                state = listState,
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.Center
            ) {
                when (listStyle) {
                    ListStyle.STANDARD -> {
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
                                },
                                onClickNotes = {
                                    onClickNotes(item)
                                }
                            )
                        }
                        if (isLoading) {
                            items(10) {
                                MediaItemHorizontalPlaceholder()
                            }
                        }
                    }

                    ListStyle.COMPACT -> {
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
                                },
                                onClickNotes = {
                                    onClickNotes(item)
                                }
                            )
                        }
                        if (isLoading) {
                            items(10) {
                                MediaItemHorizontalPlaceholder()
                            }
                        }
                    }

                    ListStyle.MINIMAL -> {
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
                                },
                                onClickNotes = {
                                    onClickNotes(item)
                                }
                            )
                        }
                        if (isLoading) {
                            items(10) {
                                MediaItemHorizontalPlaceholder()
                            }
                        }
                    }

                    else -> {}
                }
            }//: LazyVerticalGrid
        } else {
            val listState = rememberLazyListState()
            listState.OnBottomReached(buffer = 3, onLoadMore = onLoadMore)

            LazyColumn(
                modifier = listModifier,
                state = listState,
                contentPadding = contentPadding,
            ) {
                when (listStyle) {
                    ListStyle.STANDARD -> {
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
                                },
                                onClickNotes = {
                                    onClickNotes(item)
                                }
                            )
                        }
                        if (isLoading) {
                            items(10) {
                                MediaItemHorizontalPlaceholder()
                            }
                        }
                    }

                    ListStyle.COMPACT -> {
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
                                },
                                onClickNotes = {
                                    onClickNotes(item)
                                }
                            )
                        }
                        if (isLoading) {
                            items(10) {
                                MediaItemHorizontalPlaceholder()
                            }
                        }
                    }

                    ListStyle.MINIMAL -> {
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
                                },
                                onClickNotes = {
                                    onClickNotes(item)
                                }
                            )
                        }
                        if (isLoading) {
                            items(10) {
                                MediaItemHorizontalPlaceholder()
                            }
                        }
                    }

                    else -> {}
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