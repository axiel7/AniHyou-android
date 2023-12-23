package com.axiel7.anihyou.ui.screens.activitydetails.publish

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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

@Composable
fun PublishActivityView(
    activityId: Int? = null,
    id: Int? = null,
    text: String? = null,
    navActionManager: NavActionManager
) {
    val viewModel: PublishActivityViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PublishActivityContent(
        activityId = activityId,
        id = id,
        text = text,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@Composable
private fun PublishActivityContent(
    activityId: Int? = null,
    id: Int? = null,
    text: String? = null,
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
            if (activityId != null) {
                event?.publishActivityReply(activityId, id, finalText)
            } else {
                event?.publishActivity(id, finalText)
            }
        },
        isLoading = uiState.isLoading,
        initialText = text,
        navigateBack = navActionManager::goBack
    )
}

@Preview
@Composable
fun PublishActivityViewPreview() {
    AniHyouTheme {
        Surface {
            PublishActivityContent(
                text = "This is a preview",
                uiState = PublishActivityUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}