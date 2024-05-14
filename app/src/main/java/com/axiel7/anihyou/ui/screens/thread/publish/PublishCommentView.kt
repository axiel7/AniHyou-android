package com.axiel7.anihyou.ui.screens.thread.publish

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.markdown.PublishMarkdownView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.showToast
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class PublishComment(
    val threadId: Int = 0,
    val parentCommentId: Int = 0,
    val id: Int = 0,
    val text: String? = null,
)

@Composable
fun PublishCommentView(
    arguments: PublishComment,
    navActionManager: NavActionManager,
) {
    val viewModel: PublishCommentViewModel = hiltViewModel()
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
    arguments: PublishComment,
    uiState: PublishCommentUiState,
    event: PublishCommentEvent?,
    navActionManager: NavActionManager,
) {
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            context.showToast(uiState.error)
            event?.onErrorDisplayed()
        }
    }

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
                arguments = PublishComment(),
                uiState = PublishCommentUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}