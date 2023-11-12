package com.axiel7.anihyou.ui.composables.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val MEDIA_ITEM_VERTICAL_HEIGHT = 200

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaItemVertical(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    subtitle: @Composable (() -> Unit)? = null,
    badgeContent: @Composable (RowScope.() -> Unit)? = null,
    minLines: Int = 1,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .width(MEDIA_POSTER_SMALL_WIDTH.dp)
            .sizeIn(
                minHeight = MEDIA_ITEM_VERTICAL_HEIGHT.dp
            )
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        horizontalAlignment = Alignment.Start
    ) {
        val posterSizeModifier = Modifier
            .size(
                width = MEDIA_POSTER_SMALL_WIDTH.dp,
                height = MEDIA_POSTER_SMALL_HEIGHT.dp
            )
        Box(
            modifier = posterSizeModifier
        ) {
            MediaPoster(
                url = imageUrl,
                showShadow = false,
                modifier = posterSizeModifier
            )
            if (badgeContent != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(topEnd = 16.dp, bottomStart = 8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = badgeContent
                )
            }
        }

        Text(
            text = title,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 2.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            lineHeight = 18.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            minLines = minLines
        )

        if (subtitle != null) {
            subtitle()
        }
    }
}

@Composable
fun MediaItemVerticalPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .size(
                width = (MEDIA_POSTER_SMALL_WIDTH + 8).dp,
                height = MEDIA_ITEM_VERTICAL_HEIGHT.dp
            )
            .padding(end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = MEDIA_POSTER_SMALL_WIDTH.dp,
                    height = MEDIA_POSTER_SMALL_HEIGHT.dp
                )
                .background(
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Text(
            text = "This is a placeholder",
            modifier = Modifier
                .padding(top = 8.dp)
                .defaultPlaceholder(visible = true),
            fontSize = 15.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
}

@Preview
@Composable
fun MediaItemVerticalPreview() {
    AniHyouTheme {
        Surface {
            MediaItemVertical(
                title = "This is a very large anime title that should serve as a preview example",
                imageUrl = null,
                subtitle = {
                    SmallScoreIndicator(
                        score = "83%",
                        fontSize = 13.sp
                    )
                },
                badgeContent = {
                    Icon(
                        painter = painterResource(R.drawable.check_circle_24),
                        contentDescription = ""
                    )
                },
                onClick = {}
            )
        }
    }
}