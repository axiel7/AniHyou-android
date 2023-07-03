package com.axiel7.anihyou.ui.screens

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
import com.axiel7.anihyou.ui.screens.calendar.CALENDAR_DESTINATION
import com.axiel7.anihyou.ui.screens.calendar.CalendarView
import com.axiel7.anihyou.ui.screens.characterdetails.CHARACTER_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.ui.screens.explore.EXPLORE_GENRE_DESTINATION
import com.axiel7.anihyou.ui.screens.explore.ExploreView
import com.axiel7.anihyou.ui.screens.explore.MEDIA_CHART_DESTINATION
import com.axiel7.anihyou.ui.screens.explore.MediaChartListView
import com.axiel7.anihyou.ui.screens.explore.SEASON_ANIME_DESTINATION
import com.axiel7.anihyou.ui.screens.explore.SeasonAnimeView
import com.axiel7.anihyou.ui.screens.home.HomeView
import com.axiel7.anihyou.ui.screens.login.LoginView
import com.axiel7.anihyou.ui.screens.mediadetails.MEDIA_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetailsView
import com.axiel7.anihyou.ui.screens.notifications.NOTIFICATIONS_DESTINATION
import com.axiel7.anihyou.ui.screens.notifications.NotificationsView
import com.axiel7.anihyou.ui.screens.profile.ProfileView
import com.axiel7.anihyou.ui.screens.profile.USER_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.reviewdetails.REVIEW_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.reviewdetails.ReviewDetailsView
import com.axiel7.anihyou.ui.screens.settings.LIST_STYLE_SETTINGS_DESTINATION
import com.axiel7.anihyou.ui.screens.settings.ListStyleSettingsView
import com.axiel7.anihyou.ui.screens.settings.SETTINGS_DESTINATION
import com.axiel7.anihyou.ui.screens.settings.SettingsView
import com.axiel7.anihyou.ui.screens.staffdetails.STAFF_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetailsView
import com.axiel7.anihyou.ui.screens.studiodetails.STUDIO_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.studiodetails.StudioDetailsView
import com.axiel7.anihyou.ui.screens.thread.THREAD_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.screens.thread.ThreadDetailsView
import com.axiel7.anihyou.ui.screens.usermedialist.USER_MEDIA_LIST_DESTINATION
import com.axiel7.anihyou.ui.screens.usermedialist.UserMediaListHostView
import java.net.URLEncoder

@Composable
fun MainNavigation(
    navController: NavHostController,
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
                            .replace("{media_id}", deepLink.id)
                    )
                }
                DeepLink.Type.USER -> {
                    val userId = deepLink.id.toIntOrNull()
                    var dest = USER_DETAILS_DESTINATION
                    dest = if (userId != null) dest.replace("{id}", userId.toString())
                    else dest.replace("{name}", deepLink.id)
                    navController.navigate(dest)
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
                modifier = Modifier.padding(bottom = bottomPadding),
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace("{media_id}", id.toString())
                    )
                },
                navigateToAnimeSeason = { animeSeason ->
                    navController.navigate(
                        SEASON_ANIME_DESTINATION
                            .replace("{year}", animeSeason.year.toString())
                            .replace("{season}", animeSeason.season.name)
                    )
                },
                navigateToCalendar = {
                    navController.navigate(CALENDAR_DESTINATION)
                },
                navigateToExplore = { mediaType, mediaSort ->
                    navController.navigate(
                        EXPLORE_GENRE_DESTINATION
                            .replace("{mediaType}", mediaType.rawValue)
                            .replace("{mediaSort}", mediaSort.rawValue)
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
                                .replace("{media_id}", id.toString())
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
                                .replace("{media_id}", id.toString())
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
                    modifier = Modifier.padding(bottom = bottomPadding),
                    navigateToSettings = {
                        navController.navigate(SETTINGS_DESTINATION)
                    },
                    navigateToFullscreenImage = { url ->
                        val encodedUrl = URLEncoder.encode(url, "UTF-8")
                        navController.navigate(
                            FULLSCREEN_IMAGE_DESTINATION
                                .replace("{url}", encodedUrl)
                        )
                    },
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace("{media_id}", id.toString())
                        )
                    },
                    navigateToCharacterDetails = { id ->
                        navController.navigate(
                            CHARACTER_DETAILS_DESTINATION
                                .replace("{id}", id.toString())
                        )
                    },
                    navigateToStaffDetails = { id ->
                        navController.navigate(
                            STAFF_DETAILS_DESTINATION
                                .replace("{id}", id.toString())
                        )
                    },
                    navigateToStudioDetails = { id ->
                        navController.navigate(
                            STUDIO_DETAILS_DESTINATION
                                .replace("{id}", id.toString())
                        )
                    },
                    navigateToUserDetails = { id ->
                        navController.navigate(
                            USER_DETAILS_DESTINATION
                                .replace("{id}", id.toString())
                        )
                    },
                    navigateToUserMediaList = null
                )
            }
        }

        composable(BottomDestination.Explore.route) {
            ExploreView(
                modifier = Modifier.padding(bottom = bottomPadding),
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace("{media_id}", id.toString())
                    )
                },
                navigateToUserDetails = { id ->
                    navController.navigate(
                        USER_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToCharacterDetails = { id ->
                    navController.navigate(
                        CHARACTER_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToStaffDetails = { id ->
                    navController.navigate(
                        STAFF_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToStudioDetails = { id ->
                    navController.navigate(
                        STUDIO_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToMediaChart = { type ->
                    navController.navigate(
                        MEDIA_CHART_DESTINATION
                            .replace("{type}", type.name)
                    )
                },
                navigateToAnimeSeason = { year, season ->
                    navController.navigate(
                        SEASON_ANIME_DESTINATION
                            .replace("{year}", year.toString())
                            .replace("{season}", season)
                    )
                },
                navigateToCalendar = {
                    navController.navigate(CALENDAR_DESTINATION)
                }
            )
        }

        composable(
            EXPLORE_GENRE_DESTINATION,
            arguments = listOf(
                navArgument("mediaType") { type = NavType.StringType },
                navArgument("mediaSort") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("genre") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("tag") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { navEntry ->
            ExploreView(
                modifier = Modifier.padding(bottom = bottomPadding),
                initialMediaType = navEntry.arguments?.getString("mediaType")?.let { MediaType.safeValueOf(it) },
                initialMediaSort = navEntry.arguments?.getString("mediaSort")?.let { MediaSort.valueOf(it) },
                initialGenre = navEntry.arguments?.getString("genre"),
                initialTag = navEntry.arguments?.getString("tag"),
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace("{media_id}", id.toString())
                    )
                },
                navigateToUserDetails = { id ->
                    navController.navigate(
                        USER_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToCharacterDetails = { id ->
                    navController.navigate(
                        CHARACTER_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToStaffDetails = { id ->
                    navController.navigate(
                        STAFF_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToStudioDetails = { id ->
                    navController.navigate(
                        STUDIO_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToMediaChart = { type ->
                    navController.navigate(
                        MEDIA_CHART_DESTINATION
                            .replace("{type}", type.name)
                    )
                },
                navigateToAnimeSeason = { year, season ->
                    navController.navigate(
                        SEASON_ANIME_DESTINATION
                            .replace("{year}", year.toString())
                            .replace("{season}", season)
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
                navArgument("mediaType") { type = NavType.StringType },
                navArgument("userId") { type = NavType.IntType }
            )
        ) { navEntry ->
            UserMediaListHostView(
                mediaType = navEntry.arguments?.getString("mediaType")?.let { MediaType.safeValueOf(it) }!!,
                modifier = Modifier.padding(bottom = bottomPadding),
                userId = navEntry.arguments?.getInt("userId"),
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace("{media_id}", id.toString())
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
                            .replace("{media_id}", id.toString())
                    )
                },
                navigateToUserDetails = { id ->
                    navController.navigate(
                        USER_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
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
                navArgument("media_id") { type = NavType.IntType }
            ),
        ) { navEntry ->
            MediaDetailsView(
                mediaId = navEntry.arguments?.getInt("media_id") ?: 0,
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace("{media_id}", id.toString())
                    )
                },
                navigateToFullscreenImage = { url ->
                    val encodedUrl = URLEncoder.encode(url, "UTF-8")
                    navController.navigate(
                        FULLSCREEN_IMAGE_DESTINATION
                            .replace("{url}", encodedUrl)
                    )
                },
                navigateToCharacterDetails = { id ->
                    navController.navigate(
                        CHARACTER_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToStaffDetails = { id ->
                    navController.navigate(
                        STAFF_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToReviewDetails = { id ->
                    navController.navigate(
                        REVIEW_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToThreadDetails = { id ->
                    navController.navigate(
                        THREAD_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToExplore = { mediaType, genre, tag ->
                    var dest = EXPLORE_GENRE_DESTINATION
                    if (mediaType != null) dest = dest.replace("{mediaType}", mediaType.rawValue)
                    if (genre != null) dest = dest.replace("{genre}", genre)
                    if (tag != null) dest = dest.replace("{tag}", tag)
                    navController.navigate(dest)
                }
            )
        }

        composable(
            MEDIA_CHART_DESTINATION,
            arguments = listOf(
                navArgument("type") { type = NavType.StringType }
            )
        ) { navEntry ->
            navEntry.arguments?.getString("type")?.let {
                MediaChartListView(
                    type = ChartType.valueOf(it),
                    navigateBack = {
                        navController.popBackStack()
                    },
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace("{media_id}", id.toString())
                        )
                    }
                )
            }
        }

        composable(
            SEASON_ANIME_DESTINATION,
            arguments = listOf(
                navArgument("season") { type = NavType.StringType },
                navArgument("year") { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getString("season")?.let {  season ->
                navEntry.arguments?.getInt("year")?.let { year ->
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
                                    .replace("{media_id}", id.toString())
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
                            .replace("{media_id}", id.toString())
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
                navArgument("id") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("name") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { navEntry ->
            ProfileView(
                modifier = Modifier.padding(bottom = bottomPadding),
                userId = navEntry.arguments?.getString("id")?.toIntOrNull(),
                username = navEntry.arguments?.getString("name"),
                navigateToFullscreenImage = { url ->
                    val encodedUrl = URLEncoder.encode(url, "UTF-8")
                    navController.navigate(
                        FULLSCREEN_IMAGE_DESTINATION
                            .replace("{url}", encodedUrl)
                    )
                },
                navigateToMediaDetails = { id ->
                    navController.navigate(
                        MEDIA_DETAILS_DESTINATION
                            .replace("{media_id}", id.toString())
                    )
                },
                navigateToCharacterDetails = { id ->
                    navController.navigate(
                        CHARACTER_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToStaffDetails = { id ->
                    navController.navigate(
                        STAFF_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToStudioDetails = { id ->
                    navController.navigate(
                        STUDIO_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToUserDetails = { id ->
                    navController.navigate(
                        USER_DETAILS_DESTINATION
                            .replace("{id}", id.toString())
                    )
                },
                navigateToUserMediaList = { mediaType, userId ->
                    navController.navigate(
                        USER_MEDIA_LIST_DESTINATION
                            .replace("{userId}", userId.toString())
                            .replace("{mediaType}", mediaType.rawValue)
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
                navArgument("id") { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt("id")?.let { characterId ->
                CharacterDetailsView(
                    characterId = characterId,
                    navigateBack = {
                        navController.popBackStack()
                    },
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace("{media_id}", id.toString())
                        )
                    },
                    navigateToFullscreenImage = { url ->
                        val encodedUrl = URLEncoder.encode(url, "UTF-8")
                        navController.navigate(
                            FULLSCREEN_IMAGE_DESTINATION
                                .replace("{url}", encodedUrl)
                        )
                    },
                )
            }
        }

        composable(
            STAFF_DETAILS_DESTINATION,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt("id")?.let { staffId ->
                StaffDetailsView(
                    staffId = staffId,
                    navigateBack = {
                        navController.popBackStack()
                    },
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace("{media_id}", id.toString())
                        )
                    },
                    navigateToCharacterDetails = { id ->
                        navController.navigate(
                            CHARACTER_DETAILS_DESTINATION
                                .replace("{id}", id.toString())
                        )
                    },
                    navigateToFullscreenImage = { url ->
                        val encodedUrl = URLEncoder.encode(url, "UTF-8")
                        navController.navigate(
                            FULLSCREEN_IMAGE_DESTINATION
                                .replace("{url}", encodedUrl)
                        )
                    }
                )
            }
        }

        composable(
            REVIEW_DETAILS_DESTINATION,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt("id")?.let {
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
                navArgument("id") { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt("id")?.let {
                ThreadDetailsView(
                    threadId = it,
                    navigateToUserDetails = { id ->
                        navController.navigate(
                            USER_DETAILS_DESTINATION
                                .replace("{id}", id.toString())
                        )
                    },
                    navigateToFullscreenImage = { url ->
                        val encodedUrl = URLEncoder.encode(url, "UTF-8")
                        navController.navigate(
                            FULLSCREEN_IMAGE_DESTINATION
                                .replace("{url}", encodedUrl)
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
                navArgument("id") { type = NavType.IntType }
            )
        ) { navEntry ->
            navEntry.arguments?.getInt("id")?.let {
                StudioDetailsView(
                    studioId = it,
                    navigateBack = {
                        navController.popBackStack()
                    },
                    navigateToMediaDetails = { id ->
                        navController.navigate(
                            MEDIA_DETAILS_DESTINATION
                                .replace("{media_id}", id.toString())
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
                navArgument("url") { type = NavType.StringType }
            )
        ) { navEntry ->
            FullScreenImageView(
                imageUrl = navEntry.arguments?.getString("url"),
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
    }
}