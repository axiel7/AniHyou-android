package com.axiel7.anihyou.ui.screens.home.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.data.model.activity.text
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.pullrefresh.PullRefreshIndicator
import com.axiel7.anihyou.ui.composables.pullrefresh.pullRefresh
import com.axiel7.anihyou.ui.composables.pullrefresh.rememberPullRefreshState
import com.axiel7.anihyou.ui.screens.home.activity.composables.ActivityFeedItem
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun ActivityFeedView(
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
) {
    val viewModel: ActivityViewModel = viewModel()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isLoading,
        onRefresh = viewModel::refresh
    )
    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getActivityFeed()
    }

    Box(
        modifier = Modifier
            .clipToBounds()
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = PaddingValues(top = 16.dp)
        ) {
            items(
                items = viewModel.activities,
                contentType = { it }
            ) { item ->
                item.onListActivity?.let {
                    ActivityFeedItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        username = it.user?.name,
                        avatarUrl = it.user?.avatar?.medium,
                        createdAt = it.listActivityFragment.createdAt,
                        text = it.listActivityFragment.text(),
                        replyCount = it.listActivityFragment.replyCount,
                        likeCount = it.listActivityFragment.likeCount,
                        isLiked = it.listActivityFragment.isLiked,
                        mediaCoverUrl = it.listActivityFragment.media?.coverImage?.medium,
                        onClick = { /*TODO*/ },
                        onClickUser = { it.user?.id?.let { id -> navigateToUserDetails(id) } },
                        onClickLike = { /*TODO*/ },
                        onClickMedia = {
                            it.listActivityFragment.media?.id?.let { id -> navigateToMediaDetails(id) }
                        }
                    )
                }
                item.onTextActivity?.let {
                    ActivityFeedItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        username = it.user?.name,
                        avatarUrl = it.user?.avatar?.medium,
                        createdAt = it.createdAt,
                        text = it.text ?: "",
                        replyCount = it.replyCount,
                        likeCount = it.likeCount,
                        isLiked = it.isLiked,
                        onClick = { /*TODO*/ },
                        onClickUser = { it.user?.id?.let { id -> navigateToUserDetails(id) } },
                        onClickLike = { /*TODO*/ },
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
        }//:LazyColumn
        PullRefreshIndicator(
            refreshing = viewModel.isLoading,
            state = pullRefreshState,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopCenter)
        )
    }//:Box
}

@Preview
@Composable
fun ActivityFeedViewPreview() {
    AniHyouTheme {
        Surface {
            ActivityFeedView(
                navigateToMediaDetails = {},
                navigateToUserDetails = {}
            )
        }
    }
}