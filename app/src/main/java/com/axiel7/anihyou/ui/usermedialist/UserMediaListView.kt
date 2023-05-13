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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.App
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.UserMediaListSort
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.type.MediaListSort
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.RoundedTabRowIndicator
import com.axiel7.anihyou.ui.composables.StandardUserMediaListItem
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserMediaListHostView(
    mediaType: MediaType
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val tabRowItems = remember { MediaListStatus.knownValues() }
    var openSortDialog by remember { mutableStateOf(false) }
    var sortPreference by rememberPreference(
        key = if (mediaType == MediaType.ANIME) ANIME_LIST_SORT_PREFERENCE_KEY else MANGA_LIST_SORT_PREFERENCE_KEY,
        defaultValue = if (mediaType == MediaType.ANIME) App.animeListSort else App.mangaListSort
    )

    DefaultScaffoldWithSmallTopAppBar(
        title = if (mediaType == MediaType.ANIME) stringResource(R.string.anime_list)
        else stringResource(R.string.manga_list),
        actions = {
            IconButton(onClick = { openSortDialog = true }) {
                Icon(painter = painterResource(R.drawable.sort_24), contentDescription = "sort")
            }
        }
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
                pageCount = tabRowItems.size,
                state = pagerState,
                beyondBoundsPageCount = 0,
                key = { tabRowItems[it].name }
            ) {
                UserMediaListView(
                    mediaType = mediaType,
                    status = tabRowItems[it],
                    sort = if (sortPreference != null) MediaListSort.safeValueOf(sortPreference!!) else null
                )
            }//: Pager
        }//: Column
    }//: Scaffold

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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserMediaListView(
    mediaType: MediaType,
    status: MediaListStatus,
    sort: MediaListSort?
) {
    val viewModel: UserMediaListViewModel = viewModel(key = "${mediaType.name}${status.name}") {
        UserMediaListViewModel(mediaType, status)
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isLoading,
        onRefresh = { scope.launch { viewModel.getUserList() } }
    )

    Box(
        modifier = Modifier
            .clipToBounds()
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(viewModel.mediaList,
                contentType = { it.basicMediaListEntry }
            ) { item ->
                StandardUserMediaListItem(
                    item = item,
                    onClick = { /*TODO*/ },
                    onLongClick = { /*TODO*/ },
                    onClickPlus = { /*TODO*/ }
                )
            }
            item {
                if (viewModel.isLoading) {
                    CircularProgressIndicator()
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

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getUserList()
    }

    LaunchedEffect(sort) {
        if (!viewModel.isLoading && sort != null) {
            viewModel.sort = sort
            viewModel.refreshList()
        }
    }
}

@Preview
@Composable
fun UserMediaListViewPreview() {
    AniHyouTheme {
        UserMediaListView(
            mediaType = MediaType.ANIME,
            status = MediaListStatus.CURRENT,
            sort = MediaListSort.MEDIA_TITLE_ROMAJI_DESC
        )
    }
}