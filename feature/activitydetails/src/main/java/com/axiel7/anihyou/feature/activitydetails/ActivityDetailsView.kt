package com.axiel7.anihyou.feature.activitydetails

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.activity.text
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.activitydetails.composables.ActivityTextView
import com.axiel7.anihyou.feature.activitydetails.composables.ActivityTextViewPlaceholder
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ActivityDetailsView(
    arguments: Routes.ActivityDetails,
    navActionManager: NavActionManager,
) {
    val viewModel: ActivityDetailsViewModel = koinViewModel(parameters = { parametersOf(arguments) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ActivityDetailsContent(
        activityId = arguments.id,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityDetailsContent(
    activityId: Int,
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

    LifecycleResumeEffect(Unit) {
        if (!uiState.isLoading) {
            event?.refresh()
        }
        onPauseOrDispose {  }
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
        PullToRefreshBox(
            isRefreshing = uiState.fetchFromNetwork,
            onRefresh = { event?.refresh() },
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                state = listState,
                contentPadding = PaddingValues(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = padding.calculateBottomPadding() + 88.dp
                )
            ) {
                item {
                    if (uiState.details != null) {
                        ActivityTextView(
                            modifier = Modifier.padding(16.dp),
                            text = uiState.details.text
                                ?: uiState.details.listActivityFragment?.text().orEmpty(),
                            username = uiState.details.username,
                            avatarUrl = uiState.details.avatarUrl,
                            mediaCoverUrl = uiState.details.mediaCoverUrl,
                            createdAt = uiState.details.createdAt,
                            replyCount = uiState.details.replyCount,
                            likeCount = uiState.details.likeCount,
                            likes = uiState.details.likes,
                            isLiked = uiState.details.isLiked,
                            onClickUser = {
                                uiState.details.userId?.let(navActionManager::toUserDetails)
                            },
                            onClickMedia = {
                                uiState.details.mediaId?.let(navActionManager::toMediaDetails)
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
                    items = uiState.replies,
                    contentType = { it }
                ) { item ->
                    ActivityTextView(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        text = item.text.orEmpty(),
                        username = item.username,
                        avatarUrl = item.avatarUrl,
                        createdAt = item.createdAt,
                        replyCount = null,
                        likeCount = item.likeCount,
                        likes = item.likes,
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
}

@Preview
@Composable
fun ActivityDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            ActivityDetailsContent(
                activityId = 1,
                uiState = ActivityDetailsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
            )
        }
    }
}