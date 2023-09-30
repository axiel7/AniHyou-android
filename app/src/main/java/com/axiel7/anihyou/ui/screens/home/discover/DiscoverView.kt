package com.axiel7.anihyou.ui.screens.home.discover

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.screens.home.discover.content.AiringContent
import com.axiel7.anihyou.ui.screens.home.discover.content.NextSeasonContent
import com.axiel7.anihyou.ui.screens.home.discover.content.ThisSeasonContent
import com.axiel7.anihyou.ui.screens.home.discover.content.TrendingAnimeContent
import com.axiel7.anihyou.ui.screens.home.discover.content.TrendingMangaContent
import com.axiel7.anihyou.ui.theme.AniHyouTheme

enum class DiscoverInfo {
    AIRING,
    THIS_SEASON,
    TRENDING_ANIME,
    NEXT_SEASON,
    TRENDING_MANGA,
}

@Composable
fun DiscoverView(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToMediaDetails: (mediaId: Int) -> Unit,
    navigateToAnimeSeason: (AnimeSeason) -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToExplore: (MediaType, MediaSort) -> Unit,
) {
    val viewModel: DiscoverViewModel = viewModel()
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 0) {
        viewModel.addNextInfo()
    }
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding
    ) {
        items(viewModel.infos) { item ->
            when (item) {
                DiscoverInfo.AIRING -> AiringContent(
                    viewModel = viewModel,
                    navigateToCalendar = navigateToCalendar,
                    navigateToMediaDetails = navigateToMediaDetails,
                )

                DiscoverInfo.THIS_SEASON -> ThisSeasonContent(
                    viewModel = viewModel,
                    navigateToAnimeSeason = navigateToAnimeSeason,
                    navigateToMediaDetails = navigateToMediaDetails,
                )

                DiscoverInfo.TRENDING_ANIME -> TrendingAnimeContent(
                    viewModel = viewModel,
                    navigateToExplore = navigateToExplore,
                    navigateToMediaDetails = navigateToMediaDetails,
                )

                DiscoverInfo.NEXT_SEASON -> NextSeasonContent(
                    viewModel = viewModel,
                    navigateToAnimeSeason = navigateToAnimeSeason,
                    navigateToMediaDetails = navigateToMediaDetails,
                )

                DiscoverInfo.TRENDING_MANGA -> TrendingMangaContent(
                    viewModel = viewModel,
                    navigateToExplore = navigateToExplore,
                    navigateToMediaDetails = navigateToMediaDetails,
                )
            }
        }
    }//: LazyColumn
}

@Preview
@Composable
fun DiscoverViewPreview() {
    AniHyouTheme {
        DiscoverView(
            navigateToMediaDetails = { },
            navigateToAnimeSeason = { },
            navigateToCalendar = { },
            navigateToExplore = { _, _ -> },
        )
    }
}