package com.axiel7.anihyou.feature.usermedialist.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.axiel7.anihyou.core.model.media.exampleBasicMediaListEntry
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.model.media.isActive
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.composables.IncrementOneButton
import com.axiel7.anihyou.core.ui.composables.media.AiringScheduleText
import com.axiel7.anihyou.core.ui.composables.media.AllPriorityColors
import com.axiel7.anihyou.core.ui.composables.media.ListStatusBadgeIndicator
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_COMPACT_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.composables.media.MediaProgressIndicator
import com.axiel7.anihyou.core.ui.composables.media.PriorityIndicator
import com.axiel7.anihyou.core.ui.composables.scores.BadgeScoreIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompactUserMediaListItem(
    item: CommonMediaListEntry,
    listStatus: MediaListStatus?,
    scoreFormat: ScoreFormat,
    isMyList: Boolean,
    isPlusEnabled: Boolean,
    showLowPriority: Boolean,
    allPriorityColors: AllPriorityColors,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlus: (Int) -> Unit,
    blockPlus: () -> Unit,
    onClickNotes: () -> Unit,
) {
    val blurAdult = LocalBlurAdult.current
    val status = listStatus ?: item.basicMediaListEntry.status
    val priority = item.basicMediaListEntry.priority
    val singleEpisode =
        item.media?.basicMediaDetails?.type == MediaType.ANIME && (item.media?.basicMediaDetails?.episodes == 1)
    Surface(
        shape = MaterialTheme.shapes.large,
        color = Color.Transparent,
        modifier = Modifier
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
            Box(
                contentAlignment = Alignment.Center,
            ) {
                MediaPoster(
                    url = item.media?.coverImage?.large,
                    enableBlur = blurAdult && item.media?.basicMediaDetails?.isAdult == true,
                    showShadow = false,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.size(
                        width = MEDIA_POSTER_COMPACT_WIDTH.dp,
                        height = (MEDIA_POSTER_COMPACT_WIDTH + 8).dp
                    )
                )

                if (listStatus == null && status != null) {
                    ListStatusBadgeIndicator(
                        alignment = Alignment.TopStart,
                        status = status
                    )
                }

                if (item.basicMediaListEntry.score?.isGreaterThanZero() == true) {
                    BadgeScoreIndicator(
                        modifier = Modifier.align(Alignment.BottomStart),
                        score = item.basicMediaListEntry.score,
                        scoreFormat = scoreFormat
                    )
                }

                if (priority != null && (priority > 0 || showLowPriority)) {
                    PriorityIndicator(
                        modifier = Modifier.align(Alignment.TopEnd),
                        priority = priority,
                        allPriorityColors = allPriorityColors,
                    )
                }
            }//: Box

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .height((MEDIA_POSTER_COMPACT_WIDTH + 8).dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = if (item.media?.nextAiringEpisode != null) 1 else 2
                )

                AiringScheduleText(
                    item = item,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {

                    MediaProgressIndicator(
                        item = item,
                        singleEpisode = singleEpisode,
                        fontSize = 15.sp,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (item.basicMediaListEntry.repeat.isGreaterThanZero()) {
                            RepeatIndicator(
                                count = item.basicMediaListEntry.repeat ?: 0,
                            )
                        }
                        if (!item.basicMediaListEntry.notes.isNullOrBlank()) {
                            NotesIndicator(
                                modifier = Modifier.padding(bottom = 2.dp),
                                onClick = onClickNotes
                            )
                        }
                        if (isMyList && status?.isActive() == true) {
                            IncrementOneButton(
                                onClickPlus = onClickPlus,
                                blockPlus = blockPlus,
                                enabled = isPlusEnabled,
                                singleEpisode = singleEpisode
                            )
                        }
                    }
                }//:Row
            }//:Column
        }
    }
}

@Preview
@Composable
private fun CompactUserMediaListItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                CompactUserMediaListItem(
                    item = exampleCommonMediaListEntry,
                    listStatus = MediaListStatus.CURRENT,
                    scoreFormat = ScoreFormat.POINT_100,
                    isMyList = true,
                    isPlusEnabled = true,
                    showLowPriority = true,
                    allPriorityColors = AllPriorityColors.Default,
                    onClick = {},
                    onLongClick = {},
                    onClickPlus = {},
                    blockPlus = {},
                    onClickNotes = {}
                )
                CompactUserMediaListItem(
                    item = exampleCommonMediaListEntry.copy(
                        basicMediaListEntry = exampleBasicMediaListEntry.copy(
                            score = 3.0,
                            status = MediaListStatus.PLANNING
                        )
                    ),
                    listStatus = null,
                    scoreFormat = ScoreFormat.POINT_3,
                    isMyList = true,
                    isPlusEnabled = true,
                    showLowPriority = false,
                    allPriorityColors = AllPriorityColors.Default,
                    onClick = {},
                    onLongClick = {},
                    onClickPlus = {},
                    blockPlus = {},
                    onClickNotes = {}
                )
            }
        }
    }
}