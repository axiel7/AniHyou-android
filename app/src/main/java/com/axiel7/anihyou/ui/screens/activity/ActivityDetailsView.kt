package com.axiel7.anihyou.ui.screens.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.screens.activity.composables.ActivityTextView
import com.axiel7.anihyou.ui.screens.activity.composables.ActivityTextViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val ACTIVITY_ID_ARGUMENT = "{id}"
const val ACTIVITY_DETAILS_DESTINATION = "activity/$ACTIVITY_ID_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsView(
    activityId: Int,
    navigateBack: () -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    val viewModel = viewModel { ActivityDetailsViewModel(activityId) }
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val listState = rememberLazyListState()

    LaunchedEffect(activityId) {
        if (viewModel.activityDetails == null) viewModel.getActivityDetails()
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.activity),
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .padding(padding),
            state = listState,
            contentPadding = PaddingValues(top = 8.dp)
        ) {
            item {
                if (viewModel.activityDetails != null) {
                    ActivityTextView(
                        text = viewModel.activityDetails?.text ?: "",
                        username = viewModel.activityDetails?.username,
                        avatarUrl = viewModel.activityDetails?.avatarUrl,
                        createdAt = viewModel.activityDetails?.createdAt ?: 0,
                        replyCount = viewModel.activityDetails?.replyCount,
                        likeCount = viewModel.activityDetails?.likeCount ?: 0,
                        isLiked = viewModel.activityDetails?.isLiked,
                        onClickUser = {
                            viewModel.activityDetails?.userId?.let(navigateToUserDetails)
                        },
                        onClickLike = {
                            viewModel.toggleLikeActivity()
                        },
                        navigateToFullscreenImage = navigateToFullscreenImage
                    )
                } else {
                    ActivityTextViewPlaceholder()
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
            items(
                items = viewModel.replies,
                key = { it.id },
                contentType = { it }
            ) { item ->
                ActivityTextView(
                    text = item.text ?: "",
                    username = item.user?.name,
                    avatarUrl = item.user?.avatar?.medium,
                    createdAt = item.createdAt,
                    replyCount = null,
                    likeCount = item.likeCount,
                    isLiked = item.isLiked,
                    onClickUser = {
                        item.user?.id?.let(navigateToUserDetails)
                    },
                    onClickLike = {
                        viewModel.toggleLikeReply(item.id)
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage
                )
            }
        }
    }
}

@Preview
@Composable
fun ActivityDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            ActivityDetailsView(
                activityId = 1,
                navigateBack = {},
                navigateToUserDetails = {},
                navigateToFullscreenImage = {},
            )
        }
    }
}