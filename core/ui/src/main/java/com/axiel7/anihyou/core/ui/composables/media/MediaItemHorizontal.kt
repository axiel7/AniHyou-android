package com.axiel7.anihyou.core.ui.composables.media

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
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.model.media.icon
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.model.stats.overview.StatusDistribution.Companion.asStat
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun MediaItemHorizontal(
    title: String,
    imageUrl: String?,
    subtitle1: @Composable (ColumnScope.() -> Unit)? = null,
    subtitle2: @Composable (ColumnScope.() -> Unit)? = null,
    status: MediaListStatus? = null,
    topBadgeContent: @Composable (RowScope.() -> Unit)? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    val statusStat = remember(status) { status?.asStat() }
    MediaItemHorizontal(
        title = title,
        imageUrl = imageUrl,
        subtitle1 = subtitle1,
        subtitle2 = subtitle2,
        badgeContent = status?.let {
            {
                Icon(
                    painter = painterResource(status.icon()),
                    contentDescription = status.localized(),
                    tint = statusStat?.onPrimaryColor() ?: LocalContentColor.current
                )
            }
        },
        badgeBackgroundColor = statusStat?.primaryColor()
            ?: MaterialTheme.colorScheme.secondaryContainer,
        topBadgeContent = topBadgeContent,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaItemHorizontal(
    title: String,
    imageUrl: String?,
    subtitle1: @Composable (ColumnScope.() -> Unit)? = null,
    subtitle2: @Composable (ColumnScope.() -> Unit)? = null,
    badgeContent: @Composable (RowScope.() -> Unit)? = null,
    badgeBackgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    topBadgeContent: @Composable (RowScope.() -> Unit)? = null,
    topBadgeBackgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
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
                        .background(badgeBackgroundColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = badgeContent
                )
            }
            if (topBadgeContent != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomEnd = 16.dp))
                        .background(topBadgeBackgroundColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    content = topBadgeContent
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
    status: MediaListStatus? = null,
    topBadgeContent: @Composable (RowScope.() -> Unit)? = null,
) {
    val statusStat = remember(status) { status?.asStat() }
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
                score = score,
                fontSize = 15.sp
            )
        },
        badgeContent = status?.let {
            {
                Icon(
                    painter = painterResource(status.icon()),
                    contentDescription = status.localized(),
                    tint = statusStat?.onPrimaryColor() ?: LocalContentColor.current
                )
            }
        },
        badgeBackgroundColor = statusStat?.primaryColor()
            ?: MaterialTheme.colorScheme.secondaryContainer,
        topBadgeContent = topBadgeContent,
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
                    status = MediaListStatus.COMPLETED,
                    topBadgeContent = {
                        Text(text = "#1")
                    },
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