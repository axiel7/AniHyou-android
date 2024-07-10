package com.axiel7.anihyou.ui.screens.home.discover.content

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.AiringOnMyListQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.fragment.BasicMediaDetails
import com.axiel7.anihyou.fragment.BasicMediaListEntry
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
    onLongClickItem: (BasicMediaDetails, BasicMediaListEntry?) -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToMediaDetails: (mediaId: Int) -> Unit
) {
    HorizontalListHeader(
        text = stringResource(R.string.airing_soon),
        onClick = navigateToCalendar
    )
    when (airingOnMyList) {
        true -> {
            DiscoverLazyRow(
                minHeight = MEDIA_POSTER_SMALL_HEIGHT.dp
            ) {
                items(
                    items = airingAnimeOnMyList,
                    contentType = { it }
                ) { item ->
                    AiringAnimeHorizontalItem(
                        title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                        subtitle = stringResource(
                            R.string.airing_in,
                            item.nextAiringEpisode?.timeUntilAiring?.toLong()
                                ?.secondsToLegibleText() ?: UNKNOWN_CHAR
                        ),
                        imageUrl = item.coverImage?.large,
                        score = item.meanScore,
                        badgeContent = item.mediaListEntry?.basicMediaListEntry?.status?.let { status ->
                            {
                                Icon(
                                    painter = painterResource(status.icon()),
                                    contentDescription = status.localized()
                                )
                            }
                        },
                        onClick = {
                            navigateToMediaDetails(item.id)
                        },
                        onLongClick = {
                            onLongClickItem(
                                item.basicMediaDetails,
                                item.mediaListEntry?.basicMediaListEntry
                            )
                        }
                    )
                }
                if (isLoading) {
                    items(10) {
                        AiringAnimeHorizontalItemPlaceholder()
                    }
                }
            }//:LazyRow
        }

        false -> {
            DiscoverLazyRow(
                minHeight = MEDIA_POSTER_SMALL_HEIGHT.dp
            ) {
                items(
                    items = airingAnime,
                    contentType = { it }
                ) { item ->
                    AiringAnimeHorizontalItem(
                        title = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                        subtitle = stringResource(
                            R.string.airing_in,
                            item.timeUntilAiring.toLong().secondsToLegibleText()
                        ),
                        imageUrl = item.media?.coverImage?.large,
                        score = item.media?.meanScore,
                        badgeContent = item.media?.mediaListEntry?.basicMediaListEntry?.status?.let { status ->
                            {
                                Icon(
                                    painter = painterResource(status.icon()),
                                    contentDescription = status.localized()
                                )
                            }
                        },
                        onClick = {
                            navigateToMediaDetails(item.media!!.id)
                        },
                        onLongClick = {
                            if (item.media != null) {
                                onLongClickItem(
                                    item.media.basicMediaDetails,
                                    item.media.mediaListEntry?.basicMediaListEntry
                                )
                            }
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
                        Text(text = stringResource(R.string.no_information))
                    }
                }
            }//:LazyRow
        }

        else -> {
            Spacer(modifier = Modifier.height(MEDIA_POSTER_SMALL_HEIGHT.dp))
        }
    }
}