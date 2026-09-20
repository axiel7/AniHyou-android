package com.axiel7.anihyou.feature.addrecommendation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.network.SearchMediaQuery
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.composables.common.singleClick
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.composables.sheet.ModalBottomSheet
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    mediaType: MediaType,
    onSelected: (SearchMediaQuery.Medium) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    val viewModel: SimpleMediaSearchViewModel = koinViewModel { parametersOf(mediaType) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchContent(
        uiState = uiState,
        event = viewModel,
        onDismiss = onDismiss,
        onSelected = onSelected,
        sheetState = sheetState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
    uiState: SimpleMediaSearchUiState,
    event: SimpleMediaSearchEvent?,
    onDismiss: () -> Unit,
    onSelected: (SearchMediaQuery.Medium) -> Unit,
    sheetState: SheetState,
) {
    val listState = rememberLazyGridState()
    val textFieldState = rememberTextFieldState(initialText = uiState.searchQuery)
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val blurAdult = LocalBlurAdult.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissed = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            TextField(
                state = textFieldState,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(text = stringResource(R.string.search))
                },
                leadingIcon = {
                    IconButton(
                        onClick = singleClick(onDismiss),
                        shapes = IconButtonDefaults.shapes(),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24),
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textFieldState.clearText()
                                event?.setQuery("")
                                event?.search()
                            },
                            shapes = IconButtonDefaults.shapes()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close_24),
                                contentDescription = stringResource(R.string.clear)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                onKeyboardAction = KeyboardActionHandler { defaultAction ->
                    keyboardController?.hide()
                    event?.setQuery(textFieldState.text.toString())
                    event?.search()
                    defaultAction()
                },
                lineLimits = TextFieldLineLimits.SingleLine,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            Scaffold(
                Modifier.fillMaxSize()
            ) { padding ->
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
                    modifier = Modifier.padding(
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = padding.calculateBottomPadding(),
                        start = padding.calculateStartPadding(LocalLayoutDirection.current),
                        end = padding.calculateEndPadding(LocalLayoutDirection.current)
                    ),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    if (uiState.isSearching) {
                        items(10) {
                            MediaItemVerticalPlaceholder()
                        }
                    } else {
                        items(
                            items = uiState.searchResult,
                            key = { it.id },
                        ) { item ->
                            MediaItemVertical(
                                title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                                imageUrl = item.coverImage?.large,
                                blurImage = blurAdult && item.basicMediaDetails.isAdult == true,
                                onClick = {
                                    onSelected(item)
                                    onDismiss()
                                },
                                status = item.mediaListEntry?.basicMediaListEntry?.status,
                            )
                        }
                    }
                }
            }
        }
    }
}