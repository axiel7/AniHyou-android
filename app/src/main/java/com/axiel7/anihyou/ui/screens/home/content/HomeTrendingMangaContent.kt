package com.axiel7.anihyou.ui.screens.home.content

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.media.MEDIA_ITEM_VERTICAL_HEIGHT
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.ui.screens.home.HomeLazyRow
import com.axiel7.anihyou.ui.screens.home.HomeViewModel
import com.axiel7.anihyou.ui.screens.home.HorizontalListHeader

@Composable
fun HomeTrendingMangaContent(
    viewModel: HomeViewModel,
    navigateToExplore: (MediaType, MediaSort) -> Unit,
    navigateToMediaDetails: (mediaId: Int) -> Unit,
) {
    HorizontalListHeader(
        text = stringResource(R.string.trending_manga),
        onClick = {
            navigateToExplore(MediaType.MANGA, MediaSort.TRENDING_DESC)
        }
    )
    val trendingManga by viewModel.trendingManga.collectAsState()
    HomeLazyRow(
        minHeight = MEDIA_ITEM_VERTICAL_HEIGHT.dp
    ) {
        when (trendingManga) {
            is PagedResult.Loading -> {
                items(10) {
                    MediaItemVerticalPlaceholder(
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            is PagedResult.Success -> {
                items((trendingManga as PagedResult.Success).data) { item ->
                    MediaItemVertical(
                        title = item.title?.userPreferred ?: "",
                        imageUrl = item.coverImage?.large,
                        modifier = Modifier.padding(start = 8.dp),
                        subtitle = {
                            item.meanScore?.let { score ->
                                SmallScoreIndicator(score = "${score}%")
                            }
                        },
                        minLines = 2,
                        onClick = { navigateToMediaDetails(item.id) }
                    )
                }
            }

            is PagedResult.Error -> {
                item {
                    Text(text = (trendingManga as PagedResult.Error).message)
                }
            }
        }
    }//:LazyRow
}