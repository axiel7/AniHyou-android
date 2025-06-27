package com.axiel7.anihyou.feature.usermedialist.composables

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.common.utils.NumberUtils.isGreaterThanZero
import com.axiel7.anihyou.core.model.media.calculateProgressBarValue
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.exampleBasicMediaListEntry
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.model.media.isActive
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.ui.composables.IncrementOneButton
import com.axiel7.anihyou.core.ui.composables.media.AiringScheduleText
import com.axiel7.anihyou.core.ui.composables.media.ListStatusBadgeIndicator
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_HEIGHT
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.composables.scores.BadgeScoreIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StandardUserMediaListItem(
    item: CommonMediaListEntry,
    listStatus: MediaListStatus?,
    scoreFormat: ScoreFormat,
    isMyList: Boolean,
    isPlusEnabled: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlus: (Int) -> Unit,
    blockPlus: () -> Unit,
    onClickNotes: () -> Unit,
) {
    val status = listStatus ?: item.basicMediaListEntry.status
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 6.dp, horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                MediaPoster(
                    url = item.media?.coverImage?.large,
                    showShadow = false,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(MEDIA_POSTER_SMALL_WIDTH.dp)
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
            }//:Box

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .heightIn(min = MEDIA_POSTER_SMALL_HEIGHT.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                    AiringScheduleText(
                        item = item,
                        fontSize = 16.sp
                    )
                }//:Column
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val progress = item.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
                        val duration = item.duration()?.format()
                        Text(
                            text = if (duration != null) "$progress/$duration" else "$progress",
                            fontSize = 16.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            if (item.basicMediaListEntry.repeat.isGreaterThanZero()) {
                                RepeatIndicator(count = item.basicMediaListEntry.repeat ?: 0)
                            }
                            if (!item.basicMediaListEntry.notes.isNullOrBlank()) {
                                NotesIndicator(
                                    onClick = onClickNotes,
                                    modifier = Modifier.padding(bottom = 2.dp),
                                )
                            }
                            if (isMyList && status?.isActive() == true) {
                                IncrementOneButton(
                                    onClickPlus = onClickPlus,
                                    blockPlus = blockPlus,
                                    enabled = isPlusEnabled,
                                )
                            }
                        }
                    }//:Row
                    LinearProgressIndicator(
                        progress = { item.calculateProgressBarValue() },
                        modifier = Modifier
                            .padding(vertical = 1.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceColorAtElevation(94.dp),
                        strokeCap = StrokeCap.Round,
                        drawStopIndicator = { },
                    )
                }//:Column
            }//:Column
        }//:Row
        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp)
        )
    }//:Column
}

@Preview
@Composable
fun StandardUserMediaListItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                StandardUserMediaListItem(
                    item = exampleCommonMediaListEntry,
                    listStatus = MediaListStatus.CURRENT,
                    scoreFormat = ScoreFormat.POINT_100,
                    isMyList = true,
                    isPlusEnabled = true,
                    onClick = {},
                    onLongClick = {},
                    onClickPlus = {},
                    blockPlus = {},
                    onClickNotes = {},
                )
                StandardUserMediaListItem(
                    item = exampleCommonMediaListEntry.copy(
                        basicMediaListEntry = exampleBasicMediaListEntry.copy(
                            score = 3.0,
                            status = MediaListStatus.COMPLETED
                        )
                    ),
                    listStatus = null,
                    scoreFormat = ScoreFormat.POINT_3,
                    isMyList = true,
                    isPlusEnabled = true,
                    onClick = {},
                    onLongClick = {},
                    onClickPlus = {},
                    blockPlus = {},
                    onClickNotes = {},
                )
            }
        }
    }
}