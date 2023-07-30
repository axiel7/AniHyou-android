package com.axiel7.anihyou.ui.screens.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.ui.base.BottomDestination

@Composable
fun MainBottomNavBar(
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
    var selectedItem by PreferencesDataStore.rememberPreference(
        PreferencesDataStore.LAST_TAB_PREFERENCE_KEY,
        lastTabOpened
    )

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