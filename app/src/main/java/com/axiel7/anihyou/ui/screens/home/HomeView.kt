package com.axiel7.anihyou.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.RoundedTabRowIndicator
import com.axiel7.anihyou.ui.screens.home.discover.DiscoverView
import com.axiel7.anihyou.ui.theme.AniHyouTheme

enum class HomeTab : Localizable {
    DISCOVER,
    ACTIVITY_FEED;

    @Composable
    override fun localized() = when (this) {
        DISCOVER -> stringResource(R.string.discover)
        ACTIVITY_FEED -> stringResource(R.string.activity)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToMediaDetails: (mediaId: Int) -> Unit,
    navigateToAnimeSeason: (AnimeSeason) -> Unit,
    navigateToCalendar: () -> Unit,
    navigateToExplore: (MediaType, MediaSort) -> Unit,
    navigateToNotifications: () -> Unit,
) {
    val viewModel: HomeViewModel = viewModel()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    DefaultScaffoldWithSmallTopAppBar(
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
        Column(
            modifier = Modifier.padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                indicator = { tabPositions ->
                    RoundedTabRowIndicator(tabPositions[selectedTabIndex])
                }
            ) {
                HomeTab.entries.forEachIndexed { index, homeTab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = homeTab.localized()) }
                    )
                }
            }
            when (HomeTab.entries[selectedTabIndex]) {
                HomeTab.DISCOVER -> DiscoverView(
                    modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                    contentPadding = contentPadding,
                    navigateToMediaDetails = navigateToMediaDetails,
                    navigateToAnimeSeason = navigateToAnimeSeason,
                    navigateToCalendar = navigateToCalendar,
                    navigateToExplore = navigateToExplore
                )
                
                HomeTab.ACTIVITY_FEED -> Text(text = "Coming Soon")
            }
        }//:Column
    }//:Scaffold
}

@Preview
@Composable
fun HomeViewPreview() {
    AniHyouTheme {
        Surface {
            HomeView(
                navigateToMediaDetails = {},
                navigateToAnimeSeason = {},
                navigateToCalendar = {},
                navigateToExplore = { _, _ -> },
                navigateToNotifications = {}
            )
        }
    }
}