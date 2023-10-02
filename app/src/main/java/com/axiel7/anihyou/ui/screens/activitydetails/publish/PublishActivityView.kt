package com.axiel7.anihyou.ui.screens.activitydetails.publish

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.ui.composables.markdown.PublishMarkdownView
import com.axiel7.anihyou.ui.screens.activitydetails.ACTIVITY_ID_ARGUMENT
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.showToast

const val ACTIVITY_TEXT_ARGUMENT = "{text}"
const val PUBLISH_ACTIVITY_DESTINATION =
    "activity/publish/$ACTIVITY_ID_ARGUMENT?text=$ACTIVITY_TEXT_ARGUMENT"
const val PUBLISH_ACTIVITY_REPLY_DESTINATION =
    "reply/publish/$ACTIVITY_ID_ARGUMENT?text=$ACTIVITY_TEXT_ARGUMENT"

@Composable
fun PublishActivityView(
    isReply: Boolean,
    id: Int? = null,
    text: String? = null,
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: PublishActivityViewModel = viewModel()

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
            if (isReply) {
                viewModel.publishActivityReply(id = id, text = it)
            } else {
                viewModel.publishActivity(id = id, text = it)
            }
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
            PublishActivityView(
                isReply = false,
                navigateBack = {}
            )
        }
    }
}