package com.axiel7.anihyou.ui.thread

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.thread.ChildCommentView
import com.axiel7.anihyou.ui.composables.thread.ChildCommentViewPlaceholder
import com.axiel7.anihyou.ui.composables.thread.ThreadCommentView
import com.axiel7.anihyou.ui.composables.thread.ThreadCommentViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val COMMENT_DETAILS_DESTINATION = "comment/{id}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentDetailsView(
    commentId: Int,
    navigateToUserDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: CommentDetailsViewModel = viewModel()

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    LaunchedEffect(commentId) {
        viewModel.getChildComments(commentId)
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        ) {
            item(
                contentType = viewModel.comment
            ) {
                if (viewModel.comment != null) {
                    ThreadCommentView(
                        comment = viewModel.comment!!,
                        navigateToUserDetails = navigateToUserDetails,
                        navigateToFullscreenImage = navigateToFullscreenImage,
                    )
                } else {
                    ThreadCommentViewPlaceholder()
                }
                Divider()
            }
            items(
                items = viewModel.childComments,
                contentType = { it }
            ) { item ->
                ChildCommentView(
                    comment = item,
                    navigateToUserDetails = navigateToUserDetails,
                    navigateToFullscreenImage = navigateToFullscreenImage,
                )
            }
            if (viewModel.isLoading) {
                items(10) {
                    ChildCommentViewPlaceholder()
                }
            }
        }//:LazyColumn
    }//:Scaffold
}

@Preview
@Composable
fun CommentDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            CommentDetailsView(
                commentId = 1,
                navigateToUserDetails = {},
                navigateToFullscreenImage = {},
                navigateBack = {}
            )
        }
    }
}