package com.axiel7.anihyou.ui.screens.home.discover.content

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.AiringOnMyListQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.list.HorizontalListHeader
import com.axiel7.anihyou.ui.composables.media.AiringAnimeHorizontalItem
import com.axiel7.anihyou.ui.composables.media.AiringAnimeHorizontalItemPlaceholder
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_HEIGHT
import com.axiel7.anihyou.ui.screens.home.discover.composables.DiscoverLazyRow
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.UNKNOWN_CHAR

@Composable
fun AiringContent(
    airingOnMyList: Boolean?,
    airingAnime: List<AiringAnimesQuery.AiringSchedule>,
    airingAnimeOnMyList: List<AiringOnMyListQuery.Medium>,
    isLoading: Boolean,
    navigateToCalendar: () -> Unit,
    navigateToMediaDetails: (mediaId: Int) -> Unit
) {
    HorizontalListHeader(
        text = stringResource(R.string.airing),
        onClick = navigateToCalendar
    )
    if (airingOnMyList == true) {
        DiscoverLazyRow(
            minHeight = MEDIA_POSTER_SMALL_HEIGHT.dp
        ) {
            items(
                items = airingAnimeOnMyList,
                contentType = { it }
            ) { item ->
                AiringAnimeHorizontalItem(
                    title = item.title?.userPreferred ?: "",
                    subtitle = stringResource(
                        R.string.airing_in,
                        item.nextAiringEpisode?.timeUntilAiring?.toLong()
                            ?.secondsToLegibleText() ?: UNKNOWN_CHAR
                    ),
                    imageUrl = item.coverImage?.large,
                    score = if (item.meanScore != null) "${item.meanScore}%" else null,
                    onClick = {
                        navigateToMediaDetails(item.id)
                    }
                )
            }
            if (isLoading) {
                items(10) {
                    AiringAnimeHorizontalItemPlaceholder()
                }
            }
        }//:LazyRow
    } else if (airingOnMyList == false) {
        DiscoverLazyRow(
            minHeight = MEDIA_POSTER_SMALL_HEIGHT.dp
        ) {
            items(
                items = airingAnime,
                contentType = { it }
            ) { item ->
                AiringAnimeHorizontalItem(
                    title = item.media?.title?.userPreferred ?: "",
                    subtitle = stringResource(
                        R.string.airing_in,
                        item.timeUntilAiring.toLong().secondsToLegibleText()
                    ),
                    imageUrl = item.media?.coverImage?.large,
                    score = if (item.media?.meanScore != null) "${item.media.meanScore}%" else null,
                    onClick = {
                        navigateToMediaDetails(item.media!!.id)
                    }
                )
            }
            if (isLoading) {
                items(10) {
                    AiringAnimeHorizontalItemPlaceholder()
                }
            }
            if (airingAnime.isEmpty()) {
                item {
                    Text(text = "No airing anime")
                }
            }
        }//:LazyRow
    }
}