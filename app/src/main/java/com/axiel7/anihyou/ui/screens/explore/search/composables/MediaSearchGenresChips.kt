package com.axiel7.anihyou.ui.screens.explore.search.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.data.model.genre.SelectableGenre
import com.axiel7.anihyou.data.model.genre.SelectableGenre.Companion.genreTagLocalized
import com.axiel7.anihyou.ui.composables.common.InputChipError
import com.axiel7.anihyou.ui.screens.explore.search.genretag.GenresTagsSheet
import com.axiel7.anihyou.ui.screens.explore.search.genretag.GenresTagsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MediaSearchGenresChips(
    externalGenre: SelectableGenre?,
    externalTag: SelectableGenre?,
    onGenreTagStateChanged: (GenresAndTagsForSearch) -> Unit
) {
    val viewModel: GenresTagsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // TODO: pass these by savedStateHandle?
    LaunchedEffect(externalGenre) {
        if (uiState.externalGenre == null && externalGenre != null)
            viewModel.setExternalGenre(externalGenre)
    }
    LaunchedEffect(externalTag) {
        if (uiState.externalTag == null && externalTag != null)
            viewModel.setExternalTag(externalTag)
    }

    val selectedGenres = uiState.genresAndTagsForSearch.genreIn
    val excludedGenres = uiState.genresAndTagsForSearch.genreNot
    val selectedTags = uiState.genresAndTagsForSearch.tagIn
    val excludedTags = uiState.genresAndTagsForSearch.tagNot

    if (sheetState.isVisible) {
        GenresTagsSheet(
            uiState = uiState,
            event = viewModel,
            sheetState = sheetState,
            bottomPadding = bottomBarPadding,
            onDismiss = {
                scope.launch {
                    viewModel.onDismissSheet()
                    sheetState.hide()
                    onGenreTagStateChanged(uiState.genresAndTagsForSearch)
                }
            }
        )
    }

    FlowRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        selectedGenres.forEach { genre ->
            InputChip(
                selected = true,
                onClick = {
                    scope.launch {
                        onGenreTagStateChanged(
                            viewModel.onGenreRemoved(genre)
                        )
                    }
                },
                label = { Text(text = genre.genreTagLocalized()) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.close_20),
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            )
        }
        excludedGenres.forEach { genre ->
            InputChipError(
                selected = true,
                onClick = {
                    scope.launch {
                        onGenreTagStateChanged(
                            viewModel.onGenreRemoved(genre)
                        )
                    }
                },
                text = genre.genreTagLocalized(),
                icon = R.drawable.close_20,
                iconDescription = stringResource(R.string.delete)
            )
        }
        selectedTags.forEach { tag ->
            InputChip(
                selected = true,
                onClick = {
                    scope.launch {
                        onGenreTagStateChanged(
                            viewModel.onTagRemoved(tag)
                        )
                    }
                },
                label = { Text(text = tag) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.close_20),
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            )
        }
        excludedTags.forEach { tag ->
            InputChipError(
                selected = true,
                onClick = {
                    scope.launch {
                        onGenreTagStateChanged(
                            viewModel.onTagRemoved(tag)
                        )
                    }
                },
                text = tag,
                icon = R.drawable.close_20,
                iconDescription = stringResource(R.string.delete)
            )
        }
        AssistChip(
            onClick = { scope.launch { sheetState.show() } },
            label = { Text(text = stringResource(R.string.add_genre)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.add_24),
                    contentDescription = stringResource(R.string.add)
                )
            }
        )
    }//: FlowRow
}