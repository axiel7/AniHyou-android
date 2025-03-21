package com.axiel7.anihyou.feature.thread.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.axiel7.anihyou.core.model.thread.ChildComment
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.common.ReplyButton
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.core.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.core.ui.utils.DateUtils.timestampIntervalSinceNow
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit

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
    toggleLike: suspend (Int) -> Boolean,
    navigateToUserDetails: () -> Unit,
    navigateToPublishReply: (parentCommentId: Int, Int?, String?) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var isLikedState by remember { mutableStateOf(isLiked) }
    Column(
        modifier = Modifier
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
                    .secondsToLegibleText(
                        maxUnit = ChronoUnit.WEEKS,
                        isFutureDate = false
                    ),
                color = MaterialTheme.colorScheme.outline,
                fontSize = 14.sp
            )
        }
        DefaultMarkdownText(
            markdown = body,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 15.sp,
            lineHeight = 15.sp,
            navigateToFullscreenImage = navigateToFullscreenImage,
        )
        Row(
            modifier = Modifier.align(Alignment.End)
        ) {
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
                    fontSize = 14.sp,
                    iconSize = 20.dp,
                )
            }
        }
        childComments?.filterNotNull()?.forEach { comment ->
            ChildCommentView(
                comment = comment,
                toggleLike = toggleLike,
                navigateToUserDetails = navigateToUserDetails,
                navigateToPublishReply = navigateToPublishReply,
                navigateToFullscreenImage = navigateToFullscreenImage,
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
fun ThreadCommentViewPreview() {
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
                    toggleLike = { true },
                    navigateToUserDetails = {},
                    navigateToPublishReply = { _, _, _ -> },
                    navigateToFullscreenImage = {}
                )
                ThreadCommentViewPlaceholder()
            }
        }
    }
}