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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.data.model.activity.text
import com.axiel7.anihyou.type.ActivityType
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.screens.profile.ProfileViewModel
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserActivityView(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToActivityDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String) -> Unit
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPageActivity)
            viewModel.getUserActivity(viewModel.userId)
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(top = 8.dp)
    ) {
        items(
            items = viewModel.userActivities,
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
                    onClick = {
                        navigateToActivityDetails(activity.id)
                    },
                    onClickImage = {
                        activity.media?.id?.let(navigateToMediaDetails)
                    },
                    onClickLike = {
                        viewModel.toggleLikeActivity(activity.id)
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
                    onClick = {
                        navigateToActivityDetails(activity.id)
                    },
                    onClickImage = {
                        activity.userId?.let(navigateToUserDetails)
                    },
                    onClickLike = {
                        viewModel.toggleLikeActivity(activity.id)
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
                    onClick = {
                        activity.messengerId?.let(navigateToUserDetails)
                    },
                    onClickLike = {
                        viewModel.toggleLikeActivity(activity.id)
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
        if (viewModel.isLoadingActivity) {
            items(10) {
                ActivityItemPlaceholder(
                    modifier = Modifier.padding(vertical = 8.dp)
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
                viewModel = viewModel(),
                navigateToMediaDetails = {},
                navigateToUserDetails = {},
                navigateToActivityDetails = {},
                navigateToFullscreenImage = {}
            )
        }
    }
}