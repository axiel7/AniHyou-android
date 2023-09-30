package com.axiel7.anihyou.ui.screens.home.activity.composables

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.ui.composables.CommentIconButton
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.media.MediaPoster
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_VERY_SMALL
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.DateUtils.timestampIntervalSinceNow
import java.time.temporal.ChronoUnit

@Composable
fun ActivityFeedItem(
    modifier: Modifier = Modifier,
    username: String?,
    avatarUrl: String?,
    createdAt: Int,
    text: String,
    replyCount: Int,
    likeCount: Int,
    isLiked: Boolean?,
    mediaCoverUrl: String? = null,
    onClick: () -> Unit,
    onClickUser: () -> Unit,
    onClickLike: () -> Unit,
    onClickMedia: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
    ) {
        if (mediaCoverUrl != null) {
            MediaPoster(
                url = mediaCoverUrl,
                modifier = Modifier
                    .size(
                        width = 48.dp,
                        height = 74.dp
                    )
                    .clickable(onClick = onClickMedia),
                showShadow = false
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
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
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }//:Row
                Text(
                    text = createdAt.toLong().timestampIntervalSinceNow()
                        .secondsToLegibleText(maxUnit = ChronoUnit.WEEKS),
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 14.sp
                )
            }//:Row
            Text(
                text = text,
                modifier = Modifier.padding(bottom = 4.dp),
                lineHeight = 20.sp
            )
            Row(
                modifier = Modifier.align(Alignment.End),
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
                username = "axiel7",
                avatarUrl = null,
                createdAt = 12312321,
                text = "I just watched the latest season of Kanojo, Okarishimasu and I want to kms",
                replyCount = 999,
                likeCount = 999,
                isLiked = false,
                mediaCoverUrl = "",
                onClick = {},
                onClickUser = {},
                onClickLike = {},
                onClickMedia = {},
            )
        }
    }
}