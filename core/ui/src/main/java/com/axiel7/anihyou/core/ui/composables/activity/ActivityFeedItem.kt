package com.axiel7.anihyou.core.ui.composables.activity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.ui.composables.common.CommentIconButton
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.core.ui.utils.DateUtils.timestampIntervalSinceNow
import java.time.temporal.ChronoUnit

@Composable
fun ActivityFeedItem(
    modifier: Modifier = Modifier,
    type: ActivityType,
    username: String?,
    avatarUrl: String?,
    createdAt: Int,
    text: String,
    replyCount: Int,
    likeCount: Int,
    isLiked: Boolean?,
    mediaCoverUrl: String? = null,
    showMenu: Boolean = false,
    onClick: () -> Unit,
    onClickUser: () -> Unit,
    onClickLike: () -> Unit,
    onClickMedia: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    navigateToFullscreenImage: (String) -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
    ) {
        if (type == ActivityType.MEDIA_LIST) {
            MediaPoster(
                url = mediaCoverUrl,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(
                        width = 48.dp,
                        height = 74.dp
                    )
                    .clickable(onClick = onClickMedia),
                showShadow = false
            )
        }

        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                PersonItemSmall(
                    avatarUrl = avatarUrl,
                    username = username,
                    onClick = onClickUser
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
            }//:Row
            if (type == ActivityType.TEXT) {
                DefaultMarkdownText(
                    markdown = text,
                    modifier = Modifier.padding(bottom = 4.dp),
                    lineHeight = 20.sp,
                    navigateToFullscreenImage = navigateToFullscreenImage
                )
            } else {
                Text(
                    text = text,
                    modifier = Modifier.padding(bottom = 4.dp),
                    lineHeight = 20.sp
                )
            }
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CommentIconButton(
                    modifier = Modifier.width(78.dp),
                    commentCount = replyCount,
                    onClick = onClick,
                    fontSize = 14.sp,
                    iconSize = 20.dp,
                )
                FavoriteIconButton(
                    modifier = Modifier.width(78.dp),
                    isFavorite = isLiked ?: false,
                    favoritesCount = likeCount,
                    onClick = onClickLike,
                    fontSize = 14.sp,
                    iconSize = 20.dp,
                )
                if (showMenu) {
                    ActivityMenu(
                        onClickDelete = onClickDelete
                    )
                }
            }
        }//:Column
    }//Row
}

@Preview
@Composable
fun MediaActivityItemPreview() {
    AniHyouTheme {
        Surface {
            ActivityFeedItem(
                type = ActivityType.TEXT,
                username = "axiel7",
                avatarUrl = null,
                createdAt = 12312321,
                text = "I just watched the latest season of Kanojo, Okarishimasu and I want to kms",
                replyCount = 999,
                likeCount = 999,
                isLiked = false,
                mediaCoverUrl = "",
                showMenu = true,
                onClick = {},
                onClickUser = {},
                onClickLike = {},
                onClickMedia = {},
            )
        }
    }
}