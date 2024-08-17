package com.axiel7.anihyou.ui.screens.profile.activity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.ActivityType
import com.axiel7.anihyou.ui.composables.common.CommentIconButton
import com.axiel7.anihyou.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.media.MediaPoster
import com.axiel7.anihyou.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.DateUtils.timestampIntervalSinceNow
import java.time.temporal.ChronoUnit

const val ACTIVITY_IMAGE_SIZE = 48

@Composable
fun ActivityItem(
    type: ActivityType,
    text: String,
    createdAt: Int,
    replyCount: Int,
    likeCount: Int,
    isLiked: Boolean?,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    username: String? = null,
    isPrivate: Boolean? = null,
    isLocked: Boolean? = null,
    onClick: () -> Unit,
    onClickImage: () -> Unit = {},
    onClickLike: () -> Unit,
    onClickDelete: () -> Unit,
    navigateToFullscreenImage: (String) -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        if (type == ActivityType.MEDIA_LIST) {
            MediaPoster(
                url = imageUrl,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(ACTIVITY_IMAGE_SIZE.dp)
                    .clickable(onClick = onClickImage),
                showShadow = false
            )
        }

        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                if (type == ActivityType.TEXT || type == ActivityType.MESSAGE) {
                    PersonItemSmall(
                        avatarUrl = imageUrl,
                        username = username,
                        modifier = Modifier.padding(bottom = 8.dp),
                        isPrivate = isPrivate,
                        isLocked = isLocked,
                        onClick = onClickImage
                    )
                    DefaultMarkdownText(
                        markdown = text,
                        modifier = Modifier.weight(1f),
                        lineHeight = 20.sp,
                        navigateToFullscreenImage = navigateToFullscreenImage
                    )
                } else {
                    Text(
                        text = text,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .weight(1f),
                        lineHeight = 20.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3
                    )
                }
                ActivityMenu(
                    modifier = Modifier.align(Alignment.Top),
                    onClickDelete = onClickDelete
                )
            }

            Row(
                modifier = Modifier.align(Alignment.End),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = createdAt.toLong().timestampIntervalSinceNow()
                        .secondsToLegibleText(
                            maxUnit = ChronoUnit.WEEKS,
                            isFutureDate = false
                        ),
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
            }
        }
    }
}

@Composable
fun ActivityMenu(
    modifier: Modifier = Modifier,
    onClickDelete: () -> Unit,
) {
    Box(modifier = modifier.wrapContentSize(Alignment.TopEnd)) {
        var moreExpanded by remember { mutableStateOf(false) }
        IconButton(onClick = { moreExpanded = !moreExpanded }) {
            Icon(
                painter = painterResource(R.drawable.more_vert_24),
                contentDescription = stringResource(R.string.show_more),
            )
        }
        DropdownMenu(
            expanded = moreExpanded,
            onDismissRequest = { moreExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(R.string.delete)) },
                onClick = {
                    moreExpanded = false
                    onClickDelete()
                },
            )
        }
    }
}

@Composable
fun ActivityItemPlaceholder(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(ACTIVITY_IMAGE_SIZE.dp)
                .defaultPlaceholder(visible = true)
        )

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "This is a  loading placeholder",
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .defaultPlaceholder(visible = true)
            )

            Text(
                text = "Placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 14.sp,
            )
        }
    }
}

@Preview
@Composable
fun ActivityItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                ActivityItem(
                    type = ActivityType.MEDIA_LIST,
                    text = "Plans to watch Alice to Therese no Maboroshi Koujou",
                    createdAt = 1927389,
                    replyCount = 999,
                    likeCount = 999,
                    isLiked = false,
                    imageUrl = "",
                    modifier = Modifier.padding(8.dp),
                    onClick = {},
                    onClickLike = {},
                    onClickDelete = {},
                )
                ActivityItemPlaceholder(
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}