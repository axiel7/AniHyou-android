package com.axiel7.anihyou.feature.activitydetails.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampIntervalSinceNow
import com.axiel7.anihyou.core.model.activity.exampleActivityUser
import com.axiel7.anihyou.core.network.fragment.ActivityUser
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.common.CommentIconButton
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_TINY_HEIGHT
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.composables.person.PERSON_IMAGE_SIZE_VERY_SMALL
import com.axiel7.anihyou.core.ui.composables.person.PersonImage
import com.axiel7.anihyou.core.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.secondsToLegibleText
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActivityTextView(
    modifier: Modifier = Modifier,
    text: String,
    username: String?,
    avatarUrl: String?,
    mediaCoverUrl: String? = null,
    createdAt: Int,
    replyCount: Int?,
    likeCount: Int,
    likes: List<ActivityUser>?,
    isLiked: Boolean?,
    onClickUser: () -> Unit,
    onClickMedia: () -> Unit = {},
    onClickLike: () -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    var isLikesExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxWidth()
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

        Row(
            modifier = Modifier.padding(
                top = if (mediaCoverUrl != null) 16.dp else 8.dp,
                bottom = 8.dp
            ),
        ) {
            if (mediaCoverUrl != null) {
                MediaPoster(
                    url = mediaCoverUrl,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(MEDIA_POSTER_TINY_HEIGHT.dp)
                        .clickable(onClick = onClickMedia)
                )
            }
            DefaultMarkdownText(
                markdown = text,
                fontSize = 17.sp,
                navigateToFullscreenImage = navigateToFullscreenImage,
            )
        }

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
                modifier = Modifier
                    .width(78.dp)
                    .combinedClickable(onClick = {}, onLongClick = { isLikesExpanded = true }),
                isFavorite = isLiked == true,
                favoritesCount = likeCount,
                onClick = onClickLike,
                fontSize = 14.sp,
                iconSize = 20.dp,
            )
            if (!likes.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopStart)
                ) {
                    IconButton(
                        onClick = { isLikesExpanded = !isLikesExpanded },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isLikesExpanded) R.drawable.expand_less_24 else R.drawable.expand_more_24
                            ),
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = isLikesExpanded,
                        onDismissRequest = { isLikesExpanded = false }
                    ) {
                        likes.forEach { user ->
                            DropdownMenuItem(
                                text = { Text(text = user.name) },
                                onClick = {},
                                leadingIcon = {
                                    PersonImage(
                                        url = user.avatar?.medium,
                                        modifier = Modifier.size(PERSON_IMAGE_SIZE_VERY_SMALL.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityTextViewPlaceholder() {
    //TODO ThreadCommentViewPlaceholder()
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
                mediaCoverUrl = "",
                createdAt = 12312321,
                replyCount = 999,
                likeCount = 999,
                likes = listOf(exampleActivityUser, exampleActivityUser, exampleActivityUser),
                isLiked = false,
                onClickLike = {},
                onClickUser = {},
                navigateToFullscreenImage = {},
            )
        }
    }
}