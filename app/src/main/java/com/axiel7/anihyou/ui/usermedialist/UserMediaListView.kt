package com.axiel7.anihyou.ui.usermedialist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.App
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.LIST_DISPLAY_MODE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.UserMediaListSort
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.base.ListMode
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.RoundedTabRowIndicator
import com.axiel7.anihyou.ui.composables.media.CompactUserMediaListItem
import com.axiel7.anihyou.ui.composables.media.MinimalUserMediaListItem
import com.axiel7.anihyou.ui.composables.media.StandardUserMediaListItem
import com.axiel7.anihyou.ui.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserMediaListHostView(
    mediaType: MediaType,
    navigateToMediaDetails: (mediaId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val tabRowItems = remember { MediaListStatus.knownValues() }
    val pagerState = rememberPagerState { tabRowItems.size }
    var openSortDialog by remember { mutableStateOf(false) }
    var sortPreference by rememberPreference(
        key = if (mediaType == MediaType.ANIME) ANIME_LIST_SORT_PREFERENCE_KEY else MANGA_LIST_SORT_PREFERENCE_KEY,
        defaultValue = if (mediaType == MediaType.ANIME) App.animeListSort else App.mangaListSort
    )
    val sort = remember { derivedStateOf { sortPreference?.let { MediaListSort.safeValueOf(it) } } }
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    if (openSortDialog) {
        DialogWithRadioSelection(
            values = UserMediaListSort.values(),
            defaultValue = UserMediaListSort.valueOf(sortPreference),
            title = stringResource(R.string.sort),
            onConfirm = {
                sortPreference = it.value.rawValue
                openSortDialog = false
            },
            onDismiss = { openSortDialog = false }
        )
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = if (mediaType == MediaType.ANIME) stringResource(R.string.anime_list)
        else stringResource(R.string.manga_list),
        modifier = modifier,
        actions = {
            IconButton(onClick = { openSortDialog = true }) {
                Icon(painter = painterResource(R.drawable.sort_24), contentDescription = "sort")
            }
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier.padding(top = padding.calculateTopPadding())
        ) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    RoundedTabRowIndicator(tabPositions[pagerState.currentPage])
                }
            ) {
                tabRowItems.forEachIndexed { index, item ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(text = item.localized()) },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }//: TabRow

            HorizontalPager(
                state = pagerState,
                beyondBoundsPageCount = 0,
                key = { tabRowItems[it].name }
            ) {
                UserMediaListView(
                    mediaType = mediaType,
                    status = tabRowItems[it],
                    sort = sort,
                    navigateToDetails = navigateToMediaDetails,
                    nestedScrollConnection = topAppBarScrollBehavior.nestedScrollConnection
                )
            }//: Pager
        }//: Column
    }//: Scaffold
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserMediaListView(
    mediaType: MediaType,
    status: MediaListStatus,
    sort: State<MediaListSort?>,
    navigateToDetails: (mediaId: Int) -> Unit,
    nestedScrollConnection: NestedScrollConnection
) {
    val viewModel: UserMediaListViewModel = viewModel(key = "${mediaType.name}${status.name}") {
        UserMediaListViewModel(mediaType, status)
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isLoading,
        onRefresh = { scope.launch { viewModel.refreshList() } }
    )
    val sheetState = rememberModalBottomSheetState()
    val listDisplayMode by rememberPreference(LIST_DISPLAY_MODE_PREFERENCE_KEY, App.listDisplayMode.name)
    val scoreFormatPreference by rememberPreference(SCORE_FORMAT_PREFERENCE_KEY, App.scoreFormat.name)
    val scoreFormat by remember {
        derivedStateOf { ScoreFormat.valueOf(scoreFormatPreference ?: App.scoreFormat.name) }
    }

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getUserList()
    }

    if (sheetState.isVisible && viewModel.selectedItem != null) {
        EditMediaSheet(
            sheetState = sheetState,
            mediaDetails = viewModel.selectedItem!!.media!!.basicMediaDetails,
            listEntry = viewModel.selectedItem!!.basicMediaListEntry,
            onDismiss = { scope.launch { sheetState.hide() } }
        )
    }

    LaunchedEffect(sort.value) {
        if (!viewModel.isLoading && sort.value != null && viewModel.sort != sort.value) {
            viewModel.sort = sort.value!!
            viewModel.refreshList()
        }
    }

    Box(
        modifier = Modifier
            .clipToBounds()
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = viewModel.mediaList,
                key = { it.basicMediaListEntry.id },
                contentType = { it.basicMediaListEntry }
            ) { item ->
                when (listDisplayMode) {
                    ListMode.STANDARD.name -> {
                        StandardUserMediaListItem(
                            item = item,
                            status = status,
                            scoreFormat = scoreFormat,
                            onClick = { navigateToDetails(item.mediaId) },
                            onLongClick = {
                                viewModel.selectedItem = item
                                scope.launch { sheetState.show() }
                            },
                            onClickPlus = {
                                scope.launch {
                                    viewModel.updateEntryProgress(
                                        entryId = item.basicMediaListEntry.id,
                                        progress = (item.basicMediaListEntry.progress ?: 0) + 1
                                    )
                                }
                            }
                        )
                    }
                    ListMode.COMPACT.name -> {
                        CompactUserMediaListItem(
                            item = item,
                            status = status,
                            scoreFormat = scoreFormat,
                            onClick = { navigateToDetails(item.mediaId) },
                            onLongClick = {
                                viewModel.selectedItem = item
                                scope.launch { sheetState.show() }
                            },
                            onClickPlus = {
                                scope.launch {
                                    viewModel.updateEntryProgress(
                                        entryId = item.basicMediaListEntry.id,
                                        progress = (item.basicMediaListEntry.progress ?: 0) + 1
                                    )
                                }
                            }
                        )
                    }
                    ListMode.MINIMAL.name -> {
                        MinimalUserMediaListItem(
                            item = item,
                            status = status,
                            scoreFormat = scoreFormat,
                            onClick = { navigateToDetails(item.mediaId) },
                            onLongClick = {
                                viewModel.selectedItem = item
                                scope.launch { sheetState.show() }
                            },
                            onClickPlus = {
                                scope.launch {
                                    viewModel.updateEntryProgress(
                                        entryId = item.basicMediaListEntry.id,
                                        progress = (item.basicMediaListEntry.progress ?: 0) + 1
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }//: LazyColumn
        PullRefreshIndicator(
            refreshing = viewModel.isLoading,
            state = pullRefreshState,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopCenter)
        )
    }//: Box
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