package com.axiel7.anihyou.ui.composables.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import com.axiel7.anihyou.data.model.calculateProgressBarValue
import com.axiel7.anihyou.data.model.duration
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StandardUserMediaListItem(
    item: UserMediaListQuery.MediaList,
    status: MediaListStatus,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlus: () -> Unit,
) {
    OutlinedCard(
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Row(
            modifier = Modifier.height(MEDIA_POSTER_SMALL_HEIGHT.dp)
        ) {
            Box(
                contentAlignment = Alignment.BottomStart
            ) {
                MediaPoster(
                    url = item.media?.coverImage?.large,
                    showShadow = false,
                    modifier = Modifier
                        .size(
                            width = MEDIA_POSTER_SMALL_WIDTH.dp,
                            height = MEDIA_POSTER_SMALL_HEIGHT.dp
                        )
                )
            }//:Box

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.media?.basicMediaDetails?.title?.userPreferred ?: "",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                    AiringScheduleText(item = item)
                }//:Column

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${item.basicMediaListEntry.progress ?: 0}/${item.media?.basicMediaDetails?.duration() ?: 0}",
                        )

                        if (status == MediaListStatus.CURRENT
                            || status == MediaListStatus.REPEATING) {
                            FilledTonalButton(onClick = onClickPlus) {
                                Text(text = "+1")
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = item.calculateProgressBarValue(),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceColorAtElevation(94.dp),
                        strokeCap = StrokeCap.Round
                    )
                }//:Column
            }//:Column
        }//:Row
    }//: Card
}

@Composable
fun AiringScheduleText(
    item: UserMediaListQuery.MediaList
) {
    item.media?.nextAiringEpisode?.let { nextAiringEpisode ->
        val isBehind = (item.basicMediaListEntry.progress ?: 0) < (nextAiringEpisode.episode - 1)
        Text(
            text =
                if (isBehind)
                    stringResource(R.string.num_episodes_behind,
                        (nextAiringEpisode.episode - 1) - (item.basicMediaListEntry.progress ?: 0)
                    )
                else
                    stringResource(R.string.episode_in_time,
                        nextAiringEpisode.episode,
                        nextAiringEpisode.timeUntilAiring.toLong().secondsToLegibleText()
                    ),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = if (isBehind) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserMediaListItemPreview() {
    val exampleItem = UserMediaListQuery.MediaList(
        __typename = "",
        mediaId = 1,
        media = UserMediaListQuery.Media(
            __typename = "",
            coverImage = null,
            nextAiringEpisode = null,
            status = MediaStatus.RELEASING,
            basicMediaDetails = BasicMediaDetails(
                id = 1,
                title = BasicMediaDetails.Title(userPreferred = "Kimetsu no Yaiba: Katanakaji no Sato-hen"),
                episodes = 12,
                chapters = null,
                volumes = null,
                type = MediaType.ANIME
            )
        ),
        basicMediaListEntry = BasicMediaListEntry(
            id = 1,
            status = MediaListStatus.CURRENT,
            score = 77.0,
            progress = 3,
            progressVolumes = null,
            repeat = null,
            startedAt = null,
            completedAt = null
        )
    )
    AniHyouTheme {
        Surface {
            LazyColumn {
                items(3) {
                    StandardUserMediaListItem(
                        item = exampleItem,
                        status = MediaListStatus.CURRENT,
                        onClick = { },
                        onLongClick = { },
                        onClickPlus = { }
                    )
                }
            }
        }
    }
}