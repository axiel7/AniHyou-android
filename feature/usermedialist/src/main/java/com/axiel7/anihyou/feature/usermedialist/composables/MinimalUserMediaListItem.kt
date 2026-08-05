package com.axiel7.anihyou.feature.usermedialist.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.common.utils.NumberUtils.isGreaterThanZero
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.exampleBasicMediaListEntry
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.model.media.icon
import com.axiel7.anihyou.core.model.media.isUsingVolumeProgress
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.IncrementOneButton
import com.axiel7.anihyou.core.ui.composables.media.AiringScheduleText
import com.axiel7.anihyou.core.ui.composables.scores.MinimalScoreIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MinimalUserMediaListItem(
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
    ListItem(
        onClick = onClick,
        onLongClick = onLongClick,
        trailingContent = if (isMyList
            && (status == MediaListStatus.CURRENT || status == MediaListStatus.REPEATING)
        ) {
            {
                IncrementOneButton(
                    onClickPlus = onClickPlus,
                    blockPlus = blockPlus,
                    enabled = isPlusEnabled,
                )
            }
        } else null,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
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
            )

            Row(
                modifier = Modifier.padding(top = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val progress = item.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
                val duration = item.duration()?.format()
                Text(
                    text = if (duration != null) "$progress/$duration" else "$progress",
                    fontSize = 15.sp,
                    maxLines = 1
                )
                if (item.basicMediaListEntry.isUsingVolumeProgress()) {
                    Icon(
                        painter = painterResource(R.drawable.bookmark_20),
                        contentDescription = stringResource(R.string.volumes),
                    )
                }
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
        }
    }
}

@Preview
@Composable
private fun MinimalUserMediaListItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                MinimalUserMediaListItem(
                    item = exampleCommonMediaListEntry,
                    listStatus = MediaListStatus.CURRENT,
                    scoreFormat = ScoreFormat.POINT_100,
                    isMyList = true,
                    isPlusEnabled = true,
                    onClick = {},
                    onLongClick = {},
                    onClickPlus = {},
                    blockPlus = {},
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