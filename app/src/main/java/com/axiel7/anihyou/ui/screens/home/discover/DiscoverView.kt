package com.axiel7.anihyou.ui.screens.home.discover

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverView(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToMediaDetails: (mediaId: Int) -> Unit,
    navigateToAnimeSeason: (AnimeSeason) -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToExplore: (MediaType, MediaSort) -> Unit,
    navigateToNotifications: () -> Unit,
) {
    val viewModel: DiscoverViewModel = viewModel()
    val listState = rememberLazyListState()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    listState.OnBottomReached(buffer = 0) {
        viewModel.addNextInfo()
    }

    DefaultScaffoldWithMediumTopAppBar(
        title = stringResource(R.string.discover),
        modifier = modifier,
        actions = {
            BadgedBox(
                badge = {
                    val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsState()
                    if (unreadNotificationCount > 0) {
                        Badge {
                            Text(text = unreadNotificationCount.toString())
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        navigateToNotifications()
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.notifications_24),
                    contentDescription = stringResource(R.string.notifications)
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .padding(padding),
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
    }//: Scaffold
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
            navigateToNotifications = {},
        )
    }
}