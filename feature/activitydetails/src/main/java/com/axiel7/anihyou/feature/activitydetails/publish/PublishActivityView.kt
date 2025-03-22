package com.axiel7.anihyou.feature.activitydetails.publish

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes.PublishActivity
import com.axiel7.anihyou.core.ui.composables.markdown.PublishMarkdownView
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.common.utils.ContextUtils.showToast
import org.koin.androidx.compose.koinViewModel

@Composable
fun PublishActivityView(
    arguments: PublishActivity,
    navActionManager: NavActionManager
) {
    val viewModel: PublishActivityViewModel = koinViewModel()
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
            event?.onErrorDisplayed()
            context.showToast(uiState.error)
        }
    }

    LaunchedEffect(uiState.wasPublished) {
        if (uiState.wasPublished == true) navActionManager.goBack()
    }

    PublishMarkdownView(
        onPublish = { finalText ->
            if (arguments.activityId != null) {
                event?.publishActivityReply(arguments.activityId!!, arguments.id, finalText)
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
                arguments = PublishActivity(
                    activityId = null,
                    id = 1,
                    text = "This is a preview"
                ),
                uiState = PublishActivityUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}