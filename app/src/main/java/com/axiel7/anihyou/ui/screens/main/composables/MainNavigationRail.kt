package com.axiel7.anihyou.ui.screens.main.composables

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
            val dest = BottomDestination.Explore
            val isSelected = navigator.state.topLevelRoute == BottomDestination.Explore.route
            val image = AnimatedImageVector.animatedVectorResource(dest.icon)
            var atEnd by rememberSaveable { mutableStateOf(isSelected) }
            LaunchedEffect(isSelected) {
                atEnd = isSelected
            }

            FloatingActionButton(
                onClick = {
                    onItemSelected(4)
                    navigator.navigate(dest.route)
                }
            ) {
                Icon(
                    painter = rememberAnimatedVectorPainter(image, atEnd),
                    contentDescription = stringResource(dest.title),
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
                val image = AnimatedImageVector.animatedVectorResource(dest.icon)
                var atEnd by rememberSaveable { mutableStateOf(isSelected) }
                LaunchedEffect(isSelected) {
                    atEnd = isSelected
                }

                NavigationRailItem(
                    selected = isSelected,
                    onClick = {
                        onItemSelected(index)
                        navigator.navigate(dest.route)
                    },
                    icon = {
                        Icon(
                            painter = rememberAnimatedVectorPainter(image, atEnd),
                            contentDescription = stringResource(dest.title),
                        )
                    },
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