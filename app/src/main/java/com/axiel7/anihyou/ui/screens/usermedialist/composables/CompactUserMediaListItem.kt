package com.axiel7.anihyou.ui.screens.usermedialist.composables

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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.exampleBasicMediaListEntry
import com.axiel7.anihyou.data.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.data.model.media.progressOrVolumes
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.composables.media.ListStatusBadgeIndicator
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaPoster
import com.axiel7.anihyou.ui.composables.scores.BadgeScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format
import com.axiel7.anihyou.utils.NumberUtils.isGreaterThanZero

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompactUserMediaListItem(
    item: CommonMediaListEntry,
    listStatus: MediaListStatus?,
    scoreFormat: ScoreFormat,
    isMyList: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlus: () -> Unit,
    onClickNotes: () -> Unit,
) {
    val status = listStatus ?: item.basicMediaListEntry.status
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onLongClick = onLongClick, onClick = onClick)
            .padding(start = 16.dp, end = 0.dp, top = 4.dp, bottom = 8.dp),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            Box {
                MediaPoster(
                    url = item.media?.coverImage?.large,
                    showShadow = false,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .heightIn(min = MEDIA_POSTER_SMALL_WIDTH.dp)
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
            }//: Box

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(),
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
                    fontSize = 16.sp,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val progress = item.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
                    val duration = item.duration()?.format()
                    Text(
                        text = if (duration != null) "$progress/$duration" else "$progress",
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
                        if (isMyList && (status == MediaListStatus.CURRENT
                                    || status == MediaListStatus.REPEATING)
                        ) {
                            FilledTonalButton(onClick = onClickPlus) {
                                Text(text = stringResource(R.string.plus_one))
                            }
                        }
                    }
                }//:Row
            }//:Column
        }//:Row
        HorizontalDivider(
            modifier = Modifier.padding(top = 12.dp)
        )
    }//:Column
}

@Preview
@Composable
fun CompactUserMediaListItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                CompactUserMediaListItem(
                    item = exampleCommonMediaListEntry,
                    listStatus = MediaListStatus.CURRENT,
                    scoreFormat = ScoreFormat.POINT_100,
                    isMyList = true,
                    onClick = { },
                    onLongClick = { },
                    onClickPlus = { },
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
                    onClick = { },
                    onLongClick = { },
                    onClickPlus = { },
                    onClickNotes = {}
                )
            }
        }
    }
}