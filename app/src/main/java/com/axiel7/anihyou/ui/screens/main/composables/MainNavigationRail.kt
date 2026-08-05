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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.axiel7.anihyou.core.ui.common.BottomDestination
import com.axiel7.anihyou.core.ui.common.navigation.Navigator

@Composable
fun MainNavigationRail(
    navigator: Navigator,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier,
        header = {
            FloatingActionButton(
                onClick = {
                    onItemSelected(4)
                    navigator.navigate(BottomDestination.Explore.route)
                }
            ) {
                BottomDestination.Explore.Icon(
                    selected = navigator.state.topLevelRoute == BottomDestination.Explore.route
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
                val isSelected = navigator.state.topLevelRoute == dest.route
                NavigationRailItem(
                    selected = isSelected,
                    onClick = {
                        onItemSelected(index)
                        navigator.navigate(dest.route)
                    },
                    icon = { dest.Icon(selected = isSelected) },
                    label = {
                        Text(
                            text = stringResource(dest.title),
                            textAlign = TextAlign.Center
                        )
                    }
                )
            }
        }
    }
}