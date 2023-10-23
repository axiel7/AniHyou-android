package com.axiel7.anihyou.ui.screens.activitydetails.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.ui.composables.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.common.CommentIconButton
import com.axiel7.anihyou.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.ui.screens.thread.composables.ThreadCommentViewPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.DateUtils.timestampIntervalSinceNow
import java.time.temporal.ChronoUnit

@Composable
fun ActivityTextView(
    modifier: Modifier = Modifier,
    text: String,
    username: String?,
    avatarUrl: String?,
    createdAt: Int,
    replyCount: Int?,
    likeCount: Int,
    isLiked: Boolean?,
    onClickUser: () -> Unit,
    onClickLike: () -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonItemSmall(
                avatarUrl = avatarUrl,
                username = username,
                modifier = Modifier.weight(1f),
                onClick = onClickUser
            )
            Text(
                text = createdAt.toLong().timestampIntervalSinceNow()
                    .secondsToLegibleText(
                        maxUnit = ChronoUnit.WEEKS,
                        isFutureDate = false
                    ),
                color = MaterialTheme.colorScheme.outline,
                fontSize = 15.sp
            )
        }

        DefaultMarkdownText(
            markdown = text,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 17.sp,
            navigateToFullscreenImage = navigateToFullscreenImage,
        )

        Row(
            modifier = Modifier.align(Alignment.End),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (replyCount != null) {
                CommentIconButton(
                    modifier = Modifier.width(78.dp),
                    commentCount = replyCount,
                    onClick = { },
                    fontSize = 14.sp,
                    iconSize = 20.dp,
                )
            }
            FavoriteIconButton(
                modifier = Modifier.width(78.dp),
                isFavorite = isLiked ?: false,
                favoritesCount = likeCount,
                onClick = onClickLike,
                fontSize = 14.sp,
                iconSize = 20.dp,
            )
        }
    }
}

@Composable
fun ActivityTextViewPlaceholder() {
    ThreadCommentViewPlaceholder()
}

@Preview
@Composable
fun ActivityTextViewPreview() {
    AniHyouTheme {
        Surface {
            ActivityTextView(
                text = "I just watched the latest season of __Kanojo, Okarishimasu__ and I want to kms",
                username = "axiel7",
                avatarUrl = null,
                createdAt = 12312321,
                replyCount = 999,
                likeCount = 999,
                isLiked = false,
                onClickLike = {},
                onClickUser = {},
                navigateToFullscreenImage = {},
            )
        }
    }
}