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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.exampleMediaList
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.composables.scores.MinimalScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.isGreaterThanZero

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MinimalUserMediaListItem(
    item: UserMediaListQuery.MediaList,
    status: MediaListStatus,
    scoreFormat: ScoreFormat,
    isMyList: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlus: () -> Unit,
    onClickNotes: () -> Unit,
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
            .combinedClickable(onLongClick = onLongClick, onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.media?.basicMediaDetails?.title?.userPreferred ?: "",
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
                    Text(
                        text = "${item.basicMediaListEntry.progress ?: 0}/${item.media?.basicMediaDetails?.duration() ?: 0}",
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    MinimalScoreIndicator(
                        score = item.basicMediaListEntry.score,
                        scoreFormat = scoreFormat
                    )
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
                FilledTonalButton(onClick = onClickPlus) {
                    Text(text = "+1")
                }
            }
        }//:Row
    }//:Card
}

@Preview
@Composable
fun MinimalUserMediaListItemPreview() {
    AniHyouTheme {
        Surface {
            MinimalUserMediaListItem(
                item = exampleMediaList,
                status = MediaListStatus.CURRENT,
                scoreFormat = ScoreFormat.POINT_100,
                isMyList = true,
                onClick = { },
                onLongClick = { },
                onClickPlus = { },
                onClickNotes = {}
            )
        }
    }
}