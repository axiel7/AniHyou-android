package com.axiel7.anihyou.feature.thread.publish

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Route
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.markdown.PublishMarkdownView
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PublishCommentView(
    arguments: Route.PublishComment,
) {
    val viewModel: PublishCommentViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PublishCommentContent(
        arguments = arguments,
        uiState = uiState,
        event = viewModel,
    )
}

@Composable
private fun PublishCommentContent(
    arguments: Route.PublishComment,
    uiState: PublishCommentUiState,
    event: PublishCommentEvent?,
) {
    val navActionManager = LocalNavActionManager.current
    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    LaunchedEffect(uiState.wasPublished) {
        if (uiState.wasPublished == true) {
            event?.setWasPublished(false)
            navActionManager.goBack()
        }
    }

    PublishMarkdownView(
        onPublish = {
            event?.publishThreadComment(
                threadId = arguments.threadId,
                parentCommentId = arguments.parentCommentId,
                id = arguments.id,
                text = it
            )
        },
        isLoading = uiState.isLoading,
        initialText = arguments.text,
        navigateBack = navActionManager::goBack
    )
}

@Preview
@Composable
private fun PublishActivityViewPreview() {
    AniHyouTheme {
        Surface {
            PublishCommentContent(
                arguments = Route.PublishComment(),
                uiState = PublishCommentUiState(),
                event = null,
            )
        }
    }
}