package com.axiel7.anihyou.ui.screens.main

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.axiel7.anihyou.data.model.DeepLink
import com.axiel7.anihyou.ui.common.BottomDestination
import com.axiel7.anihyou.ui.common.BottomDestination.Companion.toBottomDestinationRoute
import com.axiel7.anihyou.ui.common.navigation.DestArgument.Companion.getBoolean
import com.axiel7.anihyou.ui.common.navigation.DestArgument.Companion.getIntArg
import com.axiel7.anihyou.ui.common.navigation.DestArgument.Companion.getStringArg
import com.axiel7.anihyou.ui.common.navigation.NavActionManager.Companion.rememberNavActionManager
import com.axiel7.anihyou.ui.common.navigation.NavArgument
import com.axiel7.anihyou.ui.common.navigation.NavDestination
import com.axiel7.anihyou.ui.composables.FullScreenImageView
import com.axiel7.anihyou.ui.screens.activitydetails.ActivityDetailsView
import com.axiel7.anihyou.ui.screens.activitydetails.publish.PublishActivityView
import com.axiel7.anihyou.ui.screens.calendar.CalendarView
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.ui.screens.explore.ExploreView
import com.axiel7.anihyou.ui.screens.explore.charts.MediaChartListView
import com.axiel7.anihyou.ui.screens.explore.search.SearchView
import com.axiel7.anihyou.ui.screens.explore.season.SeasonAnimeView
import com.axiel7.anihyou.ui.screens.home.HomeTab
import com.axiel7.anihyou.ui.screens.home.HomeView
import com.axiel7.anihyou.ui.screens.login.LoginView
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetailsView
import com.axiel7.anihyou.ui.screens.notifications.NotificationsView
import com.axiel7.anihyou.ui.screens.profile.ProfileView
import com.axiel7.anihyou.ui.screens.reviewdetails.ReviewDetailsView
import com.axiel7.anihyou.ui.screens.settings.SettingsView
import com.axiel7.anihyou.ui.screens.settings.TranslationsView
import com.axiel7.anihyou.ui.screens.settings.liststyle.ListStyleSettingsView
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetailsView
import com.axiel7.anihyou.ui.screens.studiodetails.StudioDetailsView
import com.axiel7.anihyou.ui.screens.thread.ThreadDetailsView
import com.axiel7.anihyou.ui.screens.thread.publish.PublishCommentView
import com.axiel7.anihyou.ui.screens.usermedialist.UserMediaListHostView

@Composable
fun MainNavigation(
    navController: NavHostController,
    isCompactScreen: Boolean,
    isLoggedIn: Boolean,
    lastTabOpened: Int,
    homeTab: HomeTab,
    deepLink: DeepLink?,
    padding: PaddingValues = PaddingValues(),
) {
    val bottomPadding by animateDpAsState(
        targetValue = padding.calculateBottomPadding(),
        label = "bottom_bar_padding"
    )

    // common navigation actions
    val navActionManager = rememberNavActionManager(navController)

    LaunchedEffect(deepLink) {
        if (deepLink != null) {
            when (deepLink.type) {
                DeepLink.Type.ANIME, DeepLink.Type.MANGA -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toMediaDetails(it) }
                }

                DeepLink.Type.USER -> {
                    navActionManager.toUserDetails(
                        userId = deepLink.id.toIntOrNull(),
                        username = deepLink.id
                    )
                }

                DeepLink.Type.SEARCH -> navActionManager.toSearch()

                DeepLink.Type.CHARACTER -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toCharacterDetails(it) }
                }

                DeepLink.Type.STAFF -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toStaffDetails(it) }
                }

                DeepLink.Type.STUDIO -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toStudioDetails(it) }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = lastTabOpened.toBottomDestinationRoute()
            ?: NavDestination.HomeTab.route(),
        modifier = Modifier.padding(
            start = padding.calculateStartPadding(LocalLayoutDirection.current),
            top = padding.calculateTopPadding(),
            end = padding.calculateEndPadding(LocalLayoutDirection.current),
        ),
        enterTransition = {
            fadeIn(tween(400))
        },
        exitTransition = {
            fadeOut(tween(400))
        },
        popEnterTransition = {
            fadeIn(tween(400))
        },
        popExitTransition = {
            fadeOut(tween(400))
        }
    ) {
        composable(BottomDestination.Home.route) {
            HomeView(
                isLoggedIn = isLoggedIn,
                defaultHomeTab = homeTab,
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                contentPadding = if (isCompactScreen) PaddingValues(bottom = 16.dp)
                else PaddingValues(bottom = 16.dp + bottomPadding),
                navActionManager = navActionManager,
            )
        }

        composable(
            route = BottomDestination.AnimeList.route,
            arguments = NavDestination.AnimeTab.namedNavArguments
        ) {
            if (isLoggedIn) {
                UserMediaListHostView(
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = BottomDestination.MangaList.route,
            arguments = NavDestination.MangaTab.namedNavArguments
        ) {
            if (isLoggedIn) {
                UserMediaListHostView(
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable(BottomDestination.Profile.route) {
            if (isLoggedIn) {
                ProfileView(
                    modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                    navActionManager = navActionManager,
                )
            } else {
                LoginView(
                    showSettingsButton = true,
                    navigateToSettings = navActionManager::toSettings
                )
            }
        }

        composable(BottomDestination.Explore.route) {
            ExploreView(
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.Search.route(),
            arguments = NavDestination.Search.namedNavArguments
        ) { navEntry ->
            SearchView(
                modifier = Modifier.padding(bottom = bottomPadding),
                initialGenre = navEntry.getStringArg(
                    NavDestination.Search.findDestArgument(NavArgument.Genre)
                ),
                initialTag = navEntry.getStringArg(
                    NavDestination.Search.findDestArgument(NavArgument.Tag)
                ),
                initialFocus = navEntry.getBoolean(
                    NavDestination.Search.findDestArgument(NavArgument.Focus)
                ) == true,
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.UserMediaList.route(),
            arguments = NavDestination.UserMediaList.namedNavArguments
        ) {
            UserMediaListHostView(
                isCompactScreen = isCompactScreen,
                modifier = Modifier.padding(bottom = bottomPadding),
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.Notifications.route(),
            arguments = NavDestination.Notifications.namedNavArguments
        ) {
            if (isLoggedIn) {
                NotificationsView(
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = NavDestination.MediaDetails.route(),
            arguments = NavDestination.MediaDetails.namedNavArguments,
        ) {
            MediaDetailsView(
                isLoggedIn = isLoggedIn,
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.MediaChart.route(),
            arguments = NavDestination.MediaChart.namedNavArguments
        ) {
            MediaChartListView(
                navActionManager = navActionManager,
            )
        }

        composable(
            NavDestination.SeasonAnime.route(),
            arguments = NavDestination.SeasonAnime.namedNavArguments
        ) {
            SeasonAnimeView(
                navActionManager = navActionManager,
            )
        }

        composable(NavDestination.Calendar.route()) {
            CalendarView(
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.UserDetails.route(),
            arguments = NavDestination.UserDetails.namedNavArguments
        ) {
            ProfileView(
                modifier = Modifier.padding(bottom = bottomPadding),
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.CharacterDetails.route(),
            arguments = NavDestination.CharacterDetails.namedNavArguments
        ) {
            CharacterDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.StaffDetails.route(),
            arguments = NavDestination.StaffDetails.namedNavArguments
        ) {
            StaffDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.ReviewDetails.route(),
            arguments = NavDestination.ReviewDetails.namedNavArguments
        ) {
            ReviewDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.ThreadDetails.route(),
            arguments = NavDestination.ThreadDetails.namedNavArguments
        ) { navEntry ->
            navEntry.getIntArg(
                NavDestination.ThreadDetails.findDestArgument(NavArgument.ThreadId)
            )?.let {
                ThreadDetailsView(
                    navActionManager = navActionManager,
                )
            }
        }

        composable(
            route = NavDestination.StudioDetails.route(),
            arguments = NavDestination.StudioDetails.namedNavArguments
        ) {
            StudioDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable(NavDestination.Settings.route()) {
            SettingsView(
                navActionManager = navActionManager,
            )
        }
        composable(NavDestination.ListStyleSettings.route()) {
            ListStyleSettingsView(
                navActionManager = navActionManager,
            )
        }
        composable(NavDestination.Translations.route()) {
            TranslationsView(
                navActionManager = navActionManager,
            )
        }

        composable(
            route = NavDestination.FullscreenImage.route(),
            arguments = NavDestination.FullscreenImage.namedNavArguments
        ) { navEntry ->
            FullScreenImageView(
                imageUrl = navEntry.getStringArg(
                    NavDestination.FullscreenImage.findDestArgument(NavArgument.Url)
                ),
                onDismiss = navActionManager::goBack
            )
        }

        composable(
            route = NavDestination.ActivityDetails.route(),
            arguments = NavDestination.ActivityDetails.namedNavArguments
        ) { navEntry ->
            navEntry.getIntArg(
                NavDestination.ActivityDetails.findDestArgument(NavArgument.ActivityId)
            )?.let {
                ActivityDetailsView(
                    navActionManager = navActionManager,
                )
            }
        }

        composable(
            route = NavDestination.PublishActivity.route(),
            arguments = NavDestination.PublishActivity.namedNavArguments
        ) { navEntry ->
            if (isLoggedIn) {
                val id = navEntry.getIntArg(
                    NavDestination.PublishActivity.findDestArgument(NavArgument.ActivityId)
                )
                PublishActivityView(
                    id = if (id != 0) id else null,
                    text = navEntry.getStringArg(
                        NavDestination.PublishActivity.findDestArgument(NavArgument.Text)
                    ),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = NavDestination.PublishActivityReply.route(),
            arguments = NavDestination.PublishActivityReply.namedNavArguments
        ) { navEntry ->
            if (isLoggedIn) {
                val id = navEntry.getIntArg(
                    NavDestination.PublishActivityReply.findDestArgument(NavArgument.ReplyId)
                )
                PublishActivityView(
                    activityId = navEntry.getIntArg(
                        NavDestination.PublishActivityReply.findDestArgument(NavArgument.ActivityId)
                    ),
                    id = if (id != 0) id else null,
                    text = navEntry.getStringArg(
                        NavDestination.PublishActivityReply.findDestArgument(NavArgument.Text)
                    ),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = NavDestination.PublishThreadComment.route(),
            arguments = NavDestination.PublishThreadComment.namedNavArguments
        ) { navEntry ->
            if (isLoggedIn) {
                val commentId = navEntry.getIntArg(
                    NavDestination.PublishThreadComment.findDestArgument(NavArgument.CommentId)
                )
                PublishCommentView(
                    threadId = navEntry.getIntArg(
                        NavDestination.PublishThreadComment.findDestArgument(NavArgument.ThreadId)
                    ),
                    parentCommentId = null,
                    id = commentId,
                    text = navEntry.getStringArg(
                        NavDestination.PublishThreadComment.findDestArgument(NavArgument.Text)
                    ),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable(
            route = NavDestination.PublishCommentReply.route(),
            arguments = NavDestination.PublishCommentReply.namedNavArguments
        ) { navEntry ->
            if (isLoggedIn) {
                val commentId = navEntry.getIntArg(
                    NavDestination.PublishCommentReply.findDestArgument(NavArgument.CommentId)
                )
                PublishCommentView(
                    threadId = null,
                    parentCommentId = navEntry.getIntArg(
                        NavDestination.PublishCommentReply.findDestArgument(NavArgument.ParentCommentId)
                    ),
                    id = commentId,
                    text = navEntry.getStringArg(
                        NavDestination.PublishCommentReply.findDestArgument(NavArgument.Text)
                    ),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }
    }
}