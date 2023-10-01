package com.axiel7.anihyou.ui.screens.activity.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.axiel7.anihyou.ui.composables.CommentIconButton
import com.axiel7.anihyou.ui.composables.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_VERY_SMALL
import com.axiel7.anihyou.ui.composables.person.PersonImage
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
            modifier = Modifier.clickable(onClick = onClickUser),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonImage(
                url = avatarUrl,
                modifier = Modifier
                    .size(PERSON_IMAGE_SIZE_VERY_SMALL.dp)
            )
            Text(
                text = username ?: "",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
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