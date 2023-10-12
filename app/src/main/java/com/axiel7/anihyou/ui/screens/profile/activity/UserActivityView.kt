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
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserActivityView(
    activities: List<UserActivityQuery.Activity>,
    isLoading: Boolean,
    loadMore: () -> Unit,
    toggleLike: (Int) -> Unit,
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToActivityDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String) -> Unit
) {
    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3, onLoadMore = loadMore)

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(top = 8.dp)
    ) {
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
                        navigateToActivityDetails(activity.id)
                    },
                    onClickImage = {
                        activity.media?.id?.let(navigateToMediaDetails)
                    },
                    onClickLike = {
                        toggleLike(activity.id)
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
            item.onTextActivity?.textActivityFragment?.let { activity ->
                ActivityItem(
                    type = ActivityType.TEXT,
                    text = activity.text ?: "",
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
                        navigateToActivityDetails(activity.id)
                    },
                    onClickImage = {
                        activity.userId?.let(navigateToUserDetails)
                    },
                    onClickLike = {
                        toggleLike(activity.id)
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
            item.onMessageActivity?.messageActivityFragment?.let { activity ->
                ActivityItem(
                    type = ActivityType.MESSAGE,
                    text = activity.message ?: "",
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
                        navigateToActivityDetails(activity.id)
                    },
                    onClickImage = {
                        activity.messengerId?.let(navigateToUserDetails)
                    },
                    onClickLike = {
                        toggleLike(activity.id)
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
        if (isLoading) {
            items(10) {
                ActivityItemPlaceholder(
                    modifier = Modifier.padding(8.dp)
                )
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
                isLoading = true,
                loadMore = {},
                toggleLike = {},
                navigateToMediaDetails = {},
                navigateToUserDetails = {},
                navigateToActivityDetails = {},
                navigateToFullscreenImage = {}
            )
        }
    }
}