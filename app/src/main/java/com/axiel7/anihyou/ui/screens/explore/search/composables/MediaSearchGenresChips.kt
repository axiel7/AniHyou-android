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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.SelectableGenre
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MediaSearchGenresChips(
    genreCollection: List<SelectableGenre>,
    tagCollection: List<SelectableGenre>,
    onGenreSelected: (SelectableGenre) -> Unit,
    onTagSelected: (SelectableGenre) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    if (sheetState.isVisible) {
        GenresTagsSheet(
            genreCollection = genreCollection,
            tagCollection = tagCollection,
            sheetState = sheetState,
            bottomPadding = bottomBarPadding,
            onGenreSelected = onGenreSelected,
            onTagSelected = onTagSelected,
            fetchCollection = {},
            isLoadingCollection = false,
            unselectAll = {},
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    //TODO: manually perform search?
                }
            }
        )
    }

    FlowRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        genreCollection.filter { it.isSelected }.forEach {
            InputChip(
                selected = false,
                onClick = {
                    onGenreSelected(it.copy(isSelected = false))
                },
                label = { Text(text = it.name) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.close_20),
                        contentDescription = "remove"
                    )
                }
            )
        }
        tagCollection.filter { it.isSelected }.forEach {
            InputChip(
                selected = false,
                onClick = {
                    onTagSelected(it.copy(isSelected = false))
                },
                label = { Text(text = it.name) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.close_20),
                        contentDescription = "remove"
                    )
                }
            )
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