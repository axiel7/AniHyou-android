package com.axiel7.anihyou.feature.usermedialist

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.media.AllPriorityColors
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_MEDIUM_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.composables.media.PriorityColors.Companion.toPriorityColors
import com.axiel7.anihyou.feature.usermedialist.composables.CompactUserMediaListItem
import com.axiel7.anihyou.feature.usermedialist.composables.GridUserMediaListItem
import com.axiel7.anihyou.feature.usermedialist.composables.MinimalUserMediaListItem
import com.axiel7.anihyou.feature.usermedialist.composables.StandardUserMediaListItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserMediaListView(
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    isCompactScreen: Boolean,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    nestedScrollConnection: NestedScrollConnection,
    navActionManager: NavActionManager,
    onShowEditSheet: (CommonMediaListEntry) -> Unit,
    lazyListState: LazyListState,
    lazyGridState: LazyGridState,
    stickyHeaderContent: (@Composable () -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    val haptic = LocalHapticFeedback.current
    val pullRefreshState = rememberPullToRefreshState()

    val priorityNoneColor = MaterialTheme.colorScheme.secondaryContainer
    val lowPriorityColors = remember(uiState.lowPriorityColor, isDark) {
        (uiState.lowPriorityColor ?: priorityNoneColor).toPriorityColors(isDark)
    }
    val mediumPriorityColors = remember(uiState.mediumPriorityColor, isDark) {
        (uiState.mediumPriorityColor ?: priorityNoneColor).toPriorityColors(isDark)
    }
    val highPriorityColors = remember(uiState.highPriorityColor, isDark) {
        (uiState.highPriorityColor ?: priorityNoneColor).toPriorityColors(isDark)
    }
    val allPriorityColors = remember(lowPriorityColors, mediumPriorityColors, highPriorityColors) {
        AllPriorityColors(
            low = lowPriorityColors,
            medium = mediumPriorityColors,
            high = highPriorityColors,
        )
    }

    val onClickPlus: (Int, CommonMediaListEntry) -> Unit = { increment, item ->
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        event?.onClickPlusOne(increment, item)
    }

    PullToRefreshBox(
        isRefreshing = uiState.fetchFromNetwork,
        onRefresh = { event?.refreshList() },
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                state = pullRefreshState,
                isRefreshing = uiState.fetchFromNetwork,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    ) {
        val listModifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
        if (uiState.listStyle == ListStyle.GRID) {
            LazyListGrid(
                mediaList = uiState.entries,
                uiState = uiState,
                event = event,
                allPriorityColors = allPriorityColors,
                modifier = listModifier,
                navActionManager = navActionManager,
                onShowEditSheet = onShowEditSheet,
                listState = lazyGridState,
                stickyHeaderContent = stickyHeaderContent,
            )
        } else if (!isCompactScreen) {
            LazyListTablet(
                mediaList = uiState.entries,
                uiState = uiState,
                event = event,
                allPriorityColors = allPriorityColors,
                modifier = listModifier,
                contentPadding = contentPadding,
                navActionManager = navActionManager,
                onShowEditSheet = onShowEditSheet,
                onClickPlus = onClickPlus,
                listState = lazyGridState,
                stickyHeaderContent = stickyHeaderContent,
                )
        } else {
            LazyListPhone(
                mediaList = uiState.entries,
                uiState = uiState,
                event = event,
                allPriorityColors = allPriorityColors,
                modifier = listModifier,
                contentPadding = contentPadding,
                navActionManager = navActionManager,
                onShowEditSheet = onShowEditSheet,
                onClickPlus = onClickPlus,
                listState = lazyListState,
                stickyHeaderContent = stickyHeaderContent,
                )
        }
    }//: Box
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LazyListGrid(
    mediaList: List<CommonMediaListEntry>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    allPriorityColors: AllPriorityColors,
    modifier: Modifier,
    navActionManager: NavActionManager,
    onShowEditSheet: (CommonMediaListEntry) -> Unit,
    listState: LazyGridState,
    stickyHeaderContent: (@Composable () -> Unit)? = null
) {
    val navPadding = WindowInsets.navigationBars.asPaddingValues()
    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    val headerColor by animateColorAsState(
        targetValue = if (isScrolled) MaterialTheme.colorScheme.surfaceContainer
        else MaterialTheme.colorScheme.surface,
        label = "headerColor"
    )

    val headerElevation by animateDpAsState(
        targetValue = if (isScrolled) 4.dp else 0.dp,
        label = "headerElevation"
    )


    LazyVerticalGrid(
        columns = if (uiState.itemsPerRow.value > 0) GridCells.Fixed(uiState.itemsPerRow.value)
        else GridCells.Adaptive(minSize = (MEDIA_POSTER_MEDIUM_WIDTH + 8).dp),
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp) + navPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        stickyHeaderContent?.let { header ->
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = headerColor,
                    shadowElevation = headerElevation
                ) {
                    header()
                }
            }
        }

        if (uiState.isLoading) {
            items(10) {
                MediaItemVerticalPlaceholder()
            }
        } else if (mediaList.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.no_media),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
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
                showLowPriority = uiState.showLowPriority,
                allPriorityColors = allPriorityColors,
                onClick = { navActionManager.toMediaDetails(item.mediaId) },
                onLongClick = { onShowEditSheet(item) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LazyListTablet(
    mediaList: List<CommonMediaListEntry>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    allPriorityColors: AllPriorityColors,
    modifier: Modifier,
    contentPadding: PaddingValues,
    navActionManager: NavActionManager,
    onShowEditSheet: (CommonMediaListEntry) -> Unit,
    onClickPlus: (Int, CommonMediaListEntry) -> Unit,
    listState: LazyGridState,
    stickyHeaderContent: (@Composable () -> Unit)? = null
) {

    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    val headerColor by animateColorAsState(
        targetValue = if (isScrolled) MaterialTheme.colorScheme.surfaceContainer
        else MaterialTheme.colorScheme.surface,
        label = "headerColor"
    )

    val headerElevation by animateDpAsState(
        targetValue = if (isScrolled) 4.dp else 0.dp,
        label = "headerElevation"
    )

    val navPadding = WindowInsets.navigationBars.asPaddingValues()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding + navPadding,
        horizontalArrangement = Arrangement.Center
    ) {
        stickyHeaderContent?.let { header ->
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = headerColor,
                    shadowElevation = headerElevation
                ) {
                    header()
                }
            }
        }

        if (uiState.isLoading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        } else if (mediaList.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.no_media),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        when (uiState.listStyle) {
            ListStyle.STANDARD -> {
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    StandardUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        isPlusEnabled = !uiState.isLoadingPlusOne,
                        showLowPriority = uiState.showLowPriority,
                        allPriorityColors = allPriorityColors,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = { onClickPlus(it, item) },
                        onClickNotes = { event?.onClickNotes(item) },
                        blockPlus = { event?.blockPlusOne() }
                    )
                }
            }

            ListStyle.COMPACT -> {
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    CompactUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        isPlusEnabled = !uiState.isLoadingPlusOne,
                        showLowPriority = uiState.showLowPriority,
                        allPriorityColors = allPriorityColors,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = { onClickPlus(it, item) },
                        onClickNotes = { event?.onClickNotes(item) },
                        blockPlus = { event?.blockPlusOne() },
                    )
                }
            }

            ListStyle.MINIMAL -> {
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    MinimalUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        allPriorityColors = allPriorityColors,
                        isMyList = uiState.isMyList,
                        isPlusEnabled = !uiState.isLoadingPlusOne,
                        showLowPriority = uiState.showLowPriority,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = { onClickPlus(it, item) },
                        onClickNotes = { event?.onClickNotes(item) },
                        blockPlus = { event?.blockPlusOne() },
                    )
                }
            }

            else -> {}
        }
    }//: LazyVerticalGrid
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LazyListPhone(
    mediaList: List<CommonMediaListEntry>,
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    allPriorityColors: AllPriorityColors,
    modifier: Modifier,
    contentPadding: PaddingValues,
    navActionManager: NavActionManager,
    onShowEditSheet: (CommonMediaListEntry) -> Unit,
    onClickPlus: (Int, CommonMediaListEntry) -> Unit,
    listState: LazyListState,
    stickyHeaderContent: (@Composable () -> Unit)? = null
) {

    val isScrolled by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

    val headerColor by animateColorAsState(
        targetValue = if (isScrolled) MaterialTheme.colorScheme.surfaceContainer
        else MaterialTheme.colorScheme.surface,
        label = "headerColor"
    )

    val headerElevation by animateDpAsState(
        targetValue = if (isScrolled) 4.dp else 0.dp,
        label = "headerElevation"
    )

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
    ) {

        stickyHeaderContent?.let { header ->
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = headerColor,
                    shadowElevation = headerElevation
                ) {
                    header()
                }
            }
        }


        if (uiState.isLoading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        } else if (mediaList.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_media),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        when (uiState.listStyle) {
            ListStyle.STANDARD -> {
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    StandardUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        isPlusEnabled = !uiState.isLoadingPlusOne,
                        showLowPriority = uiState.showLowPriority,
                        allPriorityColors = allPriorityColors,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = { onClickPlus(it, item) },
                        onClickNotes = { event?.onClickNotes(item) },
                        blockPlus = { event?.blockPlusOne() },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            ListStyle.COMPACT -> {
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    CompactUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        isMyList = uiState.isMyList,
                        isPlusEnabled = !uiState.isLoadingPlusOne,
                        showLowPriority = uiState.showLowPriority,
                        allPriorityColors = allPriorityColors,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = { onClickPlus(it, item) },
                        onClickNotes = { event?.onClickNotes(item) },
                        blockPlus = { event?.blockPlusOne() },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            ListStyle.MINIMAL -> {
                items(
                    items = mediaList,
                    contentType = { it.basicMediaListEntry }
                ) { item ->
                    MinimalUserMediaListItem(
                        item = item,
                        listStatus = uiState.status,
                        scoreFormat = uiState.scoreFormat,
                        allPriorityColors = allPriorityColors,
                        isMyList = uiState.isMyList,
                        isPlusEnabled = !uiState.isLoadingPlusOne,
                        showLowPriority = uiState.showLowPriority,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onLongClick = { onShowEditSheet(item) },
                        onClickPlus = { onClickPlus(it, item) },
                        onClickNotes = { event?.onClickNotes(item) },
                        blockPlus = { event?.blockPlusOne() },
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            else -> {}
        }
    }//: LazyColumn
}