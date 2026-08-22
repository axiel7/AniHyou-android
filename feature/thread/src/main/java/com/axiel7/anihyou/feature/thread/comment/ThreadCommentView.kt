package com.axiel7.anihyou.feature.thread.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.result.ResultEffect
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampIntervalSinceNow
import com.axiel7.anihyou.core.common.utils.StringUtils.htmlStripped
import com.axiel7.anihyou.core.model.TranslatorApp
import com.axiel7.anihyou.core.model.thread.ChildComment
import com.axiel7.anihyou.core.network.fragment.CommonThreadComment
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalIsLanguageEn
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Route
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.common.ReplyButton
import com.axiel7.anihyou.core.ui.composables.common.TranslateIconButton
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.core.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.nonFutureDateToLegibleText
import com.axiel7.anihyou.feature.thread.composables.ChildCommentView
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ThreadCommentDetailsView(
    arguments: Route.ThreadCommentDetails,
) {
    val navActionManager = LocalNavActionManager.current
    val viewModel: ThreadCommentViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    ResultEffect<CommonThreadComment> {
        viewModel.onCommentPublished(it)
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(scrollableState = scrollState)
    ) { padding ->
        ThreadCommentView(
            id = arguments.childComment.id,
            body = arguments.childComment.comment.orEmpty(),
            username = arguments.childComment.user?.name.orEmpty(),
            avatarUrl = arguments.childComment.user?.avatar?.medium,
            likeCount = arguments.childComment.likeCount,
            isLiked = arguments.childComment.isLiked == true,
            isLocked = arguments.childComment.isLocked,
            createdAt = arguments.childComment.createdAt,
            childComments = arguments.childComment.childComments,
            translatorApp = uiState.translatorApp,
            toggleLike = { viewModel.toggleLikeComment(it) },
            navigateToUserDetails = {
                arguments.childComment.user?.id?.let(navActionManager::toUserDetails)
            },
            navigateToDetails = navActionManager::toThreadCommentDetails,
            navigateToPublishReply = navActionManager::toPublishThreadComment,
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState),
        )
    }
}

@Composable
fun ThreadCommentView(
    id: Int,
    body: String,
    username: String,
    avatarUrl: String?,
    likeCount: Int,
    isLiked: Boolean,
    isLocked: Boolean?,
    createdAt: Int,
    childComments: List<ChildComment?>?,
    translatorApp: TranslatorApp,
    toggleLike: suspend (Int) -> Boolean,
    navigateToUserDetails: () -> Unit,
    navigateToDetails: (ChildComment) -> Unit,
    navigateToPublishReply: (parentCommentId: Int, Int?, String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEnglishLocale = LocalIsLanguageEn.current
    val scope = rememberCoroutineScope()
    var isLikedState by remember { mutableStateOf(isLiked) }
    Column(
        modifier = modifier
            .padding(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp
            )
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PersonItemSmall(
                avatarUrl = avatarUrl,
                username = username,
                isLocked = isLocked,
                fontWeight = FontWeight.SemiBold,
                onClick = navigateToUserDetails
            )
            Text(
                text = createdAt.toLong().timestampIntervalSinceNow()
                    .nonFutureDateToLegibleText(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium
            )
        }
        DefaultMarkdownText(
            markdown = body,
            modifier = Modifier.padding(vertical = 8.dp),
            textStyle = MaterialTheme.typography.bodyMedium,
        )
        Row(
            modifier = Modifier.align(Alignment.End)
        ) {
            if (!isEnglishLocale) {
                TranslateIconButton(
                    text = body.htmlStripped(),
                    app = translatorApp,
                )
            }
            FavoriteIconButton(
                isFavorite = isLikedState,
                favoritesCount = likeCount,
                onClick = {
                    scope.launch { isLikedState = toggleLike(id) }
                },
                fontSize = 14.sp,
                iconSize = 20.dp,
            )
            if (isLocked == false) {
                ReplyButton(
                    onClick = { navigateToPublishReply(id, null, null) },
                    iconSize = 20.dp,
                )
            }
        }
        childComments?.filterNotNull()?.forEach { comment ->
            ChildCommentView(
                comment = comment,
                translatorApp = translatorApp,
                toggleLike = toggleLike,
                navigateToUserDetails = navigateToUserDetails,
                navigateToDetails = navigateToDetails,
                navigateToPublishReply = navigateToPublishReply,
            )
        }
    }
}

@Composable
fun ThreadCommentViewPlaceholder() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Loading",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Loading",
                modifier = Modifier.defaultPlaceholder(visible = true),
                color = MaterialTheme.colorScheme.outline,
                fontSize = 15.sp
            )
        }
        Text(
            text = "This is a loading placeholder of a comment.",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .defaultPlaceholder(visible = true),
            fontSize = 18.sp,
        )
        TextIconHorizontal(
            text = "17",
            icon = R.drawable.favorite_20,
            modifier = Modifier.defaultPlaceholder(visible = true)
        )
    }
}

@Preview
@Composable
private fun ThreadCommentViewPreview() {
    AniHyouTheme {
        Surface {
            Column {
                ThreadCommentView(
                    id = 1,
                    body = "Yet again, even more peak. ".repeat(4),
                    username = "Lap",
                    avatarUrl = "",
                    likeCount = 23,
                    isLiked = false,
                    isLocked = false,
                    createdAt = 1212370032,
                    childComments = listOf(ChildComment.preview, ChildComment.preview),
                    translatorApp = TranslatorApp.DEFAULT,
                    toggleLike = { true },
                    navigateToUserDetails = {},
                    navigateToDetails = {},
                    navigateToPublishReply = { _, _, _ -> },
                )
                ThreadCommentViewPlaceholder()
            }
        }
    }
}