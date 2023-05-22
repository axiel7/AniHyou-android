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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.AnimeSeason
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.SmallScoreIndicator
import com.axiel7.anihyou.ui.composables.media.AiringAnimeHorizontalItem
import com.axiel7.anihyou.ui.composables.media.AiringAnimeHorizontalItemPlaceholder
import com.axiel7.anihyou.ui.composables.media.MEDIA_ITEM_VERTICAL_HEIGHT
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_HEIGHT
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (mediaId: Int) -> Unit,
    navigateToAnimeSeason: (AnimeSeason) -> Unit,
) {
    val viewModel: HomeViewModel = viewModel()
    val scrollState = rememberScrollState()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    LaunchedEffect(Unit) {
        viewModel.getAiringAnime()
        viewModel.getThisSeasonAnime()
        viewModel.getTrendingAnime()
        viewModel.getNextSeasonAnime()
        viewModel.getTrendingManga()
    }

    DefaultScaffoldWithMediumTopAppBar(
        title = stringResource(R.string.home),
        modifier = modifier,
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(state = scrollState)
                .padding(padding)
        ) {
            // Airing
            HorizontalListHeader(
                text = stringResource(R.string.airing_soon),
                onClick = null
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
                            navigateToMediaDetails(item.media!!.id)
                        }
                    )
                }
            }

            // This season
            HorizontalListHeader(
                text = viewModel.nowAnimeSeason.localized(),
                onClick = {
                    navigateToAnimeSeason(viewModel.nowAnimeSeason)
                }
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

            // Trending Anime
            HorizontalListHeader(
                text = stringResource(R.string.trending_now),
                onClick = null
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

            // Next season
            HorizontalListHeader(
                text = stringResource(R.string.next_season),
                onClick = {
                    navigateToAnimeSeason(viewModel.nextAnimeSeason)
                }
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

            // Trending Manga
            HorizontalListHeader(
                text = stringResource(R.string.trending_manga),
                onClick = null
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
        }//: Column
    }//: Scaffold
}

@Composable
fun HorizontalListHeader(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Box(modifier = Modifier.clickable(onClick = onClick ?: {})) {
        Row(
            modifier = modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            if (onClick != null) {
                Icon(
                    painter = painterResource(R.drawable.arrow_forward_24),
                    contentDescription = "arrow"
                )
            }
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
            navigateToMediaDetails = { },
            navigateToAnimeSeason = { }
        )
    }
}