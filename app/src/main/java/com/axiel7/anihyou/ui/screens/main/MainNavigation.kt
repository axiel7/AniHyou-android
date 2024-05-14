package com.axiel7.anihyou.ui.screens.main

import androidx.compose.animation.AnimatedContentTransitionScope
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
import com.axiel7.anihyou.data.model.DeepLink
import com.axiel7.anihyou.ui.common.BottomDestination.Companion.toBottomDestinationRoute
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.FullScreenImage
import com.axiel7.anihyou.ui.composables.FullScreenImageView
import com.axiel7.anihyou.ui.screens.activitydetails.ActivityDetails
import com.axiel7.anihyou.ui.screens.activitydetails.ActivityDetailsView
import com.axiel7.anihyou.ui.screens.activitydetails.publish.PublishActivity
import com.axiel7.anihyou.ui.screens.activitydetails.publish.PublishActivityView
import com.axiel7.anihyou.ui.screens.calendar.Calendar
import com.axiel7.anihyou.ui.screens.calendar.CalendarView
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetails
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.ui.screens.explore.Explore
import com.axiel7.anihyou.ui.screens.explore.ExploreView
import com.axiel7.anihyou.ui.screens.explore.charts.MediaChartList
import com.axiel7.anihyou.ui.screens.explore.charts.MediaChartListView
import com.axiel7.anihyou.ui.screens.explore.search.Search
import com.axiel7.anihyou.ui.screens.explore.search.SearchView
import com.axiel7.anihyou.ui.screens.explore.season.SeasonAnime
import com.axiel7.anihyou.ui.screens.explore.season.SeasonAnimeView
import com.axiel7.anihyou.ui.screens.home.Home
import com.axiel7.anihyou.ui.screens.home.HomeTab
import com.axiel7.anihyou.ui.screens.home.HomeView
import com.axiel7.anihyou.ui.screens.login.LoginView
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetails
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetailsView
import com.axiel7.anihyou.ui.screens.notifications.Notifications
import com.axiel7.anihyou.ui.screens.notifications.NotificationsView
import com.axiel7.anihyou.ui.screens.profile.Profile
import com.axiel7.anihyou.ui.screens.profile.ProfileView
import com.axiel7.anihyou.ui.screens.reviewdetails.ReviewDetails
import com.axiel7.anihyou.ui.screens.reviewdetails.ReviewDetailsView
import com.axiel7.anihyou.ui.screens.settings.Settings
import com.axiel7.anihyou.ui.screens.settings.SettingsView
import com.axiel7.anihyou.ui.screens.settings.Translations
import com.axiel7.anihyou.ui.screens.settings.TranslationsView
import com.axiel7.anihyou.ui.screens.settings.liststyle.ListStyleSettings
import com.axiel7.anihyou.ui.screens.settings.liststyle.ListStyleSettingsView
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetails
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetailsView
import com.axiel7.anihyou.ui.screens.studiodetails.StudioDetails
import com.axiel7.anihyou.ui.screens.studiodetails.StudioDetailsView
import com.axiel7.anihyou.ui.screens.thread.ThreadDetails
import com.axiel7.anihyou.ui.screens.thread.ThreadDetailsView
import com.axiel7.anihyou.ui.screens.thread.publish.PublishComment
import com.axiel7.anihyou.ui.screens.thread.publish.PublishCommentView
import com.axiel7.anihyou.ui.screens.usermedialist.UserMediaList
import com.axiel7.anihyou.ui.screens.usermedialist.UserMediaListHostView

@Composable
fun MainNavigation(
    navController: NavHostController,
    navActionManager: NavActionManager,
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
        startDestination = lastTabOpened.toBottomDestinationRoute() ?: Home,
        modifier = Modifier.padding(
            start = padding.calculateStartPadding(LocalLayoutDirection.current),
            top = padding.calculateTopPadding(),
            end = padding.calculateEndPadding(LocalLayoutDirection.current),
        ),
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(350)
            )
        },
        popEnterTransition = {
            fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(350)
            )
        }
    ) {
        composable<Home>(
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) },
            popEnterTransition = { fadeIn(tween(400)) },
            popExitTransition = { fadeOut(tween(400)) },
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

        composable<UserMediaList>(
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) },
            popEnterTransition = { fadeIn(tween(400)) },
            popExitTransition = { fadeOut(tween(400)) },
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

        composable<Profile>(
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) },
            popEnterTransition = { fadeIn(tween(400)) },
            popExitTransition = { fadeOut(tween(400)) },
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

        composable<Explore>(
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) },
            popEnterTransition = { fadeIn(tween(400)) },
            popExitTransition = { fadeOut(tween(400)) },
        ) {
            ExploreView(
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                navActionManager = navActionManager,
            )
        }

        composable<Search> {
            SearchView(
                arguments = it.toRoute(),
                modifier = Modifier.padding(bottom = bottomPadding),
                navActionManager = navActionManager,
            )
        }

        composable<Notifications> {
            if (isLoggedIn) {
                NotificationsView(
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable<MediaDetails> {
            MediaDetailsView(
                isLoggedIn = isLoggedIn,
                navActionManager = navActionManager,
            )
        }

        composable<MediaChartList> {
            MediaChartListView(
                navActionManager = navActionManager,
            )
        }

        composable<SeasonAnime> {
            SeasonAnimeView(
                navActionManager = navActionManager,
            )
        }

        composable<Calendar> {
            CalendarView(
                navActionManager = navActionManager,
            )
        }

        composable<CharacterDetails> {
            CharacterDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<StaffDetails> {
            StaffDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<ReviewDetails> {
            ReviewDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<ThreadDetails> {
            ThreadDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<StudioDetails> {
            StudioDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<Settings> {
            SettingsView(
                navActionManager = navActionManager,
            )
        }
        composable<ListStyleSettings> {
            ListStyleSettingsView(
                navActionManager = navActionManager,
            )
        }
        composable<Translations> {
            TranslationsView(
                navActionManager = navActionManager,
            )
        }

        composable<FullScreenImage>(
            enterTransition = { fadeIn(tween(400)) },
            exitTransition = { fadeOut(tween(400)) },
            popEnterTransition = { fadeIn(tween(400)) },
            popExitTransition = { fadeOut(tween(400)) },
        ) {
            FullScreenImageView(
                arguments = it.toRoute(),
                onDismiss = navActionManager::goBack
            )
        }

        composable<ActivityDetails> {
            ActivityDetailsView(
                navActionManager = navActionManager,
            )
        }

        composable<PublishActivity> {
            if (isLoggedIn) {
                PublishActivityView(
                    arguments = it.toRoute(),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }

        composable<PublishComment> {
            if (isLoggedIn) {
                PublishCommentView(
                    arguments = it.toRoute(),
                    navActionManager = navActionManager,
                )
            } else {
                LoginView()
            }
        }
    }
}