package com.axiel7.anihyou.core.ui.composables.activity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampIntervalSinceNow
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.ui.composables.common.CommentIconButton
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.core.ui.composables.markdown.MarkdownUriHandler
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.secondsToLegibleText
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
    blurCover: Boolean = false,
    mediaCoverUrl: String? = null,
    showMenu: Boolean = false,
    onClick: () -> Unit,
    onClickUser: () -> Unit,
    onClickLike: () -> Unit,
    onClickMedia: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    uriHandler: MarkdownUriHandler,
) {
    ListItem(
        onClick = onClick,
        modifier = modifier,
        overlineContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
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
                )
            }
        },
        leadingContent = {
            if (type == ActivityType.MEDIA_LIST) {
                MediaPoster(
                    url = mediaCoverUrl,
                    enableBlur = blurCover,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(
                            width = 48.dp,
                            height = 74.dp
                        )
                        .clickable(onClick = onClickMedia),
                    showShadow = false
                )
            }
        },
        supportingContent = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
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
        },
        contentPadding = PaddingValues()
    ) {
        if (type == ActivityType.TEXT) {
            DefaultMarkdownText(
                markdown = text,
                modifier = Modifier.padding(bottom = 4.dp),
                lineHeight = 20.sp,
                uriHandler = uriHandler,
            )
        } else {
            Text(
                text = text,
                modifier = Modifier.padding(bottom = 4.dp),
                lineHeight = 20.sp
            )
        }
    }
}

@Preview
@Composable
private fun MediaActivityItemPreview() {
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
                uriHandler = MarkdownUriHandler(),
            )
        }
    }
}