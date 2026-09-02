package com.axiel7.anihyou.feature.usermedialist.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.feature.usermedialist.UserMediaListEvent
import com.axiel7.anihyou.feature.usermedialist.UserMediaListUiState
import com.axiel7.anihyou.feature.usermedialist.composables.SortMenu
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.flow.drop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopSearchBar(
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    textFieldState: TextFieldState,
    isSearchFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isSearchFocused) {
        if (!isSearchFocused) return@LaunchedEffect

        snapshotFlow { scrollBehavior.state.contentOffset }
            .drop(1)
            .collect {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
    }

    BackHandler(enabled = isSearchFocused) {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    TopAppBar(
        title = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(min = 360.dp, max = 720.dp)
                    .height(48.dp)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 4.dp, end = 16.dp)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Crossfade(
                            targetState = isSearchFocused,
                            label = "SearchIconSwap"
                        ) { focused ->
                            if (focused) {
                                IconButton(
                                    onClick = {
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.arrow_back_24),
                                        contentDescription = stringResource(R.string.action_back),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = {
                                        focusRequester.requestFocus()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.search_24),
                                        contentDescription = stringResource(R.string.search),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        if (textFieldState.text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_my_list),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        BasicTextField(
                            state = textFieldState,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            lineLimits = TextFieldLineLimits.SingleLine,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { onFocusChange(it.isFocused) }
                                .focusRequester(focusRequester)
                        )
                    }
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = { textFieldState.clearText() },
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close_24),
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        actions = {
            Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
                IconButton(
                    onClick = { event?.toggleSortMenu(true) },
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sort_24),
                        contentDescription = stringResource(R.string.sort)
                    )
                }
                SortMenu(
                    expanded = uiState.sortMenuExpanded,
                    sort = uiState.sort,
                    onDismiss = {
                        event?.toggleSortMenu(false)
                        event?.setSort(it)
                    }
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )

}