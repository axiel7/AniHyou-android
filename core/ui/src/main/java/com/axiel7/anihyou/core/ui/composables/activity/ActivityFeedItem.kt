package com.axiel7.anihyou.core.ui.composables.activity

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampIntervalSinceNow
import com.axiel7.anihyou.core.network.type.ActivityType
import com.axiel7.anihyou.core.ui.composables.common.CommentIconButton
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.nonFutureDateToLegibleText

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
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        onClick = onClick,
    ) {
        Column(modifier = modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PersonItemSmall(
                    avatarUrl = avatarUrl,
                    username = username,
                    onClick = onClickUser,
                    textStyle = MaterialTheme.typography.labelLarge,
                )
                Text(
                    text = createdAt.toLong().timestampIntervalSinceNow()
                        .nonFutureDateToLegibleText(),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                if (type == ActivityType.MEDIA_LIST) {
                    MediaPoster(
                        url = mediaCoverUrl,
                        enableBlur = blurCover,
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
                    modifier = Modifier.weight(1f)
                ) {
                    if (type == ActivityType.TEXT) {
                        DefaultMarkdownText(
                            markdown = text,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    } else {
                        Text(
                            text = text,
                            modifier = Modifier.padding(bottom = 4.dp),
                            lineHeight = 20.sp
                        )
                    }

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
                }
            }
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
            )
        }
    }
}