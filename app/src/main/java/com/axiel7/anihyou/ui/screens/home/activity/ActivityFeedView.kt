package com.axiel7.anihyou.ui.screens.home.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.ActivityFeedQuery
import com.axiel7.anihyou.data.model.activity.text
import com.axiel7.anihyou.type.ActivityType
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.screens.home.activity.composables.ActivityFeedItem
import com.axiel7.anihyou.ui.screens.home.activity.composables.ActivityFollowingChip
import com.axiel7.anihyou.ui.screens.home.activity.composables.ActivityTypeChip
import com.axiel7.anihyou.ui.screens.profile.activity.ActivityItemPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun ActivityFeedView(
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val viewModel: ActivityFeedViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ActivityFeedContent(
        activities = viewModel.activities,
        modifier = modifier,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityFeedContent(
    activities: List<ActivityFeedQuery.Activity>,
    modifier: Modifier = Modifier,
    uiState: ActivityFeedUiState,
    event: ActivityFeedEvent?,
    navActionManager: NavActionManager,
) {
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            event?.refreshList()
        }
    }
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) pullRefreshState.endRefresh()
    }

    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })

    Box(
        modifier = Modifier
            .clipToBounds()
            .nestedScroll(pullRefreshState.nestedScrollConnection)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = modifier,
        ) {
            item {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActivityTypeChip(
                        value = uiState.type,
                        onValueChanged = { event?.setType(it) }
                    )
                    ActivityFollowingChip(
                        value = uiState.isFollowing,
                        onValueChanged = { event?.setIsFollowing(it) }
                    )
                }
            }
            if (uiState.isLoading) {
                items(10) {
                    ActivityItemPlaceholder(
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            items(
                items = activities,
                contentType = { it }
            ) { item ->
                item.onListActivity?.let {
                    ActivityFeedItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        type = ActivityType.MEDIA_LIST,
                        username = it.user?.name,
                        avatarUrl = it.user?.avatar?.medium,
                        createdAt = it.listActivityFragment.createdAt,
                        text = it.listActivityFragment.text(),
                        replyCount = it.listActivityFragment.replyCount,
                        likeCount = it.listActivityFragment.likeCount,
                        isLiked = it.listActivityFragment.isLiked,
                        mediaCoverUrl = it.listActivityFragment.media?.coverImage?.medium,
                        onClick = {
                            navActionManager.toActivityDetails(it.listActivityFragment.id)
                        },
                        onClickUser = {
                            it.listActivityFragment.userId?.let(navActionManager::toUserDetails)
                        },
                        onClickLike = {
                            event?.toggleLikeActivity(it.listActivityFragment.id)
                        },
                        onClickMedia = {
                            it.listActivityFragment.media?.id?.let(navActionManager::toMediaDetails)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                }
                item.onTextActivity?.textActivityFragment?.let {
                    ActivityFeedItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        type = ActivityType.TEXT,
                        username = it.user?.name,
                        avatarUrl = it.user?.avatar?.medium,
                        createdAt = it.createdAt,
                        text = it.text.orEmpty(),
                        replyCount = it.replyCount,
                        likeCount = it.likeCount,
                        isLiked = it.isLiked,
                        onClick = {
                            navActionManager.toActivityDetails(it.id)
                        },
                        onClickUser = {
                            it.userId?.let(navActionManager::toUserDetails)
                        },
                        onClickLike = {
                            event?.toggleLikeActivity(it.id)
                        },
                        navigateToFullscreenImage = navActionManager::toFullscreenImage
                    )
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                }
            }
        }//:LazyColumn
        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }//:Box
}

@Preview
@Composable
fun ActivityFeedViewPreview() {
    AniHyouTheme {
        Surface {
            ActivityFeedContent(
                activities = emptyList(),
                uiState = ActivityFeedUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}