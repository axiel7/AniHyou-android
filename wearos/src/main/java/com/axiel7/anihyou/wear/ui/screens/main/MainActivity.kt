package com.axiel7.anihyou.wear.ui.screens.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.tooling.preview.devices.WearDevices
import com.axiel7.anihyou.wear.ui.navigation.Routes
import com.axiel7.anihyou.wear.ui.screens.login.LoginView
import com.axiel7.anihyou.wear.ui.screens.usermedialist.UserMediaListHostView
import com.axiel7.anihyou.wear.ui.screens.usermedialist.edit.EditMediaView
import com.axiel7.anihyou.wear.ui.theme.AniHyouTheme
import com.google.android.horologist.compose.layout.AppScaffold
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        viewModel.onCreateActivity()
        intent.data?.let { viewModel.onIntentDataReceived(it) }

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            WearApp(
                uiState = uiState,
                event = viewModel,
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.onIntentDataReceived(intent.data)
    }
}

@Composable
fun WearApp(
    uiState: MainUiState,
    event: MainEvent?,
) {
    val navController = rememberSwipeDismissableNavController()

    AniHyouTheme {
        AppScaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = if (uiState.isLoggedIn) Routes.MEDIA_LIST else Routes.LOGIN
            ) {
                composable(Routes.LOGIN) {
                    LoginView()
                }

                composable(Routes.MEDIA_LIST) {
                    UserMediaListHostView(
                        goToEditMedia = { navController.navigate("${Routes.EDIT_MEDIA}/$it") }
                    )
                }

                composable(
                    route = "${Routes.EDIT_MEDIA}/{${Routes.Arguments.ID}}",
                    arguments = listOf(
                        navArgument(Routes.Arguments.ID) { type = NavType.IntType }
                    )
                ) {
                    EditMediaView()
                }
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(
        uiState = MainUiState(),
        event = null,
    )
}