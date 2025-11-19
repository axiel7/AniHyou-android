package com.axiel7.anihyou.feature.thread.publish

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.markdown.PublishMarkdownView
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun PublishCommentView(
    arguments: Routes.PublishComment,
    navActionManager: NavActionManager,
) {
    val viewModel: PublishCommentViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PublishCommentContent(
        arguments = arguments,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@Composable
private fun PublishCommentContent(
    arguments: Routes.PublishComment,
    uiState: PublishCommentUiState,
    event: PublishCommentEvent?,
    navActionManager: NavActionManager,
) {
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
fun PublishActivityViewPreview() {
    AniHyouTheme {
        Surface {
            PublishCommentContent(
                arguments = Routes.PublishComment(),
                uiState = PublishCommentUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}