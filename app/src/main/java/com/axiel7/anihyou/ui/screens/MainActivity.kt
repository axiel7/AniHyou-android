package com.axiel7.anihyou.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.axiel7.anihyou.App
import com.axiel7.anihyou.data.PreferencesDataStore.AIRING_ON_MY_LIST_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_COMPLETED_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_CURRENT_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_DROPPED_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_PAUSED_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.ANIME_PLANNING_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.GENERAL_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.GRID_ITEMS_PER_ROW_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.LAST_TAB_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_COMPLETED_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_CURRENT_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_DROPPED_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_LIST_SORT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_PAUSED_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.MANGA_PLANNING_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.SCORE_FORMAT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.THEME_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.USE_GENERAL_LIST_STYLE_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.data.PreferencesDataStore.rememberPreference
import com.axiel7.anihyou.data.model.DeepLink
import com.axiel7.anihyou.data.repository.LoginRepository
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.ui.base.BottomDestination
import com.axiel7.anihyou.ui.base.BottomDestination.Companion.toBottomDestinationIndex
import com.axiel7.anihyou.ui.base.ListStyle
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ANIHYOU_SCHEME
import com.axiel7.anihyou.utils.THEME_BLACK
import com.axiel7.anihyou.utils.THEME_DARK
import com.axiel7.anihyou.utils.THEME_FOLLOW_SYSTEM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        var deepLink: DeepLink? = null
        // Widget intent
        if (intent.action == "media_details") {
            deepLink = DeepLink(
                type = DeepLink.Type.ANIME,
                id = intent.getIntExtra("media_id", 0).toString()
            )
        } else if (intent.data != null) {
            parseLoginIntentData(intent.data)
            // Manually handle deep links because the uri pattern in the compose navigation
            // matches this -> https://anilist.co/manga/41514/
            // but not this -> https://anilist.co/manga/41514/Otoyomegatari/
            //TODO: find a better solution :)
            val anilistSchemeIndex = intent.dataString?.indexOf("anilist.co")
            if (anilistSchemeIndex != null && anilistSchemeIndex != -1) {
                val linkSplit = intent.dataString!!.substring(anilistSchemeIndex).split('/')
                deepLink = DeepLink(
                    type = DeepLink.Type.valueOf(linkSplit[1].uppercase()),
                    id = linkSplit[2]
                )
            }
        }

        //get necessary preferences while on splashscreen
        val startTab = App.dataStore.getValueSync(LAST_TAB_PREFERENCE_KEY)
        val lastTabOpened = intent.action?.toBottomDestinationIndex() ?: startTab
        val theme = App.dataStore.getValueSync(THEME_PREFERENCE_KEY) ?: "follow_system"

        preloadPreferences()

        setContent {
            val themePreference by rememberPreference(THEME_PREFERENCE_KEY, theme)

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
                        lastTabOpened = lastTabOpened ?: 0,
                        deepLink = deepLink,
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

    private fun preloadPreferences() {
        App.dataStore.getValueSync(ANIME_LIST_SORT_PREFERENCE_KEY)?.let {
            App.animeListSort = it
        }
        App.dataStore.getValueSync(MANGA_LIST_SORT_PREFERENCE_KEY)?.let {
            App.mangaListSort = it
        }
        App.dataStore.getValueSync(SCORE_FORMAT_PREFERENCE_KEY)?.let {
            App.scoreFormat = ScoreFormat.valueOf(it)
        }
        App.dataStore.getValueSync(AIRING_ON_MY_LIST_PREFERENCE_KEY)?.let {
            App.airingOnMyList = it
        }
        App.dataStore.getValueSync(GENERAL_LIST_STYLE_PREFERENCE_KEY)?.let {
            App.generalListStyle = ListStyle.valueOf(it)
        }
        App.dataStore.getValueSync(USE_GENERAL_LIST_STYLE_PREFERENCE_KEY)?.let {
            App.useGeneralListStyle = it
        }
        App.dataStore.getValueSync(GRID_ITEMS_PER_ROW_PREFERENCE_KEY)?.let {
            App.gridItemsPerRow = it
        }
        App.dataStore.getValueSync(ANIME_CURRENT_LIST_STYLE_PREFERENCE_KEY)?.let {
            App.animeCurrentListStyle = ListStyle.valueOf(it)
        }
        App.dataStore.getValueSync(MANGA_CURRENT_LIST_STYLE_PREFERENCE_KEY)?.let {
            App.mangaCurrentListStyle = ListStyle.valueOf(it)
        }
        // load preferences used later in another thread
        lifecycleScope.launch(Dispatchers.IO) {
            App.dataStore.getValueSync(ANIME_PLANNING_LIST_STYLE_PREFERENCE_KEY)?.let {
                App.animePlanningListStyle = ListStyle.valueOf(it)
            }
            App.dataStore.getValueSync(ANIME_COMPLETED_LIST_STYLE_PREFERENCE_KEY)?.let {
                App.animeCompletedListStyle = ListStyle.valueOf(it)
            }
            App.dataStore.getValueSync(ANIME_PAUSED_LIST_STYLE_PREFERENCE_KEY)?.let {
                App.animePausedListStyle = ListStyle.valueOf(it)
            }
            App.dataStore.getValueSync(ANIME_DROPPED_LIST_STYLE_PREFERENCE_KEY)?.let {
                App.animeDroppedListStyle = ListStyle.valueOf(it)
            }
            App.dataStore.getValueSync(MANGA_PLANNING_LIST_STYLE_PREFERENCE_KEY)?.let {
                App.mangaPlanningListStyle = ListStyle.valueOf(it)
            }
            App.dataStore.getValueSync(MANGA_COMPLETED_LIST_STYLE_PREFERENCE_KEY)?.let {
                App.mangaCompletedListStyle = ListStyle.valueOf(it)
            }
            App.dataStore.getValueSync(MANGA_PAUSED_LIST_STYLE_PREFERENCE_KEY)?.let {
                App.mangaPausedListStyle = ListStyle.valueOf(it)
            }
            App.dataStore.getValueSync(MANGA_DROPPED_LIST_STYLE_PREFERENCE_KEY)?.let {
                App.mangaDroppedListStyle = ListStyle.valueOf(it)
            }
        }
    }
}

@Composable
fun MainView(
    lastTabOpened: Int,
    deepLink: DeepLink?,
) {
    val navController = rememberNavController()

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
        MainNavigation(
            navController = navController,
            lastTabOpened = lastTabOpened,
            deepLink = deepLink,
            padding = padding,
        )
    }
}

@Composable
fun BottomNavBar(
    navController: NavController,
    lastTabOpened: Int
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isVisible by remember {
        derivedStateOf {
            when {
                BottomDestination.values.map { it.route }
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
            BottomDestination.values.forEachIndexed { index, dest ->
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
            lastTabOpened = 0,
            deepLink = null
        )
    }
}