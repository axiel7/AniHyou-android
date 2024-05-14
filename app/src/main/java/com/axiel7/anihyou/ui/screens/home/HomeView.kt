package com.axiel7.anihyou.ui.screens.home

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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.IconButtonWithBadge
import com.axiel7.anihyou.ui.screens.home.activity.ActivityFeedView
import com.axiel7.anihyou.ui.screens.home.discover.DiscoverView
import com.axiel7.anihyou.ui.screens.login.LoginView
import com.axiel7.anihyou.utils.NumberUtils.format
import kotlinx.serialization.Serializable

@Serializable
@Immutable
object Home

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    isLoggedIn: Boolean,
    defaultHomeTab: HomeTab,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navActionManager: NavActionManager,
) {
    val viewModel: HomeViewModel = hiltViewModel()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(defaultHomeTab.ordinal) }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.home),
        modifier = modifier,
        floatingActionButton = {
            if (selectedTabIndex == HomeTab.ACTIVITY_FEED.ordinal && isLoggedIn) {
                FloatingActionButton(
                    onClick = { navActionManager.toPublishActivity(null, null) }
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
            }
        }//:Column
    }//:Scaffold
}