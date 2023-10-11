package com.axiel7.anihyou.ui.screens.home.discover

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.screens.home.discover.content.AiringContent
import com.axiel7.anihyou.ui.screens.home.discover.content.SeasonAnimeContent
import com.axiel7.anihyou.ui.screens.home.discover.content.TrendingMediaContent
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
    val viewModel: DiscoverViewModel = hiltViewModel()
    val airingOnMyList by viewModel.airingOnMyList.collectAsStateWithLifecycle()

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
                DiscoverInfo.AIRING -> {
                    LaunchedEffect(airingOnMyList) {
                        if (airingOnMyList == true) viewModel.fetchAiringAnimeOnMyList()
                        else if (airingOnMyList == false) viewModel.fetchAiringAnime()
                    }
                    AiringContent(
                        airingOnMyList = airingOnMyList,
                        airingAnime = viewModel.airingAnime,
                        airingAnimeOnMyList = viewModel.airingAnimeOnMyList,
                        isLoading = viewModel.isLoadingAiring,
                        navigateToCalendar = navigateToCalendar,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )
                }

                DiscoverInfo.THIS_SEASON -> {
                    LaunchedEffect(viewModel.nowAnimeSeason) {
                        viewModel.fetchThisSeasonAnime()
                    }
                    SeasonAnimeContent(
                        animeSeason = viewModel.nowAnimeSeason,
                        seasonAnime = viewModel.thisSeasonAnime,
                        isLoading = viewModel.isLoadingThisSeason,
                        isNextSeason = false,
                        navigateToAnimeSeason = navigateToAnimeSeason,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )
                }

                DiscoverInfo.TRENDING_ANIME -> {
                    LaunchedEffect(MediaType.ANIME) {
                        viewModel.fetchTrendingAnime()
                    }
                    TrendingMediaContent(
                        mediaType = MediaType.ANIME,
                        trendingMedia = viewModel.trendingAnime,
                        isLoading = viewModel.isLoadingTrendingAnime,
                        navigateToExplore = navigateToExplore,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )
                }

                DiscoverInfo.NEXT_SEASON -> {
                    LaunchedEffect(viewModel.nextAnimeSeason) {
                        viewModel.fetchNextSeasonAnime()
                    }
                    SeasonAnimeContent(
                        animeSeason = viewModel.nextAnimeSeason,
                        seasonAnime = viewModel.nextSeasonAnime,
                        isLoading = viewModel.isLoadingNextSeason,
                        isNextSeason = true,
                        navigateToAnimeSeason = navigateToAnimeSeason,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )
                }

                DiscoverInfo.TRENDING_MANGA -> {
                    LaunchedEffect(MediaType.MANGA) {
                        viewModel.fetchTrendingManga()
                    }
                    TrendingMediaContent(
                        mediaType = MediaType.MANGA,
                        trendingMedia = viewModel.trendingManga,
                        isLoading = viewModel.isLoadingTrendingManga,
                        navigateToExplore = navigateToExplore,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )
                }
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