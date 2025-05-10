package com.axiel7.anihyou.ui.screens.main

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
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
import androidx.navigation.toRoute
import com.axiel7.anihyou.core.model.DeepLink
import com.axiel7.anihyou.core.model.HomeTab
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.ui.common.BottomDestination
import com.axiel7.anihyou.core.ui.common.BottomDestination.Companion.toBottomDestinationRoute
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.composables.FullScreenImageView
import com.axiel7.anihyou.feature.activitydetails.ActivityDetailsView
import com.axiel7.anihyou.feature.activitydetails.publish.PublishActivityView
import com.axiel7.anihyou.feature.calendar.CalendarView
import com.axiel7.anihyou.feature.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.feature.explore.ExploreView
import com.axiel7.anihyou.feature.explore.charts.MediaChartListView
import com.axiel7.anihyou.feature.explore.search.SearchView
import com.axiel7.anihyou.feature.explore.season.SeasonAnimeView
import com.axiel7.anihyou.feature.home.HomeView
import com.axiel7.anihyou.feature.home.current.fulllist.CurrentFullListView
import com.axiel7.anihyou.feature.login.LoginView
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsView
import com.axiel7.anihyou.feature.mediadetails.activity.MediaActivityView
import com.axiel7.anihyou.feature.notifications.NotificationsView
import com.axiel7.anihyou.feature.profile.ProfileView
import com.axiel7.anihyou.feature.reviewdetails.ReviewDetailsView
import com.axiel7.anihyou.feature.settings.SettingsView
import com.axiel7.anihyou.feature.settings.TranslationsView
import com.axiel7.anihyou.feature.settings.customlists.CustomListsView
import com.axiel7.anihyou.feature.settings.liststyle.ListStyleSettingsView
import com.axiel7.anihyou.feature.staffdetails.StaffDetailsView
import com.axiel7.anihyou.feature.studiodetails.StudioDetailsView
import com.axiel7.anihyou.feature.thread.ThreadDetailsView
import com.axiel7.anihyou.feature.thread.publish.PublishCommentView
import com.axiel7.anihyou.feature.usermedialist.UserMediaListHostView

@Composable
fun MainNavigation(
    navController: NavHostController,
    navActionManager: NavActionManager,
    isCompactScreen: Boolean,
    isLoggedIn: Boolean,
    tabToOpen: Int,
    homeTab: HomeTab,
    deepLink: DeepLink?,
    padding: PaddingValues = PaddingValues(),
) {
    val bottomPadding by animateDpAsState(
        targetValue = padding.calculateBottomPadding(),
        label = "bottom_bar_padding"
    )

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

                DeepLink.Type.THREAD -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toThreadDetails(it) }
                }

                DeepLink.Type.ACTIVITY -> {
                    deepLink.id.toIntOrNull()?.let { navActionManager.toActivityDetails(it) }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = tabToOpen.toBottomDestinationRoute() ?: BottomDestination.Home,
        modifier = Modifier.padding(
            start = padding.calculateStartPadding(LocalLayoutDirection.current),
            top = padding.calculateTopPadding(),
            end = padding.calculateEndPadding(LocalLayoutDirection.current),
        ),
        enterTransition = {
            fadeIn(
                animationSpec = tween(220, easing = LinearEasing)
            ) + slideIntoContainer(
                animationSpec = tween(220, easing = EaseIn),
                towards = AnimatedContentTransitionScope.SlideDirection.Start
            )
        },
        exitTransition = {
            fadeOut(
                animationSpec = tween(280, easing = LinearEasing)
            ) + slideOutOfContainer(
                animationSpec = tween(280, easing = EaseOut),
                towards = AnimatedContentTransitionScope.SlideDirection.End
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(220, easing = LinearEasing)
            )
        },
    ) {
        composable<Routes.Home>(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
            HomeView(
                isLoggedIn = isLoggedIn,
                defaultHomeTab = homeTab,
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                contentPadding = if (isCompactScreen) PaddingValues(bottom = 16.dp)
                else PaddingValues(bottom = 16.dp + bottomPadding),
                navActionManager = navActionManager,
            )
        }

        composable<Routes.AnimeTab>(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
            if (isLoggedIn) {
                UserMediaListHostView(
                    mediaType = MediaType.ANIME,
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable<Routes.MangaTab>(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
            if (isLoggedIn) {
                UserMediaListHostView(
                    mediaType = MediaType.MANGA,
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable<Routes.Profile>(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
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

        composable<Routes.Explore>(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
            ExploreView(
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                navActionManager = navActionManager,
            )
        }

        composable<Routes.UserDetails> {
            ProfileView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.UserMediaList> {
            val arguments = it.toRoute<Routes.UserMediaList>()
            UserMediaListHostView(
                mediaType = MediaType.safeValueOf(arguments.mediaType),
                isCompactScreen = isCompactScreen,
                modifier = Modifier.padding(bottom = bottomPadding),
                navActionManager = navActionManager,
            )
        }

        composable<Routes.Search> {
            SearchView(
                arguments = it.toRoute(),
                modifier = Modifier.padding(bottom = bottomPadding),
                navActionManager = navActionManager,
            )
        }

        composable<Routes.Notifications> {
            if (isLoggedIn) {
                NotificationsView(
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable<Routes.MediaDetails> {
            MediaDetailsView(
                isLoggedIn = isLoggedIn,
                navActionManager = navActionManager,
            )
        }

        composable<Routes.MediaChartList> {
            MediaChartListView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.SeasonAnime> {
            SeasonAnimeView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.Calendar> {
            CalendarView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.CharacterDetails> {
            CharacterDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.StaffDetails> {
            StaffDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.ReviewDetails> {
            ReviewDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.ThreadDetails> {
            ThreadDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.StudioDetails> {
            StudioDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.Settings> {
            SettingsView(
                navActionManager = navActionManager,
            )
        }
        composable<Routes.ListStyleSettings> {
            ListStyleSettingsView(
                navActionManager = navActionManager,
            )
        }
        composable<Routes.CustomLists> {
            CustomListsView(
                navActionManager = navActionManager,
            )
        }
        composable<Routes.Translations> {
            TranslationsView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.FullScreenImage>(
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() },
        ) {
            FullScreenImageView(
                arguments = it.toRoute(),
                onDismiss = navActionManager::goBack
            )
        }

        composable<Routes.ActivityDetails> {
            ActivityDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<Routes.PublishActivity> {
            if (isLoggedIn) {
                PublishActivityView(
                    arguments = it.toRoute(),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable<Routes.PublishComment> {
            if (isLoggedIn) {
                PublishCommentView(
                    arguments = it.toRoute(),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable<Routes.MediaActivity> {
            MediaActivityView(
                navActionManager = navActionManager
            )
        }

        composable<Routes.CurrentFullList> {
            CurrentFullListView(
                listType = it.toRoute<Routes.CurrentFullList>().listType,
                navActionManager = navActionManager,
            )
        }
    }
}