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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.screens.explore.search.SearchViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MediaSearchGenresChips(
    viewModel: SearchViewModel,
    performSearch: MutableState<Boolean>,
    searchByGenre: MutableState<Boolean>
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    if (sheetState.isVisible) {
        GenresTagsSheet(
            viewModel = viewModel,
            sheetState = sheetState,
            bottomPadding = bottomBarPadding,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    if (viewModel.selectedGenres.isNotEmpty() || viewModel.selectedTags.isNotEmpty()) {
                        searchByGenre.value = true
                        performSearch.value = true
                    }
                }
            }
        )
    }

    FlowRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        viewModel.selectedGenres.forEach { (genre, isSelected) ->
            if (isSelected) {
                InputChip(
                    selected = false,
                    onClick = { viewModel.genreCollection[genre] = false },
                    label = { Text(text = genre) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close_20),
                            contentDescription = "remove"
                        )
                    }
                )
            }
        }
        viewModel.selectedTags.forEach { (tag, isSelected) ->
            if (isSelected) {
                InputChip(
                    selected = false,
                    onClick = { viewModel.tagCollection[tag] = false },
                    label = { Text(text = tag) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close_20),
                            contentDescription = "remove"
                        )
                    }
                )
            }
        }
        AssistChip(
            onClick = { scope.launch { sheetState.show() } },
            label = { Text(text = stringResource(R.string.add_genre)) },
            leadingIcon = {
                Icon(painter = painterResource(R.drawable.add_24), contentDescription = "add")
            }
        )
    }//: FlowRow
}