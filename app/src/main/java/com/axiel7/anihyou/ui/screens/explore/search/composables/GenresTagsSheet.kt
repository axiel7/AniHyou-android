package com.axiel7.anihyou.ui.screens.explore.search.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.common.TabRowItem
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.TextCheckbox
import com.axiel7.anihyou.ui.screens.explore.search.SearchViewModel
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

private enum class GenresTagsSheetTab {
    GENRES, TAGS;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(value = GENRES, title = R.string.genres),
            TabRowItem(value = TAGS, title = R.string.tags),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GenresTagsSheet(
    viewModel: SearchViewModel,
    sheetState: SheetState,
    bottomPadding: Dp,
    onDismiss: () -> Unit,
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val genresList by remember {
        derivedStateOf { viewModel.genreCollection.toList().sortedBy { it.first } }
    }
    val tagsList by remember {
        derivedStateOf { viewModel.tagCollection.toList().sortedBy { it.first } }
    }
    var filter by remember { mutableStateOf("") }
    val filteredList by remember {
        derivedStateOf {
            when (GenresTagsSheetTab.tabRows[selectedTabIndex].value) {
                GenresTagsSheetTab.GENRES ->
                    if (filter.isNotBlank())
                        genresList.filter { it.first.contains(filter, ignoreCase = true) }
                    else genresList

                GenresTagsSheetTab.TAGS ->
                    if (filter.isNotBlank())
                        tagsList.filter { it.first.contains(filter, ignoreCase = true) }
                    else tagsList
            }
        }
    }
    val scope = rememberCoroutineScope()
    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    SideEffect {
        scope.launch { if (isKeyboardVisible) sheetState.expand() }
    }

    LaunchedEffect(viewModel) {
        if (viewModel.genreCollection.isEmpty() || viewModel.tagCollection.isEmpty())
            viewModel.getGenreTagCollection()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .imePadding()
                .padding(bottom = bottomPadding)
                .bringIntoViewRequester(bringIntoViewRequester)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { viewModel.unselectAllGenresAndTags() },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.clear),
                        color = MaterialTheme.colorScheme.error
                    )
                }

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

            OutlinedTextField(
                value = filter,
                onValueChange = {
                    filter = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = {
                    Text(text = stringResource(R.string.filter))
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search_24),
                        contentDescription = "search"
                    )
                },
                trailingIcon = {
                    if (filter.isNotEmpty()) {
                        IconButton(onClick = { filter = "" }) {
                            Icon(
                                painter = painterResource(R.drawable.cancel_24),
                                contentDescription = "clear"
                            )
                        }
                    }
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

            LazyColumn {
                items(filteredList) { item ->
                    TextCheckbox(
                        text = item.first,
                        checked = item.second,
                        onCheckedChange = {
                            when (GenresTagsSheetTab.tabRows[selectedTabIndex].value) {
                                GenresTagsSheetTab.GENRES -> viewModel.genreCollection[item.first] =
                                    it

                                GenresTagsSheetTab.TAGS -> viewModel.tagCollection[item.first] = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }//: Column
    }//: Sheet
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
                bottomPadding = 0.dp,
                onDismiss = {}
            )
        }
    }
}