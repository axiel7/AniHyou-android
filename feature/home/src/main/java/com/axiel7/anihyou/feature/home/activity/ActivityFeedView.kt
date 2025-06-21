package com.axiel7.anihyou.feature.home.activity

import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.activity.text
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.activity.ActivityItemPlaceholder
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.composables.activity.ActivityFeedItem
import com.axiel7.anihyou.feature.home.activity.composables.ActivityFollowingChip
import com.axiel7.anihyou.feature.home.activity.composables.ActivityTypeChip
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActivityFeedView(
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val viewModel: ActivityFeedViewModel = koinViewModel(
        viewModelStoreOwner = LocalActivity.current as AppCompatActivity
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ActivityFeedContent(
        modifier = modifier,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityFeedContent(
    modifier: Modifier = Modifier,
    uiState: ActivityFeedUiState,
    event: ActivityFeedEvent?,
    navActionManager: NavActionManager,
) {
    val pullRefreshState = rememberPullToRefreshState()

    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { event?.refreshList() },
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState
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
                items = uiState.activities,
                contentType = { it }
            ) { item ->
                item.onListActivity?.listActivityFragment?.let {
                    ActivityFeedItem(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        type = ActivityType.MEDIA_LIST,
                        username = it.user?.name,
                        avatarUrl = it.user?.avatar?.medium,
                        createdAt = it.createdAt,
                        text = it.text(),
                        replyCount = it.replyCount,
                        likeCount = it.likeCount,
                        isLiked = it.isLiked,
                        mediaCoverUrl = it.media?.coverImage?.medium,
                        onClick = {
                            navActionManager.toActivityDetails(it.id)
                        },
                        onClickUser = {
                            it.userId?.let(navActionManager::toUserDetails)
                        },
                        onClickLike = {
                            event?.toggleLikeActivity(it.id)
                        },
                        onClickMedia = {
                            it.media?.id?.let(navActionManager::toMediaDetails)
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
    }//:Box
}

@Preview
@Composable
fun ActivityFeedViewPreview() {
    AniHyouTheme {
        Surface {
            ActivityFeedContent(
                uiState = ActivityFeedUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}