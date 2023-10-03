package com.axiel7.anihyou.ui.screens.thread.publish

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.ui.composables.markdown.PublishMarkdownView
import com.axiel7.anihyou.ui.screens.thread.THREAD_ID_ARGUMENT
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.showToast

const val PARENT_COMMENT_ID_ARGUMENT = "{parent_comment_id}"
const val COMMENT_ID_ARGUMENT = "{comment_id}"
const val COMMENT_TEXT_ARGUMENT = "{text}"
const val PUBLISH_COMMENT_REPLY_DESTINATION =
    "comment/$PARENT_COMMENT_ID_ARGUMENT/publish/$COMMENT_ID_ARGUMENT?text=$COMMENT_TEXT_ARGUMENT"
const val PUBLISH_THREAD_COMMENT_DESTINATION =
    "thread/$THREAD_ID_ARGUMENT/comment/publish/$COMMENT_ID_ARGUMENT?text=$COMMENT_TEXT_ARGUMENT"

@Composable
fun PublishCommentView(
    threadId: Int?,
    parentCommentId: Int?,
    id: Int? = null,
    text: String? = null,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: PublishCommentViewModel = viewModel()

    LaunchedEffect(viewModel.message) {
        if (viewModel.message != null) {
            context.showToast(viewModel.message)
            viewModel.message = null
        }
    }

    LaunchedEffect(viewModel.wasPublished) {
        if (viewModel.wasPublished == true) navigateBack()
    }

    PublishMarkdownView(
        onPublish = {
            viewModel.publishThreadComment(
                threadId = threadId,
                parentCommentId = parentCommentId,
                id = id,
                text = it
            )
        },
        isLoading = viewModel.isLoading,
        initialText = text,
        navigateBack = navigateBack
    )
}

@Preview
@Composable
fun PublishActivityViewPreview() {
    AniHyouTheme {
        Surface {
            PublishCommentView(
                threadId = 1,
                parentCommentId = null,
                navigateBack = {}
            )
        }
    }
}