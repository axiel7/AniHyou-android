package com.axiel7.anihyou.ui.usermedialist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
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

    DefaultScaffoldWithSmallTopAppBar(
        title = if (mediaType == MediaType.ANIME) stringResource(R.string.anime_list)
        else stringResource(R.string.manga_list),
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
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
                    status = tabRowItems[it]
                )
            }//: Pager
        }//: Column
    }
}

@Composable
fun UserMediaListView(
    mediaType: MediaType,
    status: MediaListStatus
) {
    val viewModel: UserMediaListViewModel = viewModel(key = "${mediaType.name}${status.name}") {
        UserMediaListViewModel(mediaType, status)
    }
    val listState = rememberLazyListState()

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
    }

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getUserList()
    }
}

@Preview
@Composable
fun UserMediaListViewPreview() {
    AniHyouTheme {
        UserMediaListView(
            mediaType = MediaType.ANIME,
            status = MediaListStatus.CURRENT
        )
    }
}