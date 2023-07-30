package com.axiel7.anihyou.ui.screens.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.ui.base.BottomDestination
import com.axiel7.anihyou.ui.base.BottomDestination.Companion.Icon

@Composable
fun MainNavigationRail(
    navController: NavController,
    lastTabOpened: Int,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    var selectedItem by PreferencesDataStore.rememberPreference(
        PreferencesDataStore.LAST_TAB_PREFERENCE_KEY,
        lastTabOpened
    )

    NavigationRail(
        header = {
            FloatingActionButton(
                onClick = {
                    selectedItem = 4
                    navController.navigate(BottomDestination.Explore.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ) {
                BottomDestination.Explore.Icon(
                    selected = navBackStackEntry?.destination?.route == BottomDestination.Explore.route
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom
        ) {
            BottomDestination.railValues.forEachIndexed { index, dest ->
                val isSelected = navBackStackEntry?.destination?.route == dest.route
                NavigationRailItem(
                    selected = isSelected,
                    onClick = {
                        selectedItem = index
                        navController.navigate(dest.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { dest.Icon(selected = isSelected) },
                    label = { Text(text = stringResource(dest.title)) }
                )
            }
        }
    }
}