package com.axiel7.anihyou.ui.composables.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaItemHorizontal(
    title: String,
    imageUrl: String?,
    subtitle1: @Composable (ColumnScope.() -> Unit)? = null,
    subtitle2: @Composable (ColumnScope.() -> Unit)? = null,
    badgeContent: @Composable (RowScope.() -> Unit)? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(MEDIA_POSTER_SMALL_HEIGHT.dp)
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = MEDIA_POSTER_SMALL_WIDTH.dp,
                    height = MEDIA_POSTER_SMALL_HEIGHT.dp
                ),
            contentAlignment = Alignment.BottomStart
        ) {
            MediaPoster(
                url = imageUrl,
                showShadow = false,
                modifier = Modifier
                    .size(
                        width = MEDIA_POSTER_SMALL_WIDTH.dp,
                        height = MEDIA_POSTER_SMALL_HEIGHT.dp
                    )
            )
            if (badgeContent != null) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topEnd = 16.dp, bottomStart = 8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = badgeContent
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 17.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            if (subtitle1 != null) subtitle1()
            if (subtitle2 != null) subtitle2()
        }//: Column
    }//: Row
}

@Composable
fun MediaItemHorizontal(
    title: String,
    imageUrl: String?,
    score: Int,
    format: MediaFormat,
    year: Int?,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    badgeContent: @Composable (RowScope.() -> Unit)? = null,
) {
    MediaItemHorizontal(
        title = title,
        imageUrl = imageUrl,
        subtitle1 = {
            Text(
                text = buildString {
                    append(format.localized())
                    if (year != null) append(" · $year")
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        subtitle2 = {
            SmallScoreIndicator(
                score = "$score%",
                fontSize = 15.sp
            )
        },
        badgeContent = badgeContent,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

@Composable
fun MediaItemHorizontalPlaceholder() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(MEDIA_POSTER_SMALL_HEIGHT.dp)
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = MEDIA_POSTER_SMALL_WIDTH.dp,
                    height = MEDIA_POSTER_SMALL_HEIGHT.dp
                )
                .clip(RoundedCornerShape(8.dp))
                .defaultPlaceholder(visible = true)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "This is a placeholder text",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 17.sp,
            )

            Text(
                text = "Placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
            )
            Text(
                text = "Placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
            )
        }//: Column
    }//: Row
}

@Preview
@Composable
fun MediaItemHorizontalPreview() {
    AniHyouTheme {
        Surface {
            Column {
                MediaItemHorizontal(
                    title = "This is a very large anime title that should serve as a preview example",
                    imageUrl = null,
                    badgeContent = { Text(text = "#1") },
                    score = 76,
                    format = MediaFormat.TV,
                    year = 2014,
                    onClick = {}
                )
                MediaItemHorizontalPlaceholder()
            }
        }
    }
}