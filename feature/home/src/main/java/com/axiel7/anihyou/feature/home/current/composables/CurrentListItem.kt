package com.axiel7.anihyou.feature.home.current.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.NumberUtils.isGreaterThanZero
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.common.LocalScoreFormat
import com.axiel7.anihyou.core.ui.composables.IncrementOneButton
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.media.AiringScheduleText
import com.axiel7.anihyou.core.ui.composables.media.AllPriorityColors
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_COMPACT_HEIGHT
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_COMPACT_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.composables.media.MediaProgressIndicator
import com.axiel7.anihyou.core.ui.composables.media.PriorityColors.Companion.toPriorityColors
import com.axiel7.anihyou.core.ui.composables.media.PriorityIndicator
import com.axiel7.anihyou.core.ui.composables.scores.BadgeScoreIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrentListItem(
    modifier: Modifier = Modifier,
    item: CommonMediaListEntry,
    isPlusEnabled: Boolean,
    showLowPriority: Boolean,
    allPriorityColors: AllPriorityColors,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlus: (Int) -> Unit,
    blockPlus: () -> Unit,
) {
    val scoreFormat = LocalScoreFormat.current
    val blurAdult = LocalBlurAdult.current
    val singleEpisode =
        item.media?.basicMediaDetails?.type == MediaType.ANIME && (item.media?.basicMediaDetails?.episodes == 1)
    Surface(
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                MediaPoster(
                    url = item.media?.coverImage?.large,
                    enableBlur = blurAdult && item.media?.basicMediaDetails?.isAdult == true,
                    showShadow = false,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(
                            width = MEDIA_POSTER_COMPACT_WIDTH.dp,
                            height = MEDIA_POSTER_COMPACT_HEIGHT.dp
                        )
                )

                if (item.basicMediaListEntry.score?.isGreaterThanZero() == true) {
                    BadgeScoreIndicator(
                        modifier = Modifier.align(Alignment.BottomStart),
                        score = item.basicMediaListEntry.score,
                        scoreFormat = scoreFormat
                    )
                }

                if (item.basicMediaListEntry.priority != null && (item.basicMediaListEntry.priority!! > 0 || showLowPriority)) {
                    PriorityIndicator(
                        modifier = Modifier.align(Alignment.TopEnd),
                        priority = item.basicMediaListEntry.priority!!,
                        allPriorityColors = allPriorityColors,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 0.dp)
                    .height(IntrinsicSize.Min),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2,
                    maxLines = 2
                )
                
                AiringScheduleText(
                    item = item,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    MediaProgressIndicator(
                        item = item,
                        singleEpisode = singleEpisode
                    )
                    IncrementOneButton(
                        onClickPlus = onClickPlus,
                        blockPlus = blockPlus,
                        enabled = isPlusEnabled,
                        singleEpisode = singleEpisode
                    )
                }//:Row
            }//:Column
        }
    }
}

@Composable
fun CurrentListItemPlaceholder(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .size(
                width = 350.dp,
                height = (MEDIA_POSTER_COMPACT_HEIGHT + 16).dp
            )
            .padding(start = 16.dp, end = 0.dp, top = 8.dp, bottom = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = MEDIA_POSTER_COMPACT_WIDTH.dp,
                    height = MEDIA_POSTER_COMPACT_HEIGHT.dp
                )
                .defaultPlaceholder(visible = true)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "This is a placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 16.sp,
            )
            Text(
                text = "Loading item",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 15.sp,
            )

            Text(
                text = "Loading",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 15.sp,
            )
        }//:Column
    }//:Row
}

@Preview
@Composable
private fun CurrentListItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                val priorityNoneColor = MaterialTheme.colorScheme.secondaryContainer

                val lowPriorityColors = remember(false) {
                    priorityNoneColor.toPriorityColors(false)
                }
                val mediumPriorityColors = remember(false) {
                    Color.Yellow.toPriorityColors(false)
                }
                val highPriorityColors = remember(false) {
                    Color.Red.toPriorityColors(false)
                }

                val allPriorityColors = remember(lowPriorityColors, mediumPriorityColors, highPriorityColors) {
                    AllPriorityColors(
                        low = lowPriorityColors,
                        medium = mediumPriorityColors,
                        high = highPriorityColors,
                    )
                }

                CurrentListItem(
                    item = exampleCommonMediaListEntry,
                    isPlusEnabled = true,
                    showLowPriority = true,
                    allPriorityColors = allPriorityColors,
                    onClick = {},
                    onLongClick = {},
                    onClickPlus = {},
                    blockPlus = {},
                )
                CurrentListItemPlaceholder()
            }
        }
    }
}