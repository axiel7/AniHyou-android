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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.RoundedTabRowIndicator
import com.axiel7.anihyou.ui.screens.home.activity.ActivityFeedView
import com.axiel7.anihyou.ui.screens.home.discover.DiscoverView
import com.axiel7.anihyou.ui.theme.AniHyouTheme

enum class HomeTab(val index: Int) : Localizable {
    DISCOVER(0),
    ACTIVITY_FEED(1);

    @Composable
    override fun localized() = stringResource(stringRes)

    val stringRes
        get() = when (this) {
            DISCOVER -> R.string.discover
            ACTIVITY_FEED -> R.string.activity
        }

    companion object {
        val entriesLocalized = entries.associateWith { it.stringRes }

        fun valueOf(index: Int) = entries.find { it.index == index }
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
    navigateToNotifications: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToActivityDetails: (Int) -> Unit,
    navigateToPublishActivity: (Int?, String?) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val defaultHomeTab by viewModel.defaultHomeTab.collectAsStateWithLifecycle()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(defaultHomeTab?.index ?: 0) }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.home),
        modifier = modifier,
        floatingActionButton = {
            if (selectedTabIndex == HomeTab.ACTIVITY_FEED.index) {
                FloatingActionButton(
                    onClick = { navigateToPublishActivity(null, null) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_24),
                        contentDescription = stringResource(R.string.add)
                    )
                }
            }
        },
        actions = {
            val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
            BadgedBox(
                badge = {
                    if (unreadNotificationCount > 0) {
                        Badge {
                            Text(text = unreadNotificationCount.toString())
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable {
                        navigateToNotifications(unreadNotificationCount)
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
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                indicator = { tabPositions ->
                    RoundedTabRowIndicator(tabPositions[selectedTabIndex])
                }
            ) {
                HomeTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTabIndex == tab.index,
                        onClick = { selectedTabIndex = tab.index },
                        text = { Text(text = tab.localized()) }
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

                HomeTab.ACTIVITY_FEED -> ActivityFeedView(
                    modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                    navigateToActivityDetails = navigateToActivityDetails,
                    navigateToMediaDetails = navigateToMediaDetails,
                    navigateToUserDetails = navigateToUserDetails,
                    navigateToFullscreenImage = navigateToFullscreenImage,
                )
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
                navigateToNotifications = {},
                navigateToUserDetails = {},
                navigateToActivityDetails = {},
                navigateToPublishActivity = { _, _ -> },
                navigateToFullscreenImage = {},
            )
        }
    }
}