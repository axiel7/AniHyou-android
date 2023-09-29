package com.axiel7.anihyou.ui.screens.thread

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.OpenInBrowserIconButton
import com.axiel7.anihyou.ui.screens.thread.composables.ParentThreadView
import com.axiel7.anihyou.ui.screens.thread.composables.ParentThreadViewPlaceholder
import com.axiel7.anihyou.ui.screens.thread.composables.ThreadCommentView
import com.axiel7.anihyou.ui.screens.thread.composables.ThreadCommentViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ANILIST_THREAD_URL

const val THREAD_ID_ARGUMENT = "{id}"
const val THREAD_DETAILS_DESTINATION = "thread/$THREAD_ID_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadDetailsView(
    threadId: Int,
    navigateToUserDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel = viewModel { ThreadDetailsViewModel(threadId) }
    val listState = rememberLazyListState()

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    val threadDetailsState by viewModel.threadDetails.collectAsState()
    val threadDetails by remember {
        derivedStateOf { (threadDetailsState as? DataResult.Success)?.data }
    }

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getThreadComments(threadId)
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            OpenInBrowserIconButton(url = ANILIST_THREAD_URL + threadId)
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                bottom = padding.calculateBottomPadding()
            )
        ) {
            item(
                contentType = threadDetails
            ) {
                if (threadDetails != null) {
                    ParentThreadView(
                        thread = threadDetails!!.basicThreadDetails,
                        navigateToUserDetails = navigateToUserDetails,
                    )
                } else {
                    ParentThreadViewPlaceholder()
                }
                HorizontalDivider()
            }
            items(
                items = viewModel.threadComments,
                key = { it.id },
                contentType = { it }
            ) { item ->
                ThreadCommentView(
                    body = item.comment ?: "",
                    username = item.user?.name ?: "",
                    avatarUrl = item.user?.avatar?.medium,
                    likeCount = item.likeCount,
                    isLiked = viewModel.isLiked,
                    createdAt = item.createdAt,
                    childComments = item.childComments,
                    toggleLike = viewModel::toggleLikeThread,
                    toggleLikeComment = {
                        viewModel.toggleLikeComment(it)
                    },
                    navigateToUserDetails = {
                        navigateToUserDetails(item.user!!.id)
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage
                )
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
            }
            if (viewModel.isLoading) {
                items(10) {
                    ThreadCommentViewPlaceholder()
                    HorizontalDivider()
                }
            }
        }//: LazyColumn
    }//: Scaffold
}

@Preview
@Composable
fun ThreadDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            ThreadDetailsView(
                threadId = 1,
                navigateToUserDetails = {},
                navigateToFullscreenImage = {},
                navigateBack = {}
            )
        }
    }
}