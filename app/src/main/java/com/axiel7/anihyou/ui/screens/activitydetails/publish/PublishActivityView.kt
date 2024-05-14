package com.axiel7.anihyou.ui.screens.activitydetails.publish

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
data class PublishActivity(
    val activityId: Int = 0,
    val id: Int = 0,
    val text: String? = null
)

@Composable
fun PublishActivityView(
    arguments: PublishActivity,
    navActionManager: NavActionManager
) {
    val viewModel: PublishActivityViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PublishActivityContent(
        arguments = arguments,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@Composable
private fun PublishActivityContent(
    arguments: PublishActivity,
    uiState: PublishActivityUiState,
    event: PublishActivityEvent?,
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
        if (uiState.wasPublished == true) navActionManager.goBack()
    }

    PublishMarkdownView(
        onPublish = { finalText ->
            if (arguments.activityId != 0) {
                event?.publishActivityReply(arguments.activityId, arguments.id, finalText)
            } else {
                event?.publishActivity(arguments.id, finalText)
            }
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
            PublishActivityContent(
                arguments = PublishActivity(text = "This is a preview"),
                uiState = PublishActivityUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}