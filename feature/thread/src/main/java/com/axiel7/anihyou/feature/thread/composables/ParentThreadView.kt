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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampToDateString
import com.axiel7.anihyou.core.network.fragment.BasicThreadDetails
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.common.ReplyButton
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.core.ui.composables.person.PersonItemSmall
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun ParentThreadView(
    thread: BasicThreadDetails,
    isLiked: Boolean,
    onClickLike: () -> Unit,
    onClickReply: () -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = thread.title.orEmpty(),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp
        )
        Text(
            text = thread.createdAt.toLong().timestampToDateString(format = "MMM d, YYYY").orEmpty(),
            color = MaterialTheme.colorScheme.outline,
            fontSize = 15.sp
        )

        DefaultMarkdownText(
            markdown = thread.body,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 17.sp,
            navigateToFullscreenImage = navigateToFullscreenImage,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonItemSmall(
                avatarUrl = thread.user?.avatar?.medium,
                username = thread.user?.name,
                modifier = Modifier.weight(1f),
                isLocked = thread.isLocked,
                onClick = {
                    thread.user?.id?.let(navigateToUserDetails)
                }
            )
            FavoriteIconButton(
                isFavorite = isLiked,
                favoritesCount = thread.likeCount,
                onClick = onClickLike
            )
            if (thread.isLocked == false) {
                ReplyButton(
                    onClick = onClickReply
                )
            }
        }
    }
}

@Composable
fun ParentThreadViewPlaceholder() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "This is a loading placeholder",
            modifier = Modifier
                .padding(bottom = 4.dp)
                .defaultPlaceholder(visible = true),
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp
        )
        Text(
            text = "Jan 1, 2010",
            modifier = Modifier.defaultPlaceholder(visible = true),
            color = MaterialTheme.colorScheme.outline,
            fontSize = 15.sp
        )

        Text(
            text = "This is a loading placeholder of a thread view, the content is loading so please wait until it finished loading. Thank you.",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .defaultPlaceholder(visible = true),
            fontSize = 20.sp,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextIconHorizontal(
                text = "17",
                modifier = Modifier.defaultPlaceholder(visible = true),
                icon = R.drawable.favorite_20
            )
            Text(
                text = "Username",
                modifier = Modifier.defaultPlaceholder(visible = true),
            )
        }
    }
}

@Preview
@Composable
fun ParentThreadViewPreview() {
    val thread = BasicThreadDetails(
        id = 1,
        title = "[Spoilers] Oshi no Ko - Episode 8 Discussion",
        body = "Great episode as expected. Reality Dating arc near to end and Akane was full on fire dem full of confidence and new personality. That kissing scene was soo good. But for sec i feel bad for Arima. Also finally we have 3rd member of b-komachi group i love to see new b-komachi on stage very hyped for that.",
        viewCount = 102,
        totalReplies = 12,
        likeCount = 17,
        isLiked = false,
        isSubscribed = false,
        isLocked = false,
        user = BasicThreadDetails.User(
            id = 1,
            name = "KOMBRAT",
            avatar = BasicThreadDetails.Avatar(
                medium = null
            ),
            __typename = "User"
        ),
        createdAt = 1293823000,
        __typename = ""
    )
    AniHyouTheme {
        Surface {
            Column {
                ParentThreadView(
                    thread = thread,
                    isLiked = true,
                    onClickLike = {},
                    onClickReply = {},
                    navigateToUserDetails = {},
                    navigateToFullscreenImage = {},
                )
                ParentThreadViewPlaceholder()
            }
        }
    }
}