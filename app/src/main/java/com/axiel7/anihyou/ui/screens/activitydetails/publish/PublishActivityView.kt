package com.axiel7.anihyou.ui.screens.activitydetails.publish

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
fun PublishActivityView(
    activityId: Int? = null,
    id: Int? = null,
    text: String? = null,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: PublishActivityViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            context.showToast(uiState.error)
            viewModel.onErrorDisplayed()
        }
    }

    LaunchedEffect(uiState.wasPublished) {
        if (uiState.wasPublished == true) navigateBack()
    }

    PublishMarkdownView(
        onPublish = { finalText ->
            if (activityId != null) {
                viewModel.publishActivityReply(activityId, id, finalText)
            } else {
                viewModel.publishActivity(id, finalText)
            }
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
            PublishActivityView(
                activityId = null,
                navigateBack = {}
            )
        }
    }
}