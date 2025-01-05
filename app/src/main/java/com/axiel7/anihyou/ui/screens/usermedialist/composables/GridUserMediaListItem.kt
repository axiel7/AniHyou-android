package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.data.model.media.progressOrVolumes
import com.axiel7.anihyou.fragment.CommonMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.composables.media.ListStatusBadgeIndicator
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_MEDIUM_HEIGHT
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_MEDIUM_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaPoster
import com.axiel7.anihyou.ui.composables.scores.BadgeScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format
import com.axiel7.anihyou.utils.NumberUtils.isGreaterThanZero
import com.axiel7.anihyou.utils.UNKNOWN_CHAR

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridUserMediaListItem(
    item: CommonMediaListEntry,
    listStatus: MediaListStatus?,
    scoreFormat: ScoreFormat,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val status = listStatus ?: item.basicMediaListEntry.status
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onLongClick = onLongClick, onClick = onClick),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                MediaPoster(
                    url = item.media?.coverImage?.large,
                    showShadow = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MEDIA_POSTER_MEDIUM_HEIGHT.dp)
                )

                if (item.basicMediaListEntry.score?.isGreaterThanZero() == true) {
                    BadgeScoreIndicator(
                        modifier = Modifier.align(Alignment.BottomStart),
                        score = item.basicMediaListEntry.score,
                        scoreFormat = scoreFormat
                    )
                }

                if (listStatus == null && status != null) {
                    ListStatusBadgeIndicator(
                        alignment = Alignment.BottomEnd,
                        status = status
                    )
                }

                if (item.media?.nextAiringEpisode != null) {
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        AiringScheduleText(
                            item = item,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }//:Box

            Text(
                text = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                minLines = 2,
            )

            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val progress = item.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
                val duration = item.duration()?.format() ?: UNKNOWN_CHAR
                Text(
                    text = "$progress/$duration",
                    fontSize = 15.sp,
                    maxLines = 1
                )
            }
        }//:Column
    }//:Card
}

@Preview
@Composable
fun GridUserMediaListItemPreview() {
    AniHyouTheme {
        Surface {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_MEDIUM_WIDTH + 8).dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                items(3) {
                    GridUserMediaListItem(
                        item = if (it == 1) exampleCommonMediaListEntry.copy(
                            media = exampleCommonMediaListEntry.media?.copy(nextAiringEpisode = null)
                        ) else exampleCommonMediaListEntry,
                        listStatus = null,
                        scoreFormat = ScoreFormat.POINT_100,
                        onClick = { },
                        onLongClick = { }
                    )
                }
            }
        }
    }
}