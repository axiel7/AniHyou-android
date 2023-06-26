package com.axiel7.anihyou.ui.screens.thread

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.thread.ParentThreadView
import com.axiel7.anihyou.ui.composables.thread.ParentThreadViewPlaceholder
import com.axiel7.anihyou.ui.composables.thread.ThreadCommentView
import com.axiel7.anihyou.ui.composables.thread.ThreadCommentViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val THREAD_DETAILS_DESTINATION = "thread/{id}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadDetailsView(
    threadId: Int,
    navigateToUserDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: ThreadDetailsViewModel = viewModel()
    val listState = rememberLazyListState()

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    LaunchedEffect(threadId) {
        viewModel.getThreadDetails(threadId)
    }

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getThreadComments(threadId)
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
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
                contentType = viewModel.threadDetails
            ) {
                if (viewModel.threadDetails != null) {
                    ParentThreadView(
                        thread = viewModel.threadDetails!!.basicThreadDetails,
                        navigateToUserDetails = navigateToUserDetails,
                        navigateToFullscreenImage = navigateToFullscreenImage,
                    )
                } else {
                    ParentThreadViewPlaceholder()
                }
                Divider()
            }
            items(
                items = viewModel.threadComments,
                contentType = { it }
            ) { item ->
                ThreadCommentView(
                    body = item.comment ?: "",
                    username = item.user?.name ?: "",
                    avatarUrl = item.user?.avatar?.medium,
                    likeCount = item.likeCount,
                    createdAt = item.createdAt,
                    navigateToUserDetails = {
                        navigateToUserDetails(item.user!!.id)
                    },
                    navigateToFullscreenImage = navigateToFullscreenImage,
                )
                Divider()
            }
            if (viewModel.isLoading) {
                items(10) {
                    ThreadCommentViewPlaceholder()
                    Divider()
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