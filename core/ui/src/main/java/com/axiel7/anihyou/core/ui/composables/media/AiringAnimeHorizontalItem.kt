package com.axiel7.anihyou.core.ui.composables.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AiringAnimeHorizontalItem(
    title: String,
    subtitle: String,
    imageUrl: String?,
    score: Int? = null,
    status: MediaListStatus? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .sizeIn(maxWidth = 300.dp, minWidth = 250.dp)
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
            if (status != null) {
                val statusStat = remember(status) { status.asStat() }
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(topEnd = 16.dp, bottomStart = 8.dp))
                        .background(
                            color = statusStat?.primaryColor()
                                ?: MaterialTheme.colorScheme.secondaryContainer
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(status.icon()),
                        contentDescription = status.localized(),
                        tint = statusStat?.onPrimaryColor() ?: LocalContentColor.current
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (score != null) {
                SmallScoreIndicator(
                    score = score
                )
            }
        }
    }
}

@Composable
fun AiringAnimeHorizontalItemPlaceholder() {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .sizeIn(maxWidth = 300.dp, minWidth = 250.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .size(width = MEDIA_POSTER_SMALL_WIDTH.dp, height = MEDIA_POSTER_SMALL_HEIGHT.dp)
                .background(
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = "This is a placeholder",
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .defaultPlaceholder(visible = true)
            )
            Text(
                text = "This content is loading",
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .defaultPlaceholder(visible = true)
            )

            Text(
                text = "Loading",
                modifier = Modifier.defaultPlaceholder(visible = true)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AiringAnimeHorizontalItemPreview() {
    AniHyouTheme {
        Surface {
            AiringAnimeHorizontalItem(
                title = "Kimetsu no Yaiba: Katanakaji no Sato-hen",
                subtitle = "Airing in 12 min",
                imageUrl = null,
                score = 79,
                status = MediaListStatus.COMPLETED,
                onClick = { }
            )
        }
    }
}