package com.axiel7.anihyou

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.axiel7.anihyou.data.PreferencesDataStore.ACCESS_TOKEN_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.LAST_TAB_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.THEME_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.base.BottomDestination
import com.axiel7.anihyou.ui.base.BottomDestination.Companion.toBottomDestinationIndex
import com.axiel7.anihyou.ui.home.HomeView
import com.axiel7.anihyou.ui.login.LoginView
import com.axiel7.anihyou.ui.mediadetails.MEDIA_DETAILS_DESTINATION
import com.axiel7.anihyou.ui.mediadetails.MediaDetailsView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.usermedialist.UserMediaListHostView
import com.axiel7.anihyou.utils.ANIHYOU_SCHEME
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        parseIntentData(intent.data)

        //get necessary preferences while on splashscreen
        val startTab = App.dataStore.getValueSync(LAST_TAB_PREFERENCE_KEY)
        val lastTabOpened = intent.action?.toBottomDestinationIndex() ?: startTab
        val theme = App.dataStore.getValueSync(THEME_PREFERENCE_KEY) ?: "follow_system"
        App.dataStore.getValueSync(ANIME_LIST_SORT_PREFERENCE_KEY)?.let { App.animeListSort = it }
        App.dataStore.getValueSync(MANGA_LIST_SORT_PREFERENCE_KEY)?.let { App.mangaListSort = it }

        var mediaId: Int? = null
        var mediaType: String? = null
        if (intent.action == "details") {
            mediaId = intent.getIntExtra("media_id", 0)
            mediaType = intent.getStringExtra("media_type")
        }

        setContent {
            val themePreference by rememberPreference(THEME_PREFERENCE_KEY, theme)
            val navController = rememberNavController()

            AniHyouTheme(
                darkTheme = if (themePreference == "follow_system") isSystemInDarkTheme()
                else themePreference == "dark"
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

            LaunchedEffect(mediaId) {
                if (mediaId != null && mediaId != 0) {
                    navController.navigate("details/$mediaType/$mediaId")
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        parseIntentData(intent?.data)
    }

    private fun parseIntentData(data: Uri?) {
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
        NavHost(
            navController = navController,
            startDestination = when (lastTabOpened) {
                1 -> BottomDestination.AnimeList.route
                2 -> BottomDestination.MangaList.route
                3 -> BottomDestination.Explore.route
                else -> BottomDestination.Home.route
            },
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomDestination.Home.route) {
                HomeView(
                    navigateToDetails = { id ->
                        navController.navigate("details/$id")
                    }
                )
            }

            composable(BottomDestination.AnimeList.route) {
                if (accessTokenPreference.value == null) {
                    LoginView()
                } else {
                    UserMediaListHostView(
                        mediaType = MediaType.ANIME,
                        navigateToDetails = { id ->
                            navController.navigate("details/$id") {
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
                        navigateToDetails = { id ->
                            navController.navigate("details/$id") {
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
                    Text(text = "Profile")
                }
            }

            composable(BottomDestination.Explore.route) {
                Text(text = "Explore")
            }

            composable(MEDIA_DETAILS_DESTINATION,
                arguments = listOf(
                    navArgument("media_id") { type = NavType.IntType }
                ),
                deepLinks = listOf(
                    navDeepLink { uriPattern = "https://anilist.co/anime/{media_id}" },
                    navDeepLink { uriPattern = "https://anilist.co/manga/{media_id}" }
                )
            ) { navEntry ->
                MediaDetailsView(
                    mediaId = navEntry.arguments?.getInt("media_id") ?: 0,
                    navigateBack = {
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