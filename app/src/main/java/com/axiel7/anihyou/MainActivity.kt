package com.axiel7.anihyou

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import androidx.navigation.navArgument
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.LAST_TAB_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.THEME_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.AnimeSeason
import com.axiel7.anihyou.data.model.ChartType
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.base.BottomDestination
import com.axiel7.anihyou.ui.base.BottomDestination.Companion.toBottomDestinationIndex
import com.axiel7.anihyou.ui.base.BottomDestination.Companion.toBottomDestinationRoute
import com.axiel7.anihyou.ui.characterdetails.CHARACTER_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.characterdetails.CharacterDetailsView
import com.axiel7.anihyou.ui.composables.FULLSCREEN_IMAGE_DESTINATION
import com.axiel7.anihyou.ui.composables.FullScreenImageView
import com.axiel7.anihyou.ui.explore.ExploreView
import com.axiel7.anihyou.ui.explore.MEDIA_CHART_DESTINATION
import com.axiel7.anihyou.ui.explore.MediaChartListView
import com.axiel7.anihyou.ui.explore.SEASON_ANIME_DESTINATION
import com.axiel7.anihyou.ui.explore.SeasonAnimeView
import com.axiel7.anihyou.ui.home.HomeView
import com.axiel7.anihyou.ui.login.LoginView
import com.axiel7.anihyou.ui.mediadetails.MEDIA_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.mediadetails.MediaDetailsView
import com.axiel7.anihyou.ui.profile.ProfileView
import com.axiel7.anihyou.ui.profile.USER_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.settings.SETTINGS_DESTINATION
import com.axiel7.anihyou.ui.settings.SettingsView
import com.axiel7.anihyou.ui.staffdetails.STAFF_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.staffdetails.StaffDetailsView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.usermedialist.UserMediaListHostView
import com.axiel7.anihyou.utils.ANIHYOU_SCHEME
import com.axiel7.anihyou.utils.THEME_BLACK
import com.axiel7.anihyou.utils.THEME_DARK
import com.axiel7.anihyou.utils.THEME_FOLLOW_SYSTEM
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        var detailsType: String? = null
        var detailsId: Int? = null
        if (intent.data != null) {
            parseLoginIntentData(intent.data)
            // Manually handle deep links because the uri pattern in the compose navigation
            // matches this -> https://anilist.co/manga/41514/
            // but not this -> https://anilist.co/manga/41514/Otoyomegatari/
            // TODO: find a better solution :)
            val anilistSchemeIndex = intent.dataString?.indexOf("anilist.co")
            if (anilistSchemeIndex != null && anilistSchemeIndex != -1) {
                val linkSplit = intent.dataString!!.substring(anilistSchemeIndex).split('/')
                detailsType = linkSplit[1]
                detailsId = linkSplit[2].toIntOrNull()
            }
        }

        //get necessary preferences while on splashscreen
        val startTab = App.dataStore.getValueSync(LAST_TAB_PREFERENCE_KEY)
        val lastTabOpened = intent.action?.toBottomDestinationIndex() ?: startTab
        val theme = App.dataStore.getValueSync(THEME_PREFERENCE_KEY) ?: "follow_system"
        App.dataStore.getValueSync(ANIME_LIST_SORT_PREFERENCE_KEY)?.let { App.animeListSort = it }
        App.dataStore.getValueSync(MANGA_LIST_SORT_PREFERENCE_KEY)?.let { App.mangaListSort = it }
        App.dataStore.getValueSync(SCORE_FORMAT_PREFERENCE_KEY)?.let { App.scoreFormat = ScoreFormat.valueOf(it) }

        setContent {
            val themePreference by rememberPreference(THEME_PREFERENCE_KEY, theme)
            val navController = rememberAnimatedNavController()

            LaunchedEffect(detailsId) {
                if (detailsId != 0 && detailsType != null) {
                    when (detailsType) {
                        "anime", "manga" -> {
                            navController.navigate("media_details/$detailsId")
                        }
                        "character" -> {
                            navController.navigate("character/$detailsId")
                        }
                        "staff" -> {
                            navController.navigate("staff/$detailsId")
                        }
                        "user" -> {
                            navController.navigate("profile/$detailsId")
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainView(
    navController: NavHostController,
    lastTabOpened: Int = 0
) {
    val accessTokenPreference = rememberPreference(ACCESS_TOKEN_PREFERENCE_KEY, null)

    com.google.accompanist.insets.ui.Scaffold(
        bottomBar = {
            BottomNavBar(
                navController = navController,
                lastTabOpened = lastTabOpened
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background
    ) { padding ->
        AnimatedNavHost(
            navController = navController,
            startDestination = lastTabOpened.toBottomDestinationRoute(),
            modifier = Modifier.padding(padding),
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
                    navigateToMediaDetails = { id ->
                        navController.navigate("media_details/$id")
                    },
                    navigateToAnimeSeason = { animeSeason ->
                        navController.navigate("season/${animeSeason.year}/${animeSeason.season.name}")
                    }
                )
            }

            composable(BottomDestination.AnimeList.route) {
                if (accessTokenPreference.value == null) {
                    LoginView()
                } else {
                    UserMediaListHostView(
                        mediaType = MediaType.ANIME,
                        navigateToMediaDetails = { id ->
                            navController.navigate("media_details/$id") {
                                popUpTo(navController.graph[BottomDestination.AnimeList.route].id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }

            composable(BottomDestination.MangaList.route) {
                if (accessTokenPreference.value == null) {
                    LoginView()
                } else {
                    UserMediaListHostView(
                        mediaType = MediaType.MANGA,
                        navigateToMediaDetails = { id ->
                            navController.navigate("media_details/$id") {
                                popUpTo(navController.graph[BottomDestination.MangaList.route].id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }

            composable(BottomDestination.Profile.route) {
                if (accessTokenPreference.value == null) {
                    LoginView()
                } else {
                    ProfileView(
                        navigateToSettings = {
                            navController.navigate(SETTINGS_DESTINATION)
                        },
                        navigateToFullscreenImage = { url ->
                            val encodedUrl = URLEncoder.encode(url, "UTF-8")
                            navController.navigate("full_image/$encodedUrl")
                        },
                    )
                }
            }

            composable(BottomDestination.Explore.route) {
                ExploreView(
                    navigateToMediaDetails = { id ->
                        navController.navigate("media_details/$id") {
                            popUpTo(navController.graph[BottomDestination.Explore.route].id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    navigateToUserDetails = { id ->
                        navController.navigate("profile/$id")
                    },
                    navigateToCharacterDetails = { id ->
                        navController.navigate("character/$id")
                    },
                    navigateToStaffDetails = { id ->
                        navController.navigate("staff/$id")
                    },
                    navigateToMediaChart = {
                        navController.navigate("media_chart/${it.name}")
                    },
                    navigateToAnimeSeason = { year, season ->
                        navController.navigate("season/$year/$season")
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
                        navController.navigate("media_details/$id")
                    },
                    navigateToFullscreenImage = { url ->
                        val encodedUrl = URLEncoder.encode(url, "UTF-8")
                        navController.navigate("full_image/$encodedUrl")
                    },
                    navigateToCharacterDetails = { id ->
                        navController.navigate("character/$id")
                    },
                    navigateToStaffDetails = { id ->
                        navController.navigate("staff/$id")
                    },
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
                            navController.navigate("media_details/$id") {
                                popUpTo(navController.graph[MEDIA_CHART_DESTINATION].id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
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
                                navController.navigate("media_details/$id") {
                                    popUpTo(navController.graph[SEASON_ANIME_DESTINATION].id) {
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

            composable(USER_DETAILS_DESTINATION,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { navEntry ->
                ProfileView(
                    userId = navEntry.arguments?.getInt("id"),
                    navigateToFullscreenImage = { url ->
                        val encodedUrl = URLEncoder.encode(url, "UTF-8")
                        navController.navigate("full_image/$encodedUrl")
                    },
                    navigateBack = {
                        navController.popBackStack()
                    }
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
                            navController.navigate("media_details/$id")
                        },
                        navigateToFullscreenImage = { url ->
                            val encodedUrl = URLEncoder.encode(url, "UTF-8")
                            navController.navigate("full_image/$encodedUrl")
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
                            navController.navigate("media_details/$id")
                        },
                        navigateToCharacterDetails = { id ->
                            navController.navigate("character/$id")
                        },
                        navigateToFullscreenImage = { url ->
                            val encodedUrl = URLEncoder.encode(url, "UTF-8")
                            navController.navigate("full_image/$encodedUrl")
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
                            painter = painterResource(if (selectedItem == index) dest.iconSelected else dest.icon),
                            contentDescription = stringResource(dest.title)
                        )
                    },
                    label = { Text(text = stringResource(dest.title)) },
                    selected = selectedItem == index,
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