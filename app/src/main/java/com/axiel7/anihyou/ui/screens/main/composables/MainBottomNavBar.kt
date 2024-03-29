package com.axiel7.anihyou.ui.screens.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.axiel7.anihyou.ui.common.BottomDestination

@Composable
fun MainBottomNavBar(
    navController: NavController,
    onItemSelected: (Int) -> Unit,
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

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar {
            BottomDestination.values.forEachIndexed { index, dest ->
                val isSelected = navBackStackEntry?.destination?.route == dest.route
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
                        onItemSelected(index)
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