package com.axiel7.anihyou.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.screens.home.content.HomeAiringContent
import com.axiel7.anihyou.ui.screens.home.content.HomeNextSeasonContent
import com.axiel7.anihyou.ui.screens.home.content.HomeThisSeasonContent
import com.axiel7.anihyou.ui.screens.home.content.HomeTrendingAnimeContent
import com.axiel7.anihyou.ui.screens.home.content.HomeTrendingMangaContent
import com.axiel7.anihyou.ui.theme.AniHyouTheme

enum class HomeInfo {
    AIRING,
    THIS_SEASON,
    TRENDING_ANIME,
    NEXT_SEASON,
    TRENDING_MANGA,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (mediaId: Int) -> Unit,
    navigateToAnimeSeason: (AnimeSeason) -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToExplore: (MediaType, MediaSort) -> Unit,
    navigateToNotifications: () -> Unit,
) {
    val viewModel: HomeViewModel = viewModel()
    val listState = rememberLazyListState()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    listState.OnBottomReached(buffer = 0) {
        viewModel.addNextInfo()
    }

    DefaultScaffoldWithMediumTopAppBar(
        title = stringResource(R.string.home),
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
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(viewModel.infos) { item ->
                when (item) {
                    HomeInfo.AIRING -> HomeAiringContent(
                        viewModel = viewModel,
                        navigateToCalendar = navigateToCalendar,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )

                    HomeInfo.THIS_SEASON -> HomeThisSeasonContent(
                        viewModel = viewModel,
                        navigateToAnimeSeason = navigateToAnimeSeason,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )

                    HomeInfo.TRENDING_ANIME -> HomeTrendingAnimeContent(
                        viewModel = viewModel,
                        navigateToExplore = navigateToExplore,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )

                    HomeInfo.NEXT_SEASON -> HomeNextSeasonContent(
                        viewModel = viewModel,
                        navigateToAnimeSeason = navigateToAnimeSeason,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )

                    HomeInfo.TRENDING_MANGA -> HomeTrendingMangaContent(
                        viewModel = viewModel,
                        navigateToExplore = navigateToExplore,
                        navigateToMediaDetails = navigateToMediaDetails,
                    )
                }
            }
        }//: LazyColumn
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

@Preview
@Composable
fun HomeViewPreview() {
    AniHyouTheme {
        HomeView(
            navigateToMediaDetails = { },
            navigateToAnimeSeason = { },
            navigateToCalendar = { },
            navigateToExplore = { _, _ -> },
            navigateToNotifications = {},
        )
    }
}