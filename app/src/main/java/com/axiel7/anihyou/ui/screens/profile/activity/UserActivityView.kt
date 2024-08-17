package com.axiel7.anihyou.ui.screens.profile.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.UserActivityQuery
import com.axiel7.anihyou.data.model.activity.text
import com.axiel7.anihyou.type.ActivityType
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.screens.profile.ProfileEvent
import com.axiel7.anihyou.ui.screens.profile.ProfileUiState
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserActivityView(
    activities: List<UserActivityQuery.Activity>,
    uiState: ProfileUiState,
    event: ProfileEvent?,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val listState = rememberLazyListState()
    if (!uiState.isLoadingActivity) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }

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
                    imageUrl = activity.user?.avatar?.medium,
                    username = activity.user?.name,
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
                    imageUrl = activity.messenger?.avatar?.medium,
                    username = activity.messenger?.name,
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