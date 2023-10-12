package com.axiel7.anihyou.ui.screens.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.axiel7.anihyou.App
import com.axiel7.anihyou.common.firstBlocking
import com.axiel7.anihyou.data.model.DeepLink
import com.axiel7.anihyou.ui.common.AppColorMode
import com.axiel7.anihyou.ui.common.BottomDestination.Companion.toBottomDestinationIndex
import com.axiel7.anihyou.ui.common.Theme
import com.axiel7.anihyou.ui.screens.main.composables.MainBottomNavBar
import com.axiel7.anihyou.ui.screens.main.composables.MainNavigationRail
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.ui.theme.dark_scrim
import com.axiel7.anihyou.ui.theme.light_scrim
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.accessToken.collectLatest { App.accessToken = it }
        }

        var deepLink: DeepLink? = null
        when {
            // Widget intent
            intent.action == "media_details" -> {
                deepLink = DeepLink(
                    type = DeepLink.Type.ANIME,// does not mather ANIME or MANGA
                    id = intent.getIntExtra("media_id", 0).toString()
                )
            }
            // Search shortcut
            intent.action == "search" -> {
                deepLink = DeepLink(
                    type = DeepLink.Type.SEARCH,
                    id = true.toString()
                )
            }
            // Login intent or anilist link
            intent.data != null -> {
                viewModel.onIntentDataReceived(intent.data)
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
        }

        //get necessary preferences while on splashscreen
        val startTab = viewModel.startTab.firstBlocking()
        val lastTabOpened = intent.action?.toBottomDestinationIndex() ?: startTab
        //val initialTheme = viewModel.theme.firstBlocking() ?: Theme.FOLLOW_SYSTEM
        //val initialAppColor = viewModel.appColor.firstBlocking()
        //val initialAppColorMode = viewModel.appColorMode.firstBlocking() ?: AppColorMode.DEFAULT

        setContent {
            val theme by viewModel.theme.collectAsStateWithLifecycle()
            val windowSizeClass = calculateWindowSizeClass(this)
            val darkTheme = if (theme == null || theme == Theme.FOLLOW_SYSTEM) isSystemInDarkTheme()
            else theme == Theme.DARK || theme == Theme.BLACK
            val appColor by viewModel.appColor.collectAsStateWithLifecycle()
            val appColorMode by viewModel.appColorMode.collectAsStateWithLifecycle(AppColorMode.DEFAULT)

            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT,
                    ) { darkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        light_scrim.toArgb(),
                        dark_scrim.toArgb(),
                    ) { darkTheme },
                )
                onDispose {}
            }

            AniHyouTheme(
                darkTheme = darkTheme,
                blackColors = theme == Theme.BLACK,
                appColor = appColor,
                appColorMode = appColorMode,
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView(
                        windowSizeClass = windowSizeClass,
                        lastTabOpened = lastTabOpened ?: 0,
                        saveLastTab = viewModel::saveLastTab,
                        deepLink = deepLink,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        viewModel.onIntentDataReceived(intent?.data)
    }
}

@Composable
fun MainView(
    windowSizeClass: WindowSizeClass,
    lastTabOpened: Int,
    saveLastTab: (Int) -> Unit,
    deepLink: DeepLink?,
) {
    val navController = rememberNavController()
    val isCompactScreen = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    Scaffold(
        bottomBar = {
            if (isCompactScreen) {
                MainBottomNavBar(
                    navController = navController,
                    onItemSelected = saveLastTab
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        if (isCompactScreen) {
            MainNavigation(
                navController = navController,
                isCompactScreen = true,
                lastTabOpened = lastTabOpened,
                deepLink = deepLink,
                padding = padding,
            )
        } else {
            val bottomPadding = WindowInsets.navigationBars.asPaddingValues()
            Row {
                MainNavigationRail(
                    navController = navController,
                    onItemSelected = saveLastTab
                )
                MainNavigation(
                    navController = navController,
                    isCompactScreen = false,
                    lastTabOpened = lastTabOpened,
                    deepLink = deepLink,
                    padding = PaddingValues(
                        start = padding.calculateStartPadding(LocalLayoutDirection.current),
                        top = padding.calculateTopPadding(),
                        end = padding.calculateEndPadding(LocalLayoutDirection.current),
                        bottom = bottomPadding.calculateBottomPadding()
                    ),
                )
            }
        }
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
            lastTabOpened = 0,
            saveLastTab = {},
            deepLink = null
        )
    }
}