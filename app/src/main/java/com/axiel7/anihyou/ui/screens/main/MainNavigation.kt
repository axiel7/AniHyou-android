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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.axiel7.anihyou.App
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.DeepLink
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BottomDestination
import com.axiel7.anihyou.ui.base.BottomDestination.Companion.toBottomDestinationRoute
import com.axiel7.anihyou.ui.composables.FULLSCREEN_IMAGE_DESTINATION
import com.axiel7.anihyou.ui.composables.FullScreenImageView
import com.axiel7.anihyou.ui.composables.URL_ARGUMENT
import com.axiel7.anihyou.ui.screens.calendar.CALENDAR_DESTINATION
import com.axiel7.anihyou.ui.screens.calendar.CalendarView
import com.axiel7.anihyou.ui.screens.characterdetails.CHARACTER_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.characterdetails.CHARACTER_ID_ARGUMENT
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.ui.screens.explore.EXPLORE_GENRE_DESTINATION
import com.axiel7.anihyou.ui.screens.explore.ExploreView
import com.axiel7.anihyou.ui.screens.explore.GENRE_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.MEDIA_SORT_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.MEDIA_TYPE_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.OPEN_SEARCH_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.TAG_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.charts.CHART_TYPE_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.charts.MEDIA_CHART_DESTINATION
import com.axiel7.anihyou.ui.screens.explore.charts.MediaChartListView
import com.axiel7.anihyou.ui.screens.explore.charts.SEASON_ANIME_DESTINATION
import com.axiel7.anihyou.ui.screens.explore.charts.SEASON_ARGUMENT
import com.axiel7.anihyou.ui.screens.explore.charts.SeasonAnimeView
import com.axiel7.anihyou.ui.screens.explore.charts.YEAR_ARGUMENT
import com.axiel7.anihyou.ui.screens.home.HomeView
import com.axiel7.anihyou.ui.screens.login.LoginView
import com.axiel7.anihyou.ui.screens.mediadetails.MEDIA_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.mediadetails.MEDIA_ID_ARGUMENT
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetailsView
import com.axiel7.anihyou.ui.screens.notifications.NOTIFICATIONS_DESTINATION
import com.axiel7.anihyou.ui.screens.notifications.NotificationsView
import com.axiel7.anihyou.ui.screens.profile.ProfileView
import com.axiel7.anihyou.ui.screens.profile.USER_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.profile.USER_ID_ARGUMENT
import com.axiel7.anihyou.ui.screens.profile.USER_NAME_ARGUMENT
import com.axiel7.anihyou.ui.screens.reviewdetails.REVIEW_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.reviewdetails.REVIEW_ID_ARGUMENT
import com.axiel7.anihyou.ui.screens.reviewdetails.ReviewDetailsView
import com.axiel7.anihyou.ui.screens.settings.LIST_STYLE_SETTINGS_DESTINATION
import com.axiel7.anihyou.ui.screens.settings.ListStyleSettingsView
import com.axiel7.anihyou.ui.screens.settings.SETTINGS_DESTINATION
import com.axiel7.anihyou.ui.screens.settings.SettingsView
import com.axiel7.anihyou.ui.screens.staffdetails.STAFF_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.staffdetails.STAFF_ID_ARGUMENT
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetailsView
import com.axiel7.anihyou.ui.screens.studiodetails.STUDIO_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.studiodetails.STUDIO_ID_ARGUMENT
import com.axiel7.anihyou.ui.screens.studiodetails.StudioDetailsView
import com.axiel7.anihyou.ui.screens.thread.THREAD_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.thread.THREAD_ID_ARGUMENT
import com.axiel7.anihyou.ui.screens.thread.ThreadDetailsView
import com.axiel7.anihyou.ui.screens.usermedialist.USER_MEDIA_LIST_DESTINATION
import com.axiel7.anihyou.ui.screens.usermedialist.UserMediaListHostView
import com.axiel7.anihyou.utils.StringUtils.removeFirstAndLast
import com.axiel7.anihyou.utils.UTF_8
import java.net.URLEncoder

@Composable
fun MainNavigation(
    navController: NavHostController,
    isCompactScreen: Boolean,
    lastTabOpened: Int,
    deepLink: DeepLink?,
    padding: PaddingValues = PaddingValues(),
) {
    val accessTokenPreference by rememberPreference(ACCESS_TOKEN_PREFERENCE_KEY, App.accessToken)
    val bottomPadding by animateDpAsState(
        targetValue = padding.calculateBottomPadding(),
        label = "bottom_bar_padding"
    )

    LaunchedEffect(deepLink) {
        if (deepLink != null) {
            when (deepLink.type) {
                DeepLink.Type.ANIME, DeepLink.Type.MANGA -> {
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace(MEDIA_ID_ARGUMENT, deepLink.id)
                    )
                }

                DeepLink.Type.USER -> {
                    val userId = deepLink.id.toIntOrNull()
                    var dest = USER_DETAILS_DESTINATION
                    dest = if (userId != null) dest.replace(USER_ID_ARGUMENT, userId.toString())
                    else dest.replace(USER_NAME_ARGUMENT, deepLink.id)
                    navController.navigate(dest)
                }

                DeepLink.Type.SEARCH -> {
                    navController.navigate(
                        EXPLORE_GENRE_DESTINATION
                            .replace(OPEN_SEARCH_ARGUMENT, deepLink.id)
                    )
                }

                else -> {
                    navController.navigate("${deepLink.type.name.lowercase()}/${deepLink.id}")
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = lastTabOpened.toBottomDestinationRoute() ?: BottomDestination.Home.route,
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
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                contentPadding = if (isCompactScreen) PaddingValues(bottom = 16.dp)
                else PaddingValues(bottom = 16.dp + bottomPadding),
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace(MEDIA_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToAnimeSeason = { animeSeason ->
                    navController.navigate(
                        SEASON_ANIME_DESTINATION
                            .replace(YEAR_ARGUMENT, animeSeason.year.toString())
                            .replace(SEASON_ARGUMENT, animeSeason.season.name)
                    )
                },
                navigateToCalendar = {
                    navController.navigate(CALENDAR_DESTINATION)
                },
                navigateToExplore = { mediaType, mediaSort ->
                    navController.navigate(
                        EXPLORE_GENRE_DESTINATION
                            .replace(MEDIA_TYPE_ARGUMENT, mediaType.rawValue)
                            .replace(MEDIA_SORT_ARGUMENT, mediaSort.rawValue)
                    )
                },
                navigateToNotifications = {
                    navController.navigate(NOTIFICATIONS_DESTINATION)
                }
            )
        }

        composable(BottomDestination.AnimeList.route) {
            if (accessTokenPreference == null) {
                LoginView(
                    modifier = Modifier.padding(bottom = bottomPadding),
                )
            } else {
                UserMediaListHostView(
                    mediaType = MediaType.ANIME,
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace(MEDIA_ID_ARGUMENT, id.toString())
                        )
                    }
                )
            }
        }

        composable(BottomDestination.MangaList.route) {
            if (accessTokenPreference == null) {
                LoginView(
                    modifier = Modifier.padding(bottom = bottomPadding),
                )
            } else {
                UserMediaListHostView(
                    mediaType = MediaType.MANGA,
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace(MEDIA_ID_ARGUMENT, id.toString())
                        )
                    }
                )
            }
        }

        composable(BottomDestination.Profile.route) {
            if (accessTokenPreference == null) {
                LoginView(
                    modifier = Modifier.padding(bottom = bottomPadding),
                )
            } else {
                ProfileView(
                    modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                    navigateToSettings = {
                        navController.navigate(SETTINGS_DESTINATION)
                    },
                    navigateToFullscreenImage = { url ->
                        val encodedUrl = URLEncoder.encode(url, UTF_8)
                        navController.navigate(
                            FULLSCREEN_IMAGE_DESTINATION
                                .replace(URL_ARGUMENT, encodedUrl)
                        )
                    },
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace(MEDIA_ID_ARGUMENT, id.toString())
                        )
                    },
                    navigateToCharacterDetails = { id ->
                        navController.navigate(
                            CHARACTER_DETAILS_DESTINATION
                                .replace(CHARACTER_ID_ARGUMENT, id.toString())
                        )
                    },
                    navigateToStaffDetails = { id ->
                        navController.navigate(
                            STAFF_DETAILS_DESTINATION
                                .replace(STAFF_ID_ARGUMENT, id.toString())
                        )
                    },
                    navigateToStudioDetails = { id ->
                        navController.navigate(
                            STUDIO_DETAILS_DESTINATION
                                .replace(STUDIO_ID_ARGUMENT, id.toString())
                        )
                    },
                    navigateToUserDetails = { id ->
                        navController.navigate(
                            USER_DETAILS_DESTINATION
                                .replace(USER_ID_ARGUMENT, id.toString())
                        )
                    },
                    navigateToUserMediaList = null
                )
            }
        }

        composable(BottomDestination.Explore.route) {
            ExploreView(
                modifier = if (isCompactScreen) Modifier.padding(bottom = bottomPadding) else Modifier,
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace(MEDIA_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToUserDetails = { id ->
                    navController.navigate(
                        USER_DETAILS_DESTINATION
                            .replace(USER_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToCharacterDetails = { id ->
                    navController.navigate(
                        CHARACTER_DETAILS_DESTINATION
                            .replace(CHARACTER_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToStaffDetails = { id ->
                    navController.navigate(
                        STAFF_DETAILS_DESTINATION
                            .replace(STAFF_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToStudioDetails = { id ->
                    navController.navigate(
                        STUDIO_DETAILS_DESTINATION
                            .replace(STUDIO_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToMediaChart = { type ->
                    navController.navigate(
                        MEDIA_CHART_DESTINATION
                            .replace(CHART_TYPE_ARGUMENT, type.name)
                    )
                },
                navigateToAnimeSeason = { year, season ->
                    navController.navigate(
                        SEASON_ANIME_DESTINATION
                            .replace(YEAR_ARGUMENT, year.toString())
                            .replace(SEASON_ARGUMENT, season)
                    )
                },
                navigateToCalendar = {
                    navController.navigate(CALENDAR_DESTINATION)
                }
            )
        }

        composable(EXPLORE_GENRE_DESTINATION,
            arguments = listOf(
                navArgument(MEDIA_TYPE_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(MEDIA_SORT_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(GENRE_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(TAG_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(OPEN_SEARCH_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { navEntry ->
            ExploreView(
                modifier = Modifier.padding(bottom = bottomPadding),
                initialMediaType = navEntry.arguments?.getString(MEDIA_TYPE_ARGUMENT.removeFirstAndLast())
                    ?.let { MediaType.safeValueOf(it) },
                initialMediaSort = navEntry.arguments?.getString(MEDIA_SORT_ARGUMENT.removeFirstAndLast())
                    ?.let { MediaSort.valueOf(it) },
                initialGenre = navEntry.arguments?.getString(GENRE_ARGUMENT.removeFirstAndLast()),
                initialTag = navEntry.arguments?.getString(TAG_ARGUMENT.removeFirstAndLast()),
                openSearch = navEntry.arguments?.getString(OPEN_SEARCH_ARGUMENT.removeFirstAndLast()) == "true",
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace(MEDIA_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToUserDetails = { id ->
                    navController.navigate(
                        USER_DETAILS_DESTINATION
                            .replace(USER_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToCharacterDetails = { id ->
                    navController.navigate(
                        CHARACTER_DETAILS_DESTINATION
                            .replace(CHARACTER_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToStaffDetails = { id ->
                    navController.navigate(
                        STAFF_DETAILS_DESTINATION
                            .replace(STAFF_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToStudioDetails = { id ->
                    navController.navigate(
                        STUDIO_DETAILS_DESTINATION
                            .replace(STUDIO_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToMediaChart = { type ->
                    navController.navigate(
                        MEDIA_CHART_DESTINATION
                            .replace(MEDIA_TYPE_ARGUMENT, type.name)
                    )
                },
                navigateToAnimeSeason = { year, season ->
                    navController.navigate(
                        SEASON_ANIME_DESTINATION
                            .replace(YEAR_ARGUMENT, year.toString())
                            .replace(SEASON_ARGUMENT, season)
                    )
                },
                navigateToCalendar = {
                    navController.navigate(CALENDAR_DESTINATION)
                }
            )
        }

        composable(
            USER_MEDIA_LIST_DESTINATION,
            arguments = listOf(
                navArgument(MEDIA_TYPE_ARGUMENT.removeFirstAndLast()) { type = NavType.StringType },
                navArgument(USER_ID_ARGUMENT.removeFirstAndLast()) { type = NavType.IntType }
            )
        ) { navEntry ->
            UserMediaListHostView(
                mediaType = navEntry.arguments?.getString(MEDIA_TYPE_ARGUMENT.removeFirstAndLast())
                    ?.let { MediaType.safeValueOf(it) }!!,
                modifier = Modifier.padding(bottom = bottomPadding),
                userId = navEntry.arguments?.getInt(USER_ID_ARGUMENT.removeFirstAndLast()),
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace(MEDIA_ID_ARGUMENT, id.toString())
                    )
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NOTIFICATIONS_DESTINATION) {
            NotificationsView(
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace(MEDIA_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToUserDetails = { id ->
                    navController.navigate(
                        USER_DETAILS_DESTINATION
                            .replace(USER_ID_ARGUMENT, id.toString())
                    )
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            MEDIA_DETAILS_DESTINATION,
            arguments = listOf(
                navArgument(MEDIA_ID_ARGUMENT.removeFirstAndLast()) { type = NavType.IntType }
            ),
        ) { navEntry ->
            MediaDetailsView(
                mediaId = navEntry.arguments?.getInt(MEDIA_ID_ARGUMENT.removeFirstAndLast()) ?: 0,
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace(MEDIA_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToFullscreenImage = { url ->
                    val encodedUrl = URLEncoder.encode(url, UTF_8)
                    navController.navigate(
                        FULLSCREEN_IMAGE_DESTINATION
                            .replace(URL_ARGUMENT, encodedUrl)
                    )
                },
                navigateToCharacterDetails = { id ->
                    navController.navigate(
                        CHARACTER_DETAILS_DESTINATION
                            .replace(CHARACTER_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToStaffDetails = { id ->
                    navController.navigate(
                        STAFF_DETAILS_DESTINATION
                            .replace(STAFF_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToReviewDetails = { id ->
                    navController.navigate(
                        REVIEW_DETAILS_DESTINATION
                            .replace(REVIEW_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToThreadDetails = { id ->
                    navController.navigate(
                        THREAD_DETAILS_DESTINATION
                            .replace(THREAD_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToExplore = { mediaType, genre, tag ->
                    var dest = EXPLORE_GENRE_DESTINATION
                    if (mediaType != null) dest =
                        dest.replace(MEDIA_TYPE_ARGUMENT, mediaType.rawValue)
                    if (genre != null) dest = dest.replace(GENRE_ARGUMENT, genre)
                    if (tag != null) dest = dest.replace(TAG_ARGUMENT, tag)
                    navController.navigate(dest)
                }
            )
        }

        composable(
            MEDIA_CHART_DESTINATION,
            arguments = listOf(
                navArgument(CHART_TYPE_ARGUMENT.removeFirstAndLast()) { type = NavType.StringType }
            )
        ) { navEntry ->
            navEntry.arguments?.getString(CHART_TYPE_ARGUMENT.removeFirstAndLast())?.let {
                MediaChartListView(
                    type = ChartType.valueOf(it),
                    navigateBack = {
                        navController.popBackStack()
                    },
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace(MEDIA_ID_ARGUMENT, id.toString())
                        )
                    }
                )
            }
        }

        composable(
            SEASON_ANIME_DESTINATION,
            arguments = listOf(
                navArgument(SEASON_ARGUMENT.removeFirstAndLast()) { type = NavType.StringType },
                navArgument(YEAR_ARGUMENT.removeFirstAndLast()) { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getString(SEASON_ARGUMENT.removeFirstAndLast())?.let { season ->
                navEntry.arguments?.getInt(YEAR_ARGUMENT.removeFirstAndLast())?.let { year ->
                    SeasonAnimeView(
                        initialSeason = AnimeSeason(
                            year = year,
                            season = MediaSeason.valueOf(season)
                        ),
                        navigateBack = {
                            navController.popBackStack()
                        },
                        navigateToMediaDetails = { id ->
                            navController.navigate(
                                MEDIA_DETAILS_DESTINATION
                                    .replace(MEDIA_ID_ARGUMENT, id.toString())
                            )
                        }
                    )
                }
            }
        }

        composable(CALENDAR_DESTINATION) {
            CalendarView(
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace(MEDIA_ID_ARGUMENT, id.toString())
                    )
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            USER_DETAILS_DESTINATION,
            arguments = listOf(
                navArgument(USER_ID_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument(USER_NAME_ARGUMENT.removeFirstAndLast()) {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { navEntry ->
            ProfileView(
                modifier = Modifier.padding(bottom = bottomPadding),
                userId = navEntry.arguments?.getString(USER_ID_ARGUMENT.removeFirstAndLast())
                    ?.toIntOrNull(),
                username = navEntry.arguments?.getString(USER_NAME_ARGUMENT.removeFirstAndLast()),
                navigateToFullscreenImage = { url ->
                    val encodedUrl = URLEncoder.encode(url, UTF_8)
                    navController.navigate(
                        FULLSCREEN_IMAGE_DESTINATION
                            .replace(URL_ARGUMENT, encodedUrl)
                    )
                },
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace(MEDIA_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToCharacterDetails = { id ->
                    navController.navigate(
                        CHARACTER_DETAILS_DESTINATION
                            .replace(CHARACTER_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToStaffDetails = { id ->
                    navController.navigate(
                        STAFF_DETAILS_DESTINATION
                            .replace(STAFF_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToStudioDetails = { id ->
                    navController.navigate(
                        STUDIO_DETAILS_DESTINATION
                            .replace(STUDIO_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToUserDetails = { id ->
                    navController.navigate(
                        USER_DETAILS_DESTINATION
                            .replace(USER_ID_ARGUMENT, id.toString())
                    )
                },
                navigateToUserMediaList = { mediaType, userId ->
                    navController.navigate(
                        USER_MEDIA_LIST_DESTINATION
                            .replace(USER_ID_ARGUMENT, userId.toString())
                            .replace(MEDIA_TYPE_ARGUMENT, mediaType.rawValue)
                    )
                },
                navigateBack = {
                    navController.popBackStack()
                },
            )
        }

        composable(
            CHARACTER_DETAILS_DESTINATION,
            arguments = listOf(
                navArgument(CHARACTER_ID_ARGUMENT.removeFirstAndLast()) { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt(CHARACTER_ID_ARGUMENT.removeFirstAndLast())
                ?.let { characterId ->
                    CharacterDetailsView(
                        characterId = characterId,
                        navigateBack = {
                            navController.popBackStack()
                        },
                        navigateToMediaDetails = { id ->
                            navController.navigate(
                                MEDIA_DETAILS_DESTINATION
                                    .replace(MEDIA_ID_ARGUMENT, id.toString())
                            )
                        },
                        navigateToFullscreenImage = { url ->
                            val encodedUrl = URLEncoder.encode(url, UTF_8)
                            navController.navigate(
                                FULLSCREEN_IMAGE_DESTINATION
                                    .replace(URL_ARGUMENT, encodedUrl)
                            )
                        },
                    )
                }
        }

        composable(
            STAFF_DETAILS_DESTINATION,
            arguments = listOf(
                navArgument(STAFF_ID_ARGUMENT.removeFirstAndLast()) { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt(STAFF_ID_ARGUMENT.removeFirstAndLast())?.let { staffId ->
                StaffDetailsView(
                    staffId = staffId,
                    navigateBack = {
                        navController.popBackStack()
                    },
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace(MEDIA_ID_ARGUMENT, id.toString())
                        )
                    },
                    navigateToCharacterDetails = { id ->
                        navController.navigate(
                            CHARACTER_DETAILS_DESTINATION
                                .replace(CHARACTER_ID_ARGUMENT, id.toString())
                        )
                    },
                    navigateToFullscreenImage = { url ->
                        val encodedUrl = URLEncoder.encode(url, UTF_8)
                        navController.navigate(
                            FULLSCREEN_IMAGE_DESTINATION
                                .replace(URL_ARGUMENT, encodedUrl)
                        )
                    }
                )
            }
        }

        composable(
            REVIEW_DETAILS_DESTINATION,
            arguments = listOf(
                navArgument(REVIEW_ID_ARGUMENT.removeFirstAndLast()) { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt(REVIEW_ID_ARGUMENT.removeFirstAndLast())?.let {
                ReviewDetailsView(
                    reviewId = it,
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            THREAD_DETAILS_DESTINATION,
            arguments = listOf(
                navArgument(THREAD_ID_ARGUMENT.removeFirstAndLast()) { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt(THREAD_ID_ARGUMENT.removeFirstAndLast())?.let {
                ThreadDetailsView(
                    threadId = it,
                    navigateToUserDetails = { id ->
                        navController.navigate(
                            USER_DETAILS_DESTINATION
                                .replace(USER_ID_ARGUMENT, id.toString())
                        )
                    },
                    navigateToFullscreenImage = { url ->
                        val encodedUrl = URLEncoder.encode(url, UTF_8)
                        navController.navigate(
                            FULLSCREEN_IMAGE_DESTINATION
                                .replace(URL_ARGUMENT, encodedUrl)
                        )
                    },
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            STUDIO_DETAILS_DESTINATION,
            arguments = listOf(
                navArgument(STUDIO_ID_ARGUMENT.removeFirstAndLast()) { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt(STUDIO_ID_ARGUMENT.removeFirstAndLast())?.let {
                StudioDetailsView(
                    studioId = it,
                    navigateBack = {
                        navController.popBackStack()
                    },
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace(MEDIA_ID_ARGUMENT, id.toString())
                        )
                    }
                )
            }
        }

        composable(SETTINGS_DESTINATION) {
            SettingsView(
                navigateToListStyleSettings = {
                    navController.navigate(LIST_STYLE_SETTINGS_DESTINATION)
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(LIST_STYLE_SETTINGS_DESTINATION) {
            ListStyleSettingsView(
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            FULLSCREEN_IMAGE_DESTINATION,
            arguments = listOf(
                navArgument(URL_ARGUMENT.removeFirstAndLast()) { type = NavType.StringType }
            )
        ) { navEntry ->
            FullScreenImageView(
                imageUrl = navEntry.arguments?.getString(URL_ARGUMENT.removeFirstAndLast()),
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}