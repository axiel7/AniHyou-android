package com.axiel7.anihyou.feature.calendar.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.axiel7.anihyou.core.model.media.icon
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.model.stats.overview.StatusDistribution.Companion.asStat
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_COMPACT_HEIGHT
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_COMPACT_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.materialkolor.ktx.harmonize

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarAiringHorizontalItem(
    title: String,
    subtitle: String,
    blurImage: Boolean = false,
    imageUrl: String?,
    score: Int? = null,
    status: MediaListStatus? = null,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val posterSizeModifier = Modifier
                .size(
                    width = MEDIA_POSTER_COMPACT_WIDTH.dp,
                    height = MEDIA_POSTER_COMPACT_HEIGHT.dp
                )
            Box(
                modifier = posterSizeModifier
            ) {
                MediaPoster(
                    url = imageUrl,
                    enableBlur = blurImage,
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
                                    ?.harmonize(MaterialTheme.colorScheme.primary)
                                    ?: MaterialTheme.colorScheme.secondaryContainer
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(status.icon()),
                            contentDescription = status.localized(),
                            modifier = Modifier.size(20.dp),
                            tint = statusStat?.onPrimaryColor()
                                ?.harmonize(MaterialTheme.colorScheme.primary)
                                ?: LocalContentColor.current
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelLarge,
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
}

@Composable
fun CalendarAiringHorizontalItemPlaceholder() {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .size(width = MEDIA_POSTER_COMPACT_WIDTH.dp, height = MEDIA_POSTER_COMPACT_HEIGHT.dp)
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

            SmallScoreIndicator(
                score = 0,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AiringAnimeHorizontalItemPreview() {
    AniHyouTheme {
        Surface {
            CalendarAiringHorizontalItem(
                title = "Kimetsu no Yaiba: Katanakaji no Sato-hen",
                subtitle = "Airing in 12 min",
                imageUrl = null,
                score = 79,
                status = MediaListStatus.COMPLETED,
                onClick = { },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AiringAnimeHorizontalItemPlaceholderPreview() {
    AniHyouTheme {
        Surface {
            CalendarAiringHorizontalItemPlaceholder()
        }
    }
}