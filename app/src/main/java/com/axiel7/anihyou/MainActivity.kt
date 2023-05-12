package com.axiel7.anihyou

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.axiel7.anihyou.data.PreferencesDataStore.LAST_TAB_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.THEME_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.defaultPreferencesDataStore
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.ui.base.BottomDestination
import com.axiel7.anihyou.ui.base.BottomDestination.Companion.toBottomDestinationIndex
import com.axiel7.anihyou.ui.home.HomeView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.usermedialist.UserMediaListView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //get necessary preferences while on splashscreen
        val startTab = defaultPreferencesDataStore.getValueSync(LAST_TAB_PREFERENCE_KEY)
        val lastTabOpened = intent.action?.toBottomDestinationIndex() ?: startTab
        val theme = defaultPreferencesDataStore.getValueSync(THEME_PREFERENCE_KEY) ?: "follow_system"

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
}

@Composable
fun MainView(
    navController: NavHostController,
    lastTabOpened: Int = 0
) {
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
                UserMediaListView()
            }
        }
    }
}

private val bottomDestinations = listOf(
    BottomDestination.Home,
    BottomDestination.AnimeList,
    BottomDestination.MangaList,
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