package com.axiel7.anihyou.core.ui.composables.media

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
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.model.media.icon
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.model.stats.overview.StatusDistribution.Companion.asStat
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

const val MEDIA_ITEM_VERTICAL_HEIGHT = 200

@Composable
fun MediaItemVertical(
    title: String,
    imageUrl: String?,
    subtitle: @Composable (() -> Unit)? = null,
    status: MediaListStatus?,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    val statusStat = remember(status) { status?.asStat() }
    MediaItemVertical(
        title = title,
        imageUrl = imageUrl,
        modifier = modifier,
        subtitle = subtitle,
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
        minLines = minLines,
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaItemVertical(
    title: String,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    subtitle: @Composable (() -> Unit)? = null,
    badgeContent: @Composable (RowScope.() -> Unit)? = null,
    badgeBackgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
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
                        .background(badgeBackgroundColor)
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
                    SmallScoreIndicator(score = 83)
                },
                status = MediaListStatus.COMPLETED,
                onClick = {}
            )
        }
    }
}