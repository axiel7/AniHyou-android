package com.axiel7.anihyou.ui.composables.media

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.data.model.media.calculateProgressBarValue
import com.axiel7.anihyou.data.model.media.duration
import com.axiel7.anihyou.data.model.media.isBehind
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.composables.scores.BadgeScoreIndicator
import com.axiel7.anihyou.ui.composables.scores.MinimalScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText

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

                BadgeScoreIndicator(
                    score = item.basicMediaListEntry.score,
                    scoreFormat = scoreFormat
                )
            }//:Box

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = item.media?.basicMediaDetails?.title?.userPreferred ?: "",
                        modifier = Modifier
                            .padding(vertical = 8.dp),
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
                            .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${item.basicMediaListEntry.progress ?: 0}/${item.media?.basicMediaDetails?.duration() ?: 0}",
                            fontSize = 16.sp
                        )

                        if (isMyList && (status == MediaListStatus.CURRENT
                                    || status == MediaListStatus.REPEATING)
                            ) {
                            FilledTonalButton(onClick = onClickPlus) {
                                Text(text = "+1")
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = item.calculateProgressBarValue(),
                        modifier = Modifier
                            .padding(vertical = 1.dp)
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceColorAtElevation(94.dp),
                        strokeCap = StrokeCap.Round
                    )
                }//:Column
            }//:Column
        }//:Row
    }//: Card
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CompactUserMediaListItem(
    item: UserMediaListQuery.MediaList,
    status: MediaListStatus,
    scoreFormat: ScoreFormat,
    isMyList: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlus: () -> Unit,
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
            .combinedClickable(onLongClick = onLongClick, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.height(MEDIA_POSTER_COMPACT_HEIGHT.dp)
        ) {
            Box(
                contentAlignment = Alignment.BottomStart
            ) {
                MediaPoster(
                    url = item.media?.coverImage?.large,
                    showShadow = false,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .size(MEDIA_POSTER_SMALL_WIDTH.dp)
                )

                BadgeScoreIndicator(
                    score = item.basicMediaListEntry.score,
                    scoreFormat = scoreFormat
                )
            }//: Box

            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.media?.basicMediaDetails?.title?.userPreferred ?: "",
                    modifier = Modifier
                        .padding(top = 8.dp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${item.basicMediaListEntry.progress ?: 0}/${item.media?.basicMediaDetails?.duration() ?: 0}",
                        fontSize = 15.sp,
                    )

                    if (isMyList && (status == MediaListStatus.CURRENT
                                || status == MediaListStatus.REPEATING)
                    ) {
                        FilledTonalButton(onClick = onClickPlus) {
                            Text(text = "+1")
                        }
                    }
                }
            }//:Column
        }//:Row
    }//:Card
}

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
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
            .combinedClickable(onLongClick = onLongClick, onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .height(84.dp)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridUserMediaListItem(
    item: UserMediaListQuery.MediaList,
    scoreFormat: ScoreFormat,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onLongClick = onLongClick, onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
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

                BadgeScoreIndicator(
                    modifier = Modifier.align(Alignment.BottomStart),
                    score = item.basicMediaListEntry.score,
                    scoreFormat = scoreFormat
                )
                if (item.media?.nextAiringEpisode != null) {
                    Row(
                        modifier = Modifier
                            .shadow(8.dp)
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                            .background(MaterialTheme.colorScheme.surface),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
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
                text = item.media?.basicMediaDetails?.title?.userPreferred ?: "",
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
                Text(
                    text = "${item.basicMediaListEntry.progress ?: 0}/${item.media?.basicMediaDetails?.duration() ?: 0}",
                    fontSize = 15.sp,
                    maxLines = 1
                )
            }
        }//:Column
    }//:Card
}

@Composable
fun AiringScheduleText(
    item: UserMediaListQuery.MediaList,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
) {
    item.media?.nextAiringEpisode?.let { nextAiringEpisode ->
        val isBehind = item.basicMediaListEntry.isBehind(nextAiringEpisode = nextAiringEpisode.episode)
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
            modifier = modifier,
            color = if (isBehind) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = fontSize,
            textAlign = textAlign,
            lineHeight = fontSize
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserMediaListItemPreview() {
    val exampleItem = UserMediaListQuery.MediaList(
        __typename = "",
        id = 1,
        mediaId = 1,
        media = UserMediaListQuery.Media(
            __typename = "",
            id = 1,
            coverImage = null,
            nextAiringEpisode = UserMediaListQuery.NextAiringEpisode(
                episode = 3,
                timeUntilAiring = 1203239
            ),
            status = MediaStatus.RELEASING,
            basicMediaDetails = BasicMediaDetails(
                __typename = "",
                id = 1,
                title = BasicMediaDetails.Title(userPreferred = "Kimetsu no Yaiba: Katanakaji no Sato-hen"),
                episodes = 12,
                chapters = null,
                volumes = null,
                type = MediaType.ANIME
            )
        ),
        basicMediaListEntry = BasicMediaListEntry(
            __typename = "",
            id = 1,
            mediaId = 1,
            status = MediaListStatus.CURRENT,
            score = 77.0,
            progress = 3,
            progressVolumes = null,
            repeat = null,
            startedAt = null,
            completedAt = null,
            private = false,
            notes = null,
        )
    )
    AniHyouTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                StandardUserMediaListItem(
                    item = exampleItem,
                    status = MediaListStatus.CURRENT,
                    scoreFormat = ScoreFormat.POINT_100,
                    isMyList = true,
                    onClick = { },
                    onLongClick = { },
                    onClickPlus = { }
                )
                CompactUserMediaListItem(
                    item = exampleItem,
                    status = MediaListStatus.CURRENT,
                    scoreFormat = ScoreFormat.POINT_100,
                    isMyList = true,
                    onClick = { },
                    onLongClick = { },
                    onClickPlus = { }
                )
                MinimalUserMediaListItem(
                    item = exampleItem,
                    status = MediaListStatus.CURRENT,
                    scoreFormat = ScoreFormat.POINT_100,
                    isMyList = true,
                    onClick = { },
                    onLongClick = { },
                    onClickPlus = { }
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_MEDIUM_WIDTH + 8).dp),
                    contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    items(3) {
                        GridUserMediaListItem(
                            item = if (it == 1) exampleItem.copy(
                                media = exampleItem.media?.copy(nextAiringEpisode = null)
                            ) else exampleItem,
                            scoreFormat = ScoreFormat.POINT_100,
                            onClick = { },
                            onLongClick = { }
                        )
                    }
                }
            }
        }
    }
}