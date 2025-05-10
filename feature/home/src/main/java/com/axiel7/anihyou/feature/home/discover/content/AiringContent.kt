package com.axiel7.anihyou.feature.home.discover.content

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import com.axiel7.anihyou.core.network.AiringAnimesQuery
import com.axiel7.anihyou.core.network.AiringOnMyListQuery
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.list.HorizontalListHeader
import com.axiel7.anihyou.core.ui.composables.media.AiringAnimeHorizontalItem
import com.axiel7.anihyou.core.ui.composables.media.AiringAnimeHorizontalItemPlaceholder
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_HEIGHT
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.secondsToLegibleText
import com.axiel7.anihyou.core.ui.composables.list.DiscoverLazyRow

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
                        status = item.mediaListEntry?.basicMediaListEntry?.status,
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
                        status = item.media?.mediaListEntry?.basicMediaListEntry?.status,
                        onClick = {
                            navigateToMediaDetails(item.media!!.id)
                        },
                        onLongClick = {
                            item.media?.let { media ->
                                onLongClickItem(
                                    media.basicMediaDetails,
                                    media.mediaListEntry?.basicMediaListEntry
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