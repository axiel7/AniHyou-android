package com.axiel7.anihyou.feature.addrecommendation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.network.SearchMediaQuery
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.composables.common.singleClick
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.composables.sheet.ModalBottomSheet
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleMediaSearchSheetView(
    mediaType: MediaType,
    onSelected: (SearchMediaQuery.Medium) -> Unit,
    sheetState: SheetState,
) {
    val viewModel: SimpleMediaSearchViewModel = koinViewModel { parametersOf(mediaType) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchContent(
        uiState = uiState,
        event = viewModel,
        onSelected = onSelected,
        sheetState = sheetState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
    uiState: SimpleMediaSearchUiState,
    event: SimpleMediaSearchEvent?,
    onSelected: (SearchMediaQuery.Medium) -> Unit,
    sheetState: SheetState,
) {
    val listState = rememberLazyListState()
    val textFieldState = rememberTextFieldState(initialText = uiState.searchQuery)
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val blurAdult = LocalBlurAdult.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissed = {},
        sheetState = sheetState,
        windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { dismiss ->
        Column {
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
                        onClick = singleClick(dismiss),
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (uiState.isSearching) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                } else {
                    items(
                        items = uiState.searchResult,
                        key = { it.id }
                    ) { media ->
                        MediaItemHorizontal(
                            title = media.basicMediaDetails.title?.userPreferred.orEmpty(),
                            imageUrl = media.coverImage?.large,
                            blurImage = blurAdult && media.basicMediaDetails.isAdult == true,
                            score = media.averageScore ?: 0,
                            format = media.format ?: MediaFormat.UNKNOWN__,
                            year = media.startDate?.year,
                            mediaStatus = media.status,
                            episodes = media.basicMediaDetails.episodes,
                            chapters = media.basicMediaDetails.chapters,
                            duration = media.duration,
                            genres = media.genres?.filterNotNull()?.toImmutableList(),
                            onClick = {
                                onSelected(media)
                                dismiss()
                            },
                        )
                    }
                }
            }
        }
    }
}