package com.axiel7.anihyou.ui.screens.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.ReportDrawn
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.axiel7.anihyou.core.base.extensions.firstBlocking
import com.axiel7.anihyou.core.model.DeepLink
import com.axiel7.anihyou.core.model.HomeTab
import com.axiel7.anihyou.core.model.Theme
import com.axiel7.anihyou.core.resources.dark_scrim
import com.axiel7.anihyou.core.resources.light_scrim
import com.axiel7.anihyou.core.ui.common.BottomDestination.Companion.isBottomDestination
import com.axiel7.anihyou.core.ui.common.BottomDestination.Companion.toBottomDestinationIndex
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.screens.main.composables.MainBottomNavBar
import com.axiel7.anihyou.ui.screens.main.composables.MainNavigationRail
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val deepLink = findDeepLink()

        //get necessary preferences while on splashscreen
        viewModel.setToken(viewModel.accessToken.firstBlocking())
        val initialIsLoggedIn = viewModel.isLoggedIn.firstBlocking()
        val initialTheme = viewModel.theme.firstBlocking()
        val initialUseBlackColors = viewModel.useBlackColors.firstBlocking()
        val initialAppColor = viewModel.appColor.firstBlocking()
        val initialAppColorMode = viewModel.appColorMode.firstBlocking()
        val startTab = runBlocking { viewModel.getStartTab() }
        val homeTab = viewModel.homeTab.firstBlocking() ?: HomeTab.DISCOVER
        val tabToOpen = intent.action?.toBottomDestinationIndex() ?: startTab

        setContent {
            KoinAndroidContext {
                val windowSizeClass = calculateWindowSizeClass(this)
                val theme by viewModel.theme.collectAsStateWithLifecycle(initialTheme)
                val isDark = if (theme == Theme.FOLLOW_SYSTEM) isSystemInDarkTheme()
                else theme == Theme.DARK
                val useBlackColors by viewModel.useBlackColors.collectAsStateWithLifecycle(
                    initialValue = initialUseBlackColors
                )
                val appColor by viewModel.appColor.collectAsStateWithLifecycle(initialAppColor)
                val appColorMode by viewModel.appColorMode.collectAsStateWithLifecycle(
                    initialValue = initialAppColorMode
                )
                val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle(initialIsLoggedIn)

                DisposableEffect(isDark) {
                    enableEdgeToEdge(
                        statusBarStyle = SystemBarStyle.auto(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT,
                        ) { isDark },
                        navigationBarStyle = SystemBarStyle.auto(
                            light_scrim.toArgb(),
                            dark_scrim.toArgb(),
                        ) { isDark },
                    )
                    onDispose {}
                }

                AniHyouTheme(
                    darkTheme = isDark,
                    blackColors = useBlackColors,
                    appColor = appColor,
                    appColorMode = appColorMode,
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainView(
                            windowSizeClass = windowSizeClass,
                            isLoggedIn = isLoggedIn,
                            tabToOpen = tabToOpen,
                            event = viewModel,
                            homeTab = homeTab,
                            deepLink = deepLink,
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.onIntentDataReceived(this, intent.data)
    }

    private fun findDeepLink(): DeepLink? {
        return when {
            // Widget intent
            intent.action == "media_details" -> {
                DeepLink(
                    type = DeepLink.Type.ANIME,// does not mather ANIME or MANGA
                    id = intent.getIntExtra("media_id", 0).toString()
                )
            }
            // Search shortcut
            intent.action == "search" -> {
                DeepLink(
                    type = DeepLink.Type.SEARCH,
                    id = "search"
                )
            }
            // Login intent or anilist link
            intent.data != null -> {
                viewModel.onIntentDataReceived(this, intent.data)
                // Manually handle deep links because the uri pattern in the compose navigation
                // matches this -> https://anilist.co/manga/41514/
                // but not this -> https://anilist.co/manga/41514/Otoyomegatari/
                //TODO: find a better solution :)
                val anilistSchemeIndex = intent.dataString?.indexOf("anilist.co")
                if (anilistSchemeIndex != null && anilistSchemeIndex != -1) {
                    val linkSplit = intent.dataString!!.substring(anilistSchemeIndex).split('/')
                    DeepLink(
                        type = DeepLink.Type.valueOf(linkSplit[1].uppercase()),
                        id = linkSplit[2]
                    )
                } else null
            }

            else -> null
        }
    }
}

@Composable
fun MainView(
    windowSizeClass: WindowSizeClass,
    isLoggedIn: Boolean,
    tabToOpen: Int,
    event: MainEvent?,
    homeTab: HomeTab,
    deepLink: DeepLink?,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isBottomDestination by remember {
        derivedStateOf { navBackStackEntry?.isBottomDestination() == true }
    }
    val navActionManager = NavActionManager.rememberNavActionManager(navController)
    val isCompactScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    Scaffold(
        bottomBar = {
            if (isCompactScreen) {
                MainBottomNavBar(
                    navController = navController,
                    navBackStackEntry = navBackStackEntry,
                    navActionManager = navActionManager,
                    isVisible = isBottomDestination,
                    onItemSelected = { event?.saveLastTab(it) }
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        if (isCompactScreen) {
            MainNavigation(
                navController = navController,
                navActionManager = navActionManager,
                isCompactScreen = true,
                isLoggedIn = isLoggedIn,
                tabToOpen = tabToOpen,
                deepLink = deepLink,
                homeTab = homeTab,
                padding = padding,
            )
        } else {
            Row(
                modifier = Modifier.padding(padding)
            ) {
                MainNavigationRail(
                    navController = navController,
                    navBackStackEntry = navBackStackEntry,
                    onItemSelected = { event?.saveLastTab(it) },
                    modifier = Modifier.safeDrawingPadding(),
                )
                MainNavigation(
                    navController = navController,
                    navActionManager = navActionManager,
                    isCompactScreen = false,
                    isLoggedIn = isLoggedIn,
                    tabToOpen = tabToOpen,
                    deepLink = deepLink,
                    homeTab = homeTab,
                )
            }
        }
        ReportDrawn()
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun MainPreview() {
    AniHyouTheme {
        MainView(
            windowSizeClass = WindowSizeClass.calculateFromSize(
                DpSize(width = 1280.dp, height = 1920.dp)
            ),
            isLoggedIn = false,
            tabToOpen = 0,
            event = null,
            homeTab = HomeTab.DISCOVER,
            deepLink = null
        )
    }
}