package com.axiel7.anihyou.ui.screens.main.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import com.axiel7.anihyou.core.ui.common.BottomDestination
import com.axiel7.anihyou.core.ui.common.BottomDestination.Companion.testTag
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Route

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainBottomNavBar(
    currentTopRoute: Route,
    isVisible: Boolean,
    onItemSelected: (Int) -> Unit,
) {
    val navActionManager = LocalNavActionManager.current
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar {
            BottomDestination.values.forEachIndexed { index, dest ->
                val isSelected = dest.route == currentTopRoute
                NavigationBarItem(
                    icon = {
                        dest.Icon(selected = isSelected)
                    },
                    modifier = Modifier.semantics {
                        testTagsAsResourceId = true
                        testTag = dest.testTag
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
                            navActionManager.navigate(dest.route)
                        }
                    }
                )
            }
        }
    }
}