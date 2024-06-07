package com.axiel7.anihyou.ui.screens.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.axiel7.anihyou.ui.common.BottomDestination
import com.axiel7.anihyou.ui.common.navigation.NavActionManager

@Composable
fun MainBottomNavBar(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry?,
    navActionManager: NavActionManager,
    isVisible: Boolean,
    onItemSelected: (Int) -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar {
            BottomDestination.values.forEachIndexed { index, dest ->
                val isSelected = navBackStackEntry?.destination?.hierarchy?.any {
                    it.hasRoute(dest.route::class)
                } == true
                NavigationBarItem(
                    icon = {
                        dest.Icon(selected = isSelected)
                    },
                    label = {
                        Text(
                            text = stringResource(dest.title),
                            textAlign = TextAlign.Center
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (isSelected) {
                            when (dest) {
                                BottomDestination.Explore -> {
                                    navActionManager.toSearch()
                                }

                                else -> {}
                            }
                        } else {
                            onItemSelected(index)
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}