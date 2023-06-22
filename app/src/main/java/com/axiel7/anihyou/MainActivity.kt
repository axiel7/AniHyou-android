package com.axiel7.anihyou

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.AIRING_ON_MY_LIST_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.LAST_TAB_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.LIST_DISPLAY_MODE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.THEME_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.base.BottomDestination
import com.axiel7.anihyou.ui.base.BottomDestination.Companion.toBottomDestinationIndex
import com.axiel7.anihyou.ui.base.BottomDestination.Companion.toBottomDestinationRoute
import com.axiel7.anihyou.ui.base.ListMode
import com.axiel7.anihyou.ui.calendar.CALENDAR_DESTINATION
import com.axiel7.anihyou.ui.calendar.CalendarView
import com.axiel7.anihyou.ui.characterdetails.CHARACTER_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.ui.composables.FULLSCREEN_IMAGE_DESTINATION
import com.axiel7.anihyou.ui.composables.FullScreenImageView
import com.axiel7.anihyou.ui.explore.EXPLORE_GENRE_DESTINATION
import com.axiel7.anihyou.ui.explore.ExploreView
import com.axiel7.anihyou.ui.explore.MEDIA_CHART_DESTINATION
import com.axiel7.anihyou.ui.explore.MediaChartListView
import com.axiel7.anihyou.ui.explore.SEASON_ANIME_DESTINATION
import com.axiel7.anihyou.ui.explore.SeasonAnimeView
import com.axiel7.anihyou.ui.home.HomeView
import com.axiel7.anihyou.ui.login.LoginView
import com.axiel7.anihyou.ui.mediadetails.MEDIA_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.mediadetails.MediaDetailsView
import com.axiel7.anihyou.ui.notifications.NOTIFICATIONS_DESTINATION
import com.axiel7.anihyou.ui.notifications.NotificationsView
import com.axiel7.anihyou.ui.profile.ProfileView
import com.axiel7.anihyou.ui.profile.USER_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.reviewdetails.REVIEW_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.reviewdetails.ReviewDetailsView
import com.axiel7.anihyou.ui.settings.SETTINGS_DESTINATION
import com.axiel7.anihyou.ui.settings.SettingsView
import com.axiel7.anihyou.ui.staffdetails.STAFF_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.staffdetails.StaffDetailsView
import com.axiel7.anihyou.ui.studiodetails.STUDIO_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.studiodetails.StudioDetailsView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.thread.THREAD_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.thread.ThreadDetailsView
import com.axiel7.anihyou.ui.usermedialist.USER_MEDIA_LIST_DESTINATION
import com.axiel7.anihyou.ui.usermedialist.UserMediaListHostView
import com.axiel7.anihyou.utils.ANIHYOU_SCHEME
import com.axiel7.anihyou.utils.THEME_BLACK
import com.axiel7.anihyou.utils.THEME_DARK
import com.axiel7.anihyou.utils.THEME_FOLLOW_SYSTEM
import kotlinx.coroutines.launch
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        var detailsType: String? = null
        var detailsId: String? = null
        // Widget intent
        if (intent.action == "media_details") {
            detailsType = "anime"
            detailsId = intent.getIntExtra("media_id", 0).toString()
        }
        else if (intent.data != null) {
            parseLoginIntentData(intent.data)
            // Manually handle deep links because the uri pattern in the compose navigation
            // matches this -> https://anilist.co/manga/41514/
            // but not this -> https://anilist.co/manga/41514/Otoyomegatari/
            //TODO: find a better solution :)
            val anilistSchemeIndex = intent.dataString?.indexOf("anilist.co")
            if (anilistSchemeIndex != null && anilistSchemeIndex != -1) {
                val linkSplit = intent.dataString!!.substring(anilistSchemeIndex).split('/')
                detailsType = linkSplit[1]
                detailsId = linkSplit[2]
            }
        }

        //get necessary preferences while on splashscreen
        val startTab = App.dataStore.getValueSync(LAST_TAB_PREFERENCE_KEY)
        val lastTabOpened = intent.action?.toBottomDestinationIndex() ?: startTab
        val theme = App.dataStore.getValueSync(THEME_PREFERENCE_KEY) ?: "follow_system"
        App.dataStore.getValueSync(ANIME_LIST_SORT_PREFERENCE_KEY)?.let { App.animeListSort = it }
        App.dataStore.getValueSync(MANGA_LIST_SORT_PREFERENCE_KEY)?.let { App.mangaListSort = it }
        App.dataStore.getValueSync(SCORE_FORMAT_PREFERENCE_KEY)?.let { App.scoreFormat = ScoreFormat.valueOf(it) }
        App.dataStore.getValueSync(LIST_DISPLAY_MODE_PREFERENCE_KEY)?.let { App.listDisplayMode = ListMode.valueOf(it) }
        App.dataStore.getValueSync(AIRING_ON_MY_LIST_PREFERENCE_KEY)?.let { App.airingOnMyList = it }

        setContent {
            val themePreference by rememberPreference(THEME_PREFERENCE_KEY, theme)
            val navController = rememberNavController()

            LaunchedEffect(detailsId) {
                if (detailsId != null && detailsType != null) {
                    when (detailsType) {
                        "anime", "manga" -> {
                            navController.navigate(
                                MEDIA_DETAILS_DESTINATION
                                    .replace("{media_id}", detailsId)
                            )
                        }
                        "character" -> {
                            navController.navigate("character/$detailsId")
                        }
                        "staff" -> {
                            navController.navigate("staff/$detailsId")
                        }
                        "studio" -> {
                            navController.navigate("studio/$detailsId")
                        }
                        "user" -> {
                            val userId = detailsId.toIntOrNull()
                            var dest = USER_DETAILS_DESTINATION
                            dest = if (userId != null) dest.replace("{id}", userId.toString())
                            else dest.replace("{name}", detailsId)
                            navController.navigate(dest)
                        }
                    }
                }
            }

            AniHyouTheme(
                darkTheme = if (themePreference == THEME_FOLLOW_SYSTEM) isSystemInDarkTheme()
                else themePreference == THEME_DARK || themePreference == THEME_BLACK,
                blackColors = themePreference == THEME_BLACK
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(
                        navController = navController,
                        lastTabOpened = lastTabOpened ?: 0
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseLoginIntentData(intent?.data)
    }

    private fun parseLoginIntentData(data: Uri?) {
        if (data?.scheme == ANIHYOU_SCHEME) {
            lifecycleScope.launch {
                LoginRepository.parseRedirectUri(data)
            }
        }
    }
}

@Composable
fun MainView(
    navController: NavHostController,
    lastTabOpened: Int = 0
) {
    val accessTokenPreference by rememberPreference(ACCESS_TOKEN_PREFERENCE_KEY, null)

    Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                lastTabOpened = lastTabOpened
            )
        },
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        val bottomPadding by animateDpAsState(
            targetValue = padding.calculateBottomPadding(),
            label = "bottom_bar_padding"
        )
        NavHost(
            navController = navController,
            startDestination = lastTabOpened.toBottomDestinationRoute(),
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

            composable(EXPLORE_GENRE_DESTINATION,
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

            composable(USER_MEDIA_LIST_DESTINATION,
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

            composable(MEDIA_DETAILS_DESTINATION,
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

            composable(MEDIA_CHART_DESTINATION,
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

            composable(SEASON_ANIME_DESTINATION,
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

            composable(USER_DETAILS_DESTINATION,
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

            composable(CHARACTER_DETAILS_DESTINATION,
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

            composable(STAFF_DETAILS_DESTINATION,
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

            composable(REVIEW_DETAILS_DESTINATION,
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

            composable(THREAD_DETAILS_DESTINATION,
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

            composable(STUDIO_DETAILS_DESTINATION,
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
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(FULLSCREEN_IMAGE_DESTINATION,
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
}

private val bottomDestinations = listOf(
    BottomDestination.Home,
    BottomDestination.AnimeList,
    BottomDestination.MangaList,
    BottomDestination.Profile,
    BottomDestination.Explore
)

@Composable
fun BottomNavBar(
    navController: NavController,
    lastTabOpened: Int
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isVisible by remember {
        derivedStateOf {
            when {
                bottomDestinations.map { it.route }
                    .contains(navBackStackEntry?.destination?.route) -> true
                navBackStackEntry?.destination?.route == null -> true
                else -> false
            }
        }
    }
    var selectedItem by rememberPreference(LAST_TAB_PREFERENCE_KEY, lastTabOpened)

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar {
            bottomDestinations.forEachIndexed { index, dest ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(
                                if (navBackStackEntry?.destination?.route == dest.route) dest.iconSelected
                                else dest.icon
                            ),
                            contentDescription = stringResource(dest.title)
                        )
                    },
                    label = { Text(text = stringResource(dest.title)) },
                    selected = navBackStackEntry?.destination?.route == dest.route,
                    onClick = {
                        selectedItem = index
                        navController.navigate(dest.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    AniHyouTheme {
        MainView(
            navController = rememberNavController()
        )
    }
}