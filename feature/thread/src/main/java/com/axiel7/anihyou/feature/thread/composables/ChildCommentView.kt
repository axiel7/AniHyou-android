package com.axiel7.anihyou.feature.thread.composables

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.StringUtils.htmlStripped
import com.axiel7.anihyou.core.model.TranslatorApp
import com.axiel7.anihyou.core.model.thread.ChildComment
import com.axiel7.anihyou.core.ui.common.LocalIsLanguageEn
import com.axiel7.anihyou.core.ui.composables.common.CommentIconButton
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.common.ReplyButton
import com.axiel7.anihyou.core.ui.composables.common.TranslateIconButton
import com.axiel7.anihyou.core.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.core.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.dateToRelativeText
import kotlinx.coroutines.launch

@Composable
fun ChildCommentView(
    comment: ChildComment,
    translatorApp: TranslatorApp,
    modifier: Modifier = Modifier,
    toggleLike: suspend (Int) -> Boolean,
    navigateToUserDetails: () -> Unit,
    navigateToDetails: (ChildComment) -> Unit,
    navigateToPublishReply: (parentCommentId: Int, Int?, String?) -> Unit,
) {
    val isEnglishLocale = LocalIsLanguageEn.current
    val scope = rememberCoroutineScope()
    var isLiked by remember { mutableStateOf(comment.isLiked == true) }
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
                    textStyle = MaterialTheme.typography.labelMedium,
                    onClick = navigateToUserDetails
                )
                Text(
                    text = comment.createdAt.toLong().dateToRelativeText(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            DefaultMarkdownText(
                markdown = comment.comment.orEmpty(),
                modifier = Modifier.padding(vertical = 8.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
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
                        onClick = { navigateToDetails(comment) },
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
                navigateToDetails = {},
                navigateToPublishReply = { _, _, _ -> },
            )
        }
    }
}