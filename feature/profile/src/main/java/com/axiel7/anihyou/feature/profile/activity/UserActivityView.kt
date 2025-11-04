package com.axiel7.anihyou.feature.profile.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.model.activity.text
import com.axiel7.anihyou.core.network.UserActivityQuery
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.activity.ActivityItem
import com.axiel7.anihyou.core.ui.composables.activity.ActivityItemPlaceholder
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.profile.ProfileEvent
import com.axiel7.anihyou.feature.profile.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UserActivityView(
    activities: List<UserActivityQuery.Activity>,
    uiState: ProfileUiState,
    event: ProfileEvent?,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val pullRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    if (!uiState.isLoadingActivity) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoadingActivity,
        onRefresh = { event?.onRefreshActivities() },
        state = pullRefreshState,
        indicator = {
            PullToRefreshDefaults.LoadingIndicator(
                state = pullRefreshState,
                isRefreshing = uiState.isLoadingActivity,
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    ) {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            if (uiState.isLoadingActivity) {
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
                item.onListActivity?.listActivityFragment?.let { activity ->
                    ActivityItem(
                        type = ActivityType.MEDIA_LIST,
                        text = activity.text(),
                        createdAt = activity.createdAt,
                        replyCount = activity.replyCount,
                        likeCount = activity.likeCount,
                        isLiked = activity.isLiked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        imageUrl = activity.media?.coverImage?.medium,
                        isLocked = activity.isLocked,
                        onClick = {
                            navActionManager.toActivityDetails(activity.id)
                        },
                        onClickImage = {
                            activity.media?.id?.let(navActionManager::toMediaDetails)
                        },
                        onClickLike = {
                            event?.toggleLikeActivity(activity.id)
                        },
                        onClickDelete = {
                            event?.deleteActivity(activity.id)
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                }
                item.onTextActivity?.textActivityFragment?.let { activity ->
                    ActivityItem(
                        type = ActivityType.TEXT,
                        text = activity.text.orEmpty(),
                        createdAt = activity.createdAt,
                        replyCount = activity.replyCount,
                        likeCount = activity.likeCount,
                        isLiked = activity.isLiked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        imageUrl = activity.user?.activityUser?.avatar?.medium,
                        username = activity.user?.activityUser?.name,
                        isLocked = activity.isLocked,
                        onClick = {
                            navActionManager.toActivityDetails(activity.id)
                        },
                        onClickImage = {
                            activity.userId?.let(navActionManager::toUserDetails)
                        },
                        onClickLike = {
                            event?.toggleLikeActivity(activity.id)
                        },
                        onClickDelete = {
                            event?.deleteActivity(activity.id)
                        },
                        navigateToFullscreenImage = navActionManager::toFullscreenImage
                    )
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                }
                item.onMessageActivity?.messageActivityFragment?.let { activity ->
                    ActivityItem(
                        type = ActivityType.MESSAGE,
                        text = activity.message.orEmpty(),
                        createdAt = activity.createdAt,
                        replyCount = activity.replyCount,
                        likeCount = activity.likeCount,
                        isLiked = activity.isLiked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        imageUrl = activity.messenger?.activityUser?.avatar?.medium,
                        username = activity.messenger?.activityUser?.name,
                        isPrivate = activity.isPrivate,
                        isLocked = activity.isLocked,
                        onClick = {
                            navActionManager.toActivityDetails(activity.id)
                        },
                        onClickImage = {
                            activity.messengerId?.let(navActionManager::toUserDetails)
                        },
                        onClickLike = {
                            event?.toggleLikeActivity(activity.id)
                        },
                        onClickDelete = {
                            event?.deleteActivity(activity.id)
                        },
                        navigateToFullscreenImage = navActionManager::toFullscreenImage
                    )
                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                }
            }
        }//: LazyColumn
    }
}

@Preview
@Composable
fun UserActivityViewPreview() {
    AniHyouTheme {
        Surface {
            UserActivityView(
                activities = emptyList(),
                uiState = ProfileUiState(isMyProfile = false),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}