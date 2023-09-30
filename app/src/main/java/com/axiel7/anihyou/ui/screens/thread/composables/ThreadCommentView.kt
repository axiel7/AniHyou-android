package com.axiel7.anihyou.ui.screens.thread.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.thread.ChildComment
import com.axiel7.anihyou.ui.composables.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_VERY_SMALL
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.DateUtils.timestampIntervalSinceNow
import java.time.temporal.ChronoUnit

@Composable
fun ThreadCommentView(
    body: String,
    username: String,
    avatarUrl: String?,
    likeCount: Int,
    isLiked: Boolean,
    createdAt: Int,
    childComments: List<ChildComment?>?,
    toggleLike: () -> Unit,
    toggleLikeComment: suspend (Int) -> Boolean,
    navigateToUserDetails: () -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
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
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.clickable {
                    navigateToUserDetails()
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                PersonImage(
                    url = avatarUrl,
                    modifier = Modifier
                        .size(PERSON_IMAGE_SIZE_VERY_SMALL.dp)
                )
                Text(
                    text = username,
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = createdAt.toLong().timestampIntervalSinceNow()
                    .secondsToLegibleText(maxUnit = ChronoUnit.WEEKS),
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FavoriteIconButton(
                isFavorite = isLiked,
                favoritesCount = likeCount,
                onClick = toggleLike,
                fontSize = 14.sp,
                iconSize = 20.dp,
            )
        }
        childComments?.filterNotNull()?.forEach { comment ->
            ChildCommentView(
                comment = comment,
                toggleLike = toggleLikeComment,
                navigateToUserDetails = navigateToUserDetails,
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
                    body = "Yet again, even more peak. ".repeat(4),
                    username = "Lap",
                    avatarUrl = "",
                    likeCount = 23,
                    isLiked = false,
                    createdAt = 1212370032,
                    childComments = listOf(ChildComment.preview, ChildComment.preview),
                    toggleLike = {},
                    toggleLikeComment = { true },
                    navigateToUserDetails = {},
                    navigateToFullscreenImage = {}
                )
                ThreadCommentViewPlaceholder()
            }
        }
    }
}