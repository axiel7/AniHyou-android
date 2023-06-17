package com.axiel7.anihyou.ui.composables.thread

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.fragment.BasicThreadDetails
import com.axiel7.anihyou.ui.composables.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.SpoilerDialog
import com.axiel7.anihyou.ui.composables.TextIconHorizontal
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_VERY_SMALL
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ContextUtils.openActionView
import com.axiel7.anihyou.utils.DateUtils.timestampToDateString

@Composable
fun ParentThreadView(
    thread: BasicThreadDetails,
    navigateToUserDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    val context = LocalContext.current
    var spoilerText by remember { mutableStateOf<String?>(null) }

    spoilerText?.let {
        SpoilerDialog(
            text = it,
            onDismiss = {
                spoilerText = null
            }
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = thread.title ?: "",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp
        )
        Text(
            text = thread.createdAt.toLong().timestampToDateString(format = "MMM d, YYYY") ?: "",
            color = MaterialTheme.colorScheme.outline,
            fontSize = 15.sp
        )

        DefaultMarkdownText(
            markdown = thread.body,
            modifier = Modifier.padding(vertical = 8.dp),
            fontSize = 17.sp,
            onImageClicked = navigateToFullscreenImage,
            onSpoilerClicked = { spoilerText = it },
            onLinkClicked = { context.openActionView(it) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable {
                    navigateToUserDetails(thread.user!!.id)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                PersonImage(
                    url = thread.user?.avatar?.medium,
                    modifier = Modifier
                        .size(PERSON_IMAGE_SIZE_VERY_SMALL.dp)
                )
                Text(
                    text = thread.user?.name ?: "",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            FavoriteIconButton(
                isFavorite = false,
                favoritesCount = thread.likeCount,
                onClick = { /*TODO*/ }
            )
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
        replyCount = 12,
        likeCount = 17,
        user = BasicThreadDetails.User(
            id = 1,
            name = "KOMBRAT",
            avatar = BasicThreadDetails.Avatar(
                medium = null
            ),
            __typename = "User"
        ),
        createdAt = 1293823000
    )
    AniHyouTheme {
        Surface {
            Column {
                ParentThreadView(
                    thread = thread,
                    navigateToUserDetails = {},
                    navigateToFullscreenImage = {}
                )
                ParentThreadViewPlaceholder()
            }
        }
    }
}