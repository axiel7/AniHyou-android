package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.exampleBasicMediaListEntry
import com.axiel7.anihyou.data.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.media.progressOrVolumes
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.composables.scores.MinimalScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format
import com.axiel7.anihyou.utils.NumberUtils.isGreaterThanZero

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinimalUserMediaListItem(
    item: CommonMediaListEntry,
    listStatus: MediaListStatus?,
    scoreFormat: ScoreFormat,
    isMyList: Boolean,
    isPlusEnabled: Boolean,
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
            .padding(start = 16.dp, top = 8.dp, bottom = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                    modifier = Modifier.padding(end = 4.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    lineHeight = 17.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = if (item.media?.nextAiringEpisode != null) 1 else 2
                )

                AiringScheduleText(
                    item = item,
                    fontSize = 15.sp
                )

                Row(
                    modifier = Modifier.padding(top = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val progress = item.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
                    val duration = item.duration()?.format()
                    Text(
                        text = if (duration != null) "$progress/$duration" else "$progress",
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    if (item.basicMediaListEntry.score?.isGreaterThanZero() == true) {
                        MinimalScoreIndicator(
                            score = item.basicMediaListEntry.score,
                            scoreFormat = scoreFormat
                        )
                    }
                    if (listStatus == null && status != null) {
                        Icon(
                            painter = painterResource(status.icon()),
                            contentDescription = status.localized(),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (item.basicMediaListEntry.repeat.isGreaterThanZero()) {
                        RepeatIndicator(count = item.basicMediaListEntry.repeat ?: 0)
                    }
                    if (!item.basicMediaListEntry.notes.isNullOrBlank()) {
                        NotesIndicator(onClick = onClickNotes)
                    }
                }
            }//:Column

            if (isMyList && (status == MediaListStatus.CURRENT
                        || status == MediaListStatus.REPEATING)
            ) {
                FilledTonalButton(
                    onClick = onClickPlus,
                    enabled = isPlusEnabled,
                ) {
                    Text(text = stringResource(R.string.plus_one))
                }
            }
        }//:Row
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp)
        )
    }//:Column
}

@Preview
@Composable
fun MinimalUserMediaListItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                MinimalUserMediaListItem(
                    item = exampleCommonMediaListEntry,
                    listStatus = MediaListStatus.CURRENT,
                    scoreFormat = ScoreFormat.POINT_100,
                    isMyList = true,
                    isPlusEnabled = true,
                    onClick = { },
                    onLongClick = { },
                    onClickPlus = { },
                    onClickNotes = {}
                )
                MinimalUserMediaListItem(
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
                    onClick = { },
                    onLongClick = { },
                    onClickPlus = { },
                    onClickNotes = {}
                )
            }
        }
    }
}