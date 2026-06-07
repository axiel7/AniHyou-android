package com.axiel7.anihyou.feature.thread.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampIntervalSinceNow
import com.axiel7.anihyou.core.common.utils.StringUtils.htmlStripped
import com.axiel7.anihyou.core.model.TranslatorApp
import com.axiel7.anihyou.core.model.thread.ChildComment
import com.axiel7.anihyou.core.ui.composables.common.CommentIconButton
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.common.ReplyButton
import com.axiel7.anihyou.core.ui.composables.common.TranslateIconButton
import com.axiel7.anihyou.core.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.core.ui.composables.markdown.MarkdownUriHandler
import com.axiel7.anihyou.core.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.secondsToLegibleText
import com.axiel7.anihyou.core.ui.utils.LocaleUtils.LocalIsLanguageEn
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit

@Composable
fun ChildCommentView(
    comment: ChildComment,
    translatorApp: TranslatorApp,
    modifier: Modifier = Modifier,
    toggleLike: suspend (Int) -> Boolean,
    navigateToUserDetails: () -> Unit,
    navigateToPublishReply: (parentCommentId: Int, Int?, String?) -> Unit,
    uriHandler: MarkdownUriHandler,
) {
    val isEnglishLocale = LocalIsLanguageEn.current
    val scope = rememberCoroutineScope()
    var isLiked by remember { mutableStateOf(comment.isLiked == true) }
    var showChildComments by remember { mutableStateOf(false) }
    val hasComments = !comment.childComments.isNullOrEmpty()

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        VerticalDivider(
            modifier = Modifier.padding(end = 16.dp)
        )
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PersonItemSmall(
                    avatarUrl = comment.user?.avatar?.medium,
                    username = comment.user?.name,
                    isLocked = comment.isLocked,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    onClick = navigateToUserDetails
                )
                Text(
                    text = comment.createdAt.toLong().timestampIntervalSinceNow()
                        .secondsToLegibleText(
                            maxUnit = ChronoUnit.WEEKS,
                            isFutureDate = false
                        ),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 14.sp
                )
            }
            DefaultMarkdownText(
                markdown = comment.comment.orEmpty(),
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 15.sp,
                lineHeight = 15.sp,
                uriHandler = uriHandler,
            )
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!isEnglishLocale) {
                    TranslateIconButton(
                        text = comment.comment?.htmlStripped(),
                        app = translatorApp,
                    )
                }
                if (hasComments) {
                    CommentIconButton(
                        modifier = Modifier.width(78.dp),
                        commentCount = comment.childComments?.size ?: 0,
                        onClick = { showChildComments = !showChildComments },
                        fontSize = 14.sp,
                        iconSize = 20.dp,
                    )
                }
                FavoriteIconButton(
                    modifier = Modifier.width(78.dp),
                    isFavorite = isLiked,
                    favoritesCount = comment.likeCount,
                    onClick = {
                        scope.launch { isLiked = toggleLike(comment.id) }
                    },
                    fontSize = 14.sp,
                    iconSize = 20.dp,
                )
                if (comment.isLocked == false) {
                    ReplyButton(
                        onClick = { navigateToPublishReply(comment.id, null, null) },
                        iconSize = 20.dp,
                    )
                }
            }
        }//:Column
    }//:Row
    if (hasComments) {
        AnimatedVisibility(
            visible = showChildComments,
            enter = fadeIn() + slideInVertically(),
            exit = slideOutVertically() + fadeOut(),
        ) {
            Column {
                comment.childComments?.filterNotNull()?.forEach {
                    ChildCommentView(
                        comment = it,
                        translatorApp = translatorApp,
                        modifier = Modifier.padding(start = 16.dp),
                        toggleLike = toggleLike,
                        navigateToUserDetails = navigateToUserDetails,
                        navigateToPublishReply = navigateToPublishReply,
                        uriHandler = uriHandler,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChildCommentViewPreview() {
    AniHyouTheme {
        Surface {
            ChildCommentView(
                comment = ChildComment.preview,
                translatorApp = TranslatorApp.DEFAULT,
                toggleLike = { true },
                navigateToUserDetails = {},
                navigateToPublishReply = { _, _, _ -> },
                uriHandler = MarkdownUriHandler(),
            )
        }
    }
}