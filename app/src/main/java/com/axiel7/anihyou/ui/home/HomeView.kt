package com.axiel7.anihyou.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.AiringAnimeHorizontalItem
import com.axiel7.anihyou.ui.composables.AiringAnimeHorizontalItemPlaceholder
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.MEDIA_ITEM_VERTICAL_HEIGHT
import com.axiel7.anihyou.ui.composables.MEDIA_POSTER_SMALL_HEIGHT
import com.axiel7.anihyou.ui.composables.MediaItemVertical
import com.axiel7.anihyou.ui.composables.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.composables.SmallScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText

@Composable
fun HomeView(
    navigateToDetails: (mediaId: Int) -> Unit
) {
    val viewModel: HomeViewModel = viewModel()
    val scrollState = rememberScrollState()

    DefaultScaffoldWithMediumTopAppBar(
        title = stringResource(R.string.home)
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(state = scrollState)
                .padding(padding)
        ) {
            // Airing
            HorizontalListHeader(
                text = stringResource(R.string.airing_soon),
                onClick = { /*TODO*/ }
            )
            HomeLazyRow(
                minHeight = MEDIA_POSTER_SMALL_HEIGHT.dp
            ) {
                if (viewModel.isLoadingAiring) {
                    items(10) {
                        AiringAnimeHorizontalItemPlaceholder()
                    }
                }
                else items(viewModel.airingAnime) { item ->
                    AiringAnimeHorizontalItem(
                        title = item.media?.title?.userPreferred ?: "",
                        subtitle = stringResource(R.string.airing_in, item.timeUntilAiring.toLong().secondsToLegibleText()),
                        imageUrl = item.media?.coverImage?.large,
                        score = if (item.media?.meanScore != null) "${item.media.meanScore}%" else null,
                        onClick = {
                            navigateToDetails(item.media!!.id)
                        }
                    )
                }
            }

            // This season
            HorizontalListHeader(
                text = viewModel.nowAnimeSeason.localized(),
                onClick = { /*TODO*/ }
            )
            HomeLazyRow(
                minHeight = MEDIA_ITEM_VERTICAL_HEIGHT.dp
            ) {
                if (viewModel.isLoadingThisSeason) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                else items(viewModel.thisSeasonAnime) { item ->
                    MediaItemVertical(
                        url = item.coverImage?.large,
                        title = item.title?.userPreferred ?: "",
                        modifier = Modifier.padding(start = 8.dp),
                        subtitle = {
                            item.meanScore?.let { score ->
                                SmallScoreIndicator(score = "${score}%")
                            }
                        },
                        onClick = { navigateToDetails(item.id) }
                    )
                }
            }

            // Trending Anime
            HorizontalListHeader(
                text = stringResource(R.string.trending_now),
                onClick = { /*TODO*/ }
            )
            HomeLazyRow(
                minHeight = MEDIA_ITEM_VERTICAL_HEIGHT.dp
            ) {
                if (viewModel.isLoadingTrendingAnime) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                else items(viewModel.trendingAnime) { item ->
                    MediaItemVertical(
                        url = item.coverImage?.large,
                        title = item.title?.userPreferred ?: "",
                        modifier = Modifier.padding(start = 8.dp),
                        subtitle = {
                            item.meanScore?.let { score ->
                                SmallScoreIndicator(score = "${score}%")
                            }
                        },
                        onClick = { navigateToDetails(item.id) }
                    )
                }
            }

            // Next season
            HorizontalListHeader(
                text = stringResource(R.string.next_season),
                onClick = { /*TODO*/ }
            )
            HomeLazyRow(
                minHeight = MEDIA_ITEM_VERTICAL_HEIGHT.dp
            ) {
                if (viewModel.isLoadingNextSeason) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                else items(viewModel.nextSeasonAnime) { item ->
                    MediaItemVertical(
                        url = item.coverImage?.large,
                        title = item.title?.userPreferred ?: "",
                        modifier = Modifier.padding(start = 8.dp),
                        subtitle = {
                            item.meanScore?.let { score ->
                                SmallScoreIndicator(score = "${score}%")
                            }
                        },
                        onClick = { navigateToDetails(item.id) }
                    )
                }
            }

            // Trending Manga
            HorizontalListHeader(
                text = stringResource(R.string.trending_manga),
                onClick = { /*TODO*/ }
            )
            HomeLazyRow(
                minHeight = MEDIA_ITEM_VERTICAL_HEIGHT.dp
            ) {
                if (viewModel.isLoadingTrendingManga) {
                    items(10) {
                        MediaItemVerticalPlaceholder()
                    }
                }
                else items(viewModel.trendingManga) { item ->
                    MediaItemVertical(
                        url = item.coverImage?.large,
                        title = item.title?.userPreferred ?: "",
                        modifier = Modifier.padding(start = 8.dp),
                        subtitle = {
                            item.meanScore?.let { score ->
                                SmallScoreIndicator(score = "${score}%")
                            }
                        },
                        onClick = { navigateToDetails(item.id) }
                    )
                }
            }
        }//: Column
    }//: Scaffold

    LaunchedEffect(Unit) {
        viewModel.getAiringAnime()
        viewModel.getThisSeasonAnime()
        viewModel.getTrendingAnime()
        viewModel.getNextSeasonAnime()
        viewModel.getTrendingManga()
    }
}

@Composable
fun HorizontalListHeader(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Icon(painter = painterResource(R.drawable.arrow_forward_24), contentDescription = "arrow")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeLazyRow(
    minHeight: Dp = MEDIA_POSTER_SMALL_HEIGHT.dp,
    content: LazyListScope.() -> Unit
) {
    val state = rememberLazyListState()
    LazyRow(
        modifier = Modifier
            .padding(top = 8.dp)
            .sizeIn(minHeight = minHeight),
        state = state,
        contentPadding = PaddingValues(horizontal = 8.dp),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
        content = content
    )
}

@Preview
@Composable
fun HomeViewPreview() {
    AniHyouTheme {
        HomeView(
            navigateToDetails = { }
        )
    }
}