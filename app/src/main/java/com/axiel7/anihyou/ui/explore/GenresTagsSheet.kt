package com.axiel7.anihyou.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.TextCheckbox
import com.axiel7.anihyou.ui.theme.AniHyouTheme

private enum class GenresTagsSheetTab {
    GENRES, TAGS;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(value = GENRES, title = R.string.genres),
            TabRowItem(value = TAGS, title = R.string.tags),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenresTagsSheet(
    viewModel: SearchViewModel,
    sheetState: SheetState,
    onDismiss: () -> Unit,
) {
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val genresList by remember {
        derivedStateOf { viewModel.genreCollection.toList().sortedBy { it.first } }
    }
    val tagsList by remember {
        derivedStateOf { viewModel.tagCollection.toList().sortedBy { it.first } }
    }

    LaunchedEffect(viewModel) {
        if (viewModel.genreCollection.isEmpty() || viewModel.tagCollection.isEmpty())
            viewModel.getGenreTagCollection()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = stringResource(R.string.close))
            }
        }

        SegmentedButtons(
            items = GenresTagsSheetTab.tabRows,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            defaultSelectedIndex = selectedTabIndex,
            onItemSelection = {
                selectedTabIndex = it
            }
        )

        if (viewModel.isLoadingGenres) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        when (GenresTagsSheetTab.tabRows[selectedTabIndex].value) {
            GenresTagsSheetTab.GENRES -> {
                LazyColumn {
                    items(genresList) { item ->
                        TextCheckbox(
                            text = item.first,
                            checked = item.second,
                            onCheckedChange = {
                                viewModel.genreCollection[item.first] = it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            GenresTagsSheetTab.TAGS -> {
                LazyColumn {
                    items(tagsList) { item ->
                        TextCheckbox(
                            text = item.first,
                            checked = item.second,
                            onCheckedChange = {
                                viewModel.tagCollection[item.first] = it
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }//: Column
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun GenresTagsSheetPreview() {
    AniHyouTheme {
        Surface {
            GenresTagsSheet(
                viewModel = viewModel(),
                sheetState = rememberModalBottomSheetState(),
                onDismiss = {}
            )
        }
    }
}