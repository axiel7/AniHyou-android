package com.axiel7.anihyou.feature.home.discover.content

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.network.SeasonalAnimeQuery
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.list.HorizontalListHeader
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_ITEM_VERTICAL_HEIGHT
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.core.ui.composables.list.DiscoverLazyRow

@Composable
fun SeasonAnimeContent(
    animeSeason: AnimeSeason,
    seasonAnime: List<SeasonalAnimeQuery.Medium>,
    isLoading: Boolean,
    isNextSeason: Boolean,
    onLongClickItem: (SeasonalAnimeQuery.Medium) -> Unit,
    navigateToAnimeSeason: (AnimeSeason) -> Unit,
    navigateToMediaDetails: (mediaId: Int) -> Unit,
) {
    HorizontalListHeader(
        text = if (isNextSeason) stringResource(R.string.next_season)
        else animeSeason.localized(),
        onClick = {
            navigateToAnimeSeason(animeSeason)
        }
    )
    DiscoverLazyRow(
        minHeight = MEDIA_ITEM_VERTICAL_HEIGHT.dp
    ) {
        items(
            items = seasonAnime,
            contentType = { it }
        ) { item ->
            MediaItemVertical(
                title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                imageUrl = item.coverImage?.large,
                modifier = Modifier.padding(horizontal = 8.dp),
                subtitle = {
                    item.meanScore?.let { meanScore ->
                        SmallScoreIndicator(score = meanScore)
                    } ?: run {
                        Spacer(modifier = Modifier.size(20.dp))
                    }
                },
                status = item.mediaListEntry?.basicMediaListEntry?.status,
                minLines = 2,
                onClick = { navigateToMediaDetails(item.id) },
                onLongClick = { onLongClickItem(item) }
            )
        }
        if (isLoading) {
            items(10) {
                MediaItemVerticalPlaceholder(
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
        if (seasonAnime.isEmpty()) {
            item {
                Text(text = stringResource(R.string.no_information))
            }
        }
    }//:LazyRow
}