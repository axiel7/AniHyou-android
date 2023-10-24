package com.axiel7.anihyou.ui.screens.thread.publish

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.ui.composables.markdown.PublishMarkdownView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.showToast

@Composable
fun PublishCommentView(
    threadId: Int?,
    parentCommentId: Int?,
    id: Int? = null,
    text: String? = null,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: PublishCommentViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            context.showToast(uiState.error)
            viewModel.onErrorDisplayed()
        }
    }

    LaunchedEffect(uiState.wasPublished) {
        if (uiState.wasPublished == true) {
            viewModel.setWasPublished(false)
            navigateBack()
        }
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
        isLoading = uiState.isLoading,
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