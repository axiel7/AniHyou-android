package com.axiel7.anihyou.feature.thread

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.base.ANILIST_THREAD_URL
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.NotificationIconButton
import com.axiel7.anihyou.core.ui.composables.common.OpenInBrowserIconButton
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.thread.composables.ParentThreadView
import com.axiel7.anihyou.feature.thread.composables.ParentThreadViewPlaceholder
import com.axiel7.anihyou.feature.thread.composables.ThreadCommentView
import com.axiel7.anihyou.feature.thread.composables.ThreadCommentViewPlaceholder
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ThreadDetailsView(
    arguments: Routes.ThreadDetails,
    navActionManager: NavActionManager
) {
    val viewModel: ThreadDetailsViewModel = koinViewModel(parameters = { parametersOf(arguments) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ThreadDetailsContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ThreadDetailsContent(
    uiState: ThreadDetailsUiState,
    event: ThreadDetailsEvent?,
    navActionManager: NavActionManager,
) {
    val pullRefreshState = rememberPullToRefreshState()
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val listState = rememberLazyListState()
    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            NotificationIconButton(
                isActive = uiState.isSubscribed,
                onClick = { event?.subscribeToThread(!uiState.isSubscribed) }
            )
            OpenInBrowserIconButton(
                url = ANILIST_THREAD_URL + uiState.details?.basicThreadDetails?.id
            )
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.fetchFromNetwork,
            onRefresh = { event?.refresh() },
            modifier = Modifier.fillMaxSize(),
            state = pullRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullRefreshState,
                    isRefreshing = uiState.fetchFromNetwork,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(
                        start = padding.calculateStartPadding(LocalLayoutDirection.current),
                        top = padding.calculateTopPadding(),
                        end = padding.calculateEndPadding(LocalLayoutDirection.current)
                    )
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    bottom = padding.calculateBottomPadding()
                )
            ) {
                item(
                    contentType = uiState.details
                ) {
                    if (uiState.details != null) {
                        ParentThreadView(
                            thread = uiState.details.basicThreadDetails,
                            isLiked = uiState.isLiked,
                            onClickLike = { event?.toggleLikeThread() },
                            onClickReply = {
                                navActionManager.toPublishThreadComment(
                                    threadId = uiState.details.basicThreadDetails.id,
                                    commentId = null,
                                    text = null
                                )
                            },
                            navigateToUserDetails = navActionManager::toUserDetails,
                            navigateToFullscreenImage = navActionManager::toFullscreenImage,
                        )
                    } else {
                        ParentThreadViewPlaceholder()
                    }
                    HorizontalDivider()
                }
                items(
                    items = uiState.comments,
                    contentType = { it }
                ) { item ->
                    ThreadCommentView(
                        id = item.id,
                        body = item.comment.orEmpty(),
                        username = item.user?.name.orEmpty(),
                        avatarUrl = item.user?.avatar?.medium,
                        likeCount = item.likeCount,
                        isLiked = item.isLiked == true,
                        isLocked = item.isLocked,
                        createdAt = item.createdAt,
                        childComments = item.childComments,
                        toggleLike = { event?.toggleLikeComment(item.id) ?: false },
                        navigateToUserDetails = {
                            navActionManager.toUserDetails(item.user!!.id)
                        },
                        navigateToPublishReply = { parentCommentId, id, text ->
                            navActionManager.toPublishCommentReply(
                                threadId = uiState.details!!.id,
                                parentCommentId = parentCommentId,
                                commentId = id,
                                text = text,
                            )
                        },
                        navigateToFullscreenImage = navActionManager::toFullscreenImage
                    )
                    HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
                }
                if (uiState.isLoading) {
                    items(10) {
                        ThreadCommentViewPlaceholder()
                        HorizontalDivider()
                    }
                }
            }//: LazyColumn
        }
    }//: Scaffold
}

@Preview
@Composable
fun ThreadDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            ThreadDetailsContent(
                uiState = ThreadDetailsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}