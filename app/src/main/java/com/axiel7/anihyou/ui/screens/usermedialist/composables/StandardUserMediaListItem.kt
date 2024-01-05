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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.model.media.calculateProgressBarValue
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.exampleMediaList
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_HEIGHT
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaPoster
import com.axiel7.anihyou.ui.composables.scores.BadgeScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format
import com.axiel7.anihyou.utils.NumberUtils.isGreaterThanZero

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StandardUserMediaListItem(
    item: UserMediaListQuery.MediaList,
    status: MediaListStatus,
    scoreFormat: ScoreFormat,
    isMyList: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlus: () -> Unit,
    onClickNotes: () -> Unit,
) {
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
            Box(
                contentAlignment = Alignment.BottomStart
            ) {
                MediaPoster(
                    url = item.media?.coverImage?.large,
                    showShadow = false,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(MEDIA_POSTER_SMALL_WIDTH.dp)
                )

                BadgeScoreIndicator(
                    score = item.basicMediaListEntry.score,
                    scoreFormat = scoreFormat
                )
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
                        val progress = item.basicMediaListEntry.progress?.format() ?: 0
                        val duration = item.media?.basicMediaDetails?.duration()?.format() ?: 0
                        Text(
                            text = "$progress/$duration",
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
                            if (isMyList && (status == MediaListStatus.CURRENT
                                        || status == MediaListStatus.REPEATING)
                            ) {
                                FilledTonalButton(onClick = onClickPlus) {
                                    Text(text = stringResource(R.string.plus_one))
                                }
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
                    item = exampleMediaList,
                    status = MediaListStatus.CURRENT,
                    scoreFormat = ScoreFormat.POINT_100,
                    isMyList = true,
                    onClick = { },
                    onLongClick = { },
                    onClickPlus = { },
                    onClickNotes = {},
                )
                StandardUserMediaListItem(
                    item = exampleMediaList.copy(
                        basicMediaListEntry = exampleMediaList.basicMediaListEntry.copy(
                            score = 3.0
                        )
                    ),
                    status = MediaListStatus.PLANNING,
                    scoreFormat = ScoreFormat.POINT_3,
                    isMyList = true,
                    onClick = { },
                    onLongClick = { },
                    onClickPlus = { },
                    onClickNotes = {},
                )
            }
        }
    }
}