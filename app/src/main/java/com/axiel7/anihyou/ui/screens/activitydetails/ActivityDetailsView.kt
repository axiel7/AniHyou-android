package com.axiel7.anihyou.ui.screens.activitydetails

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.fragment.ActivityReplyFragment
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.screens.activitydetails.composables.ActivityTextView
import com.axiel7.anihyou.ui.screens.activitydetails.composables.ActivityTextViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun ActivityDetailsView(
    navActionManager: NavActionManager,
) {
    val viewModel: ActivityDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activityId by viewModel.activityId.collectAsStateWithLifecycle()

    ActivityDetailsContent(
        activityId = activityId ?: 0,
        replies = viewModel.replies,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityDetailsContent(
    activityId: Int,
    replies: List<ActivityReplyFragment>,
    uiState: ActivityDetailsUiState,
    event: ActivityDetailsEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val listState = rememberLazyListState()
    val expandedFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.activity),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(R.string.reply))
                },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.reply_24),
                        contentDescription = stringResource(R.string.reply)
                    )
                },
                onClick = { navActionManager.toPublishActivityReply(activityId, null, null) },
                expanded = expandedFab
            )
        },
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
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
                if (uiState.details != null) {
                    ActivityTextView(
                        text = uiState.details.text.orEmpty(),
                        username = uiState.details.username,
                        avatarUrl = uiState.details.avatarUrl,
                        createdAt = uiState.details.createdAt,
                        replyCount = uiState.details.replyCount,
                        likeCount = uiState.details.likeCount,
                        isLiked = uiState.details.isLiked,
                        onClickUser = {
                            uiState.details.userId?.let(navActionManager::toUserDetails)
                        },
                        onClickLike = {
                            event?.toggleLikeActivity()
                        },
                        navigateToFullscreenImage = navActionManager::toFullscreenImage
                    )
                } else {
                    ActivityTextViewPlaceholder()
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
            items(
                items = replies,
                contentType = { it }
            ) { item ->
                ActivityTextView(
                    text = item.text.orEmpty(),
                    username = item.user?.name,
                    avatarUrl = item.user?.avatar?.medium,
                    createdAt = item.createdAt,
                    replyCount = null,
                    likeCount = item.likeCount,
                    isLiked = item.isLiked,
                    onClickUser = {
                        item.userId?.let(navActionManager::toUserDetails)
                    },
                    onClickLike = {
                        event?.toggleLikeReply(item.id)
                    },
                    navigateToFullscreenImage = navActionManager::toFullscreenImage
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
            ActivityDetailsContent(
                activityId = 1,
                replies = emptyList(),
                uiState = ActivityDetailsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
            )
        }
    }
}