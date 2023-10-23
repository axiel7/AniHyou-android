package com.axiel7.anihyou.ui.screens.thread.composables

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
import com.axiel7.anihyou.data.model.thread.ChildComment
import com.axiel7.anihyou.ui.composables.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.common.CommentIconButton
import com.axiel7.anihyou.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.common.ReplyButton
import com.axiel7.anihyou.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.DateUtils.timestampIntervalSinceNow
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit

@Composable
fun ChildCommentView(
    comment: ChildComment,
    modifier: Modifier = Modifier,
    toggleLike: suspend (Int) -> Boolean,
    navigateToUserDetails: () -> Unit,
    navigateToPublishReply: (parentCommentId: Int, Int?, String?) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isLiked by remember { mutableStateOf(comment.isLiked ?: false) }
    var showChildComments by remember { mutableStateOf(false) }
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
                markdown = comment.comment ?: "",
                modifier = Modifier.padding(vertical = 8.dp),
                fontSize = 15.sp,
                lineHeight = 15.sp,
                navigateToFullscreenImage = navigateToFullscreenImage
            )
            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (!comment.childComments.isNullOrEmpty()) {
                    CommentIconButton(
                        modifier = Modifier.width(78.dp),
                        commentCount = comment.childComments.size,
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
                        fontSize = 14.sp,
                        iconSize = 20.dp,
                    )
                }
            }
        }//:Column
    }//:Row
    if (showChildComments) {
        comment.childComments?.filterNotNull()?.forEach {
            ChildCommentView(
                comment = it,
                modifier = Modifier.padding(start = 16.dp),
                toggleLike = toggleLike,
                navigateToUserDetails = navigateToUserDetails,
                navigateToPublishReply = navigateToPublishReply,
                navigateToFullscreenImage = navigateToFullscreenImage,
            )
        }
    }
}

@Preview
@Composable
fun ChildCommentViewPreview() {
    AniHyouTheme {
        Surface {
            ChildCommentView(
                comment = ChildComment.preview,
                toggleLike = { true },
                navigateToUserDetails = {},
                navigateToPublishReply = { _, _, _ -> },
                navigateToFullscreenImage = {}
            )
        }
    }
}