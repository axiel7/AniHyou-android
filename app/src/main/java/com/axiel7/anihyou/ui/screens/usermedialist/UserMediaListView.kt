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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.App
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.PreferencesDataStore.GENERAL_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.GRID_ITEMS_PER_ROW_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.USE_GENERAL_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.media.ListType
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.common.ListStyle
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_MEDIUM_WIDTH
import com.axiel7.anihyou.ui.composables.pullrefresh.PullRefreshIndicator
import com.axiel7.anihyou.ui.composables.pullrefresh.pullRefresh
import com.axiel7.anihyou.ui.composables.pullrefresh.rememberPullRefreshState
import com.axiel7.anihyou.ui.screens.usermedialist.composables.CompactUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.GridUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.MinimalUserMediaListItem
import com.axiel7.anihyou.ui.screens.usermedialist.composables.StandardUserMediaListItem

@Composable
fun UserMediaListView(
    mediaList: List<UserMediaListQuery.MediaList>,
    status: MediaListStatus,
    mediaType: MediaType,
    isMyList: Boolean,
    isLoading: Boolean,
    showAsGrid: Boolean,
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
            listState.OnBottomReached(buffer = 6, onLoadMore = onLoadMore)

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