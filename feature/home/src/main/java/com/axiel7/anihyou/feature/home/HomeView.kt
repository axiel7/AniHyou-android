package com.axiel7.anihyou.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.model.HomeTab
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.IconButtonWithBadge
import com.axiel7.anihyou.feature.home.activity.ActivityFeedView
import com.axiel7.anihyou.feature.home.current.CurrentView
import com.axiel7.anihyou.feature.home.discover.DiscoverView
import com.axiel7.anihyou.feature.login.LoginView
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    isLoggedIn: Boolean,
    defaultHomeTab: HomeTab,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navActionManager: NavActionManager,
) {
    val viewModel: HomeViewModel = koinViewModel()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(defaultHomeTab.ordinal) }

    LaunchedEffect(selectedTabIndex) {
        viewModel.saveHomeTab(selectedTabIndex)
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.home),
        modifier = modifier,
        floatingActionButton = {
            if (selectedTabIndex == HomeTab.ACTIVITY_FEED.ordinal && isLoggedIn) {
                FloatingActionButton(
                    onClick = { navActionManager.toPublishNewActivity() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_24),
                        contentDescription = stringResource(R.string.add)
                    )
                }
            }
        },
        actions = {
            if (isLoggedIn) {
                val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsStateWithLifecycle(
                    initialValue = 0
                )
                IconButtonWithBadge(
                    icon = R.drawable.notifications_24,
                    badge = {
                        if (unreadNotificationCount > 0) {
                            Badge {
                                Text(text = unreadNotificationCount.format().orEmpty())
                            }
                        }
                    },
                    onClick = { navActionManager.toNotifications(unreadNotificationCount) }
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
            ) {
                HomeTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTabIndex == tab.ordinal,
                        onClick = { selectedTabIndex = tab.ordinal },
                        text = { Text(text = tab.localized()) }
                    )
                }
            }
            when (HomeTab.entries[selectedTabIndex]) {
                HomeTab.DISCOVER ->
                    DiscoverView(
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = contentPadding,
                        navActionManager = navActionManager,
                    )

                HomeTab.ACTIVITY_FEED -> {
                    if (isLoggedIn) {
                        ActivityFeedView(
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                            navActionManager = navActionManager,
                        )
                    } else {
                        LoginView()
                    }
                }

                HomeTab.CURRENT -> {
                    if (isLoggedIn) {
                        CurrentView(
                            navActionManager = navActionManager,
                            modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        )
                    } else {
                        LoginView()
                    }
                }
            }
        }//:Column
    }//:Scaffold
}