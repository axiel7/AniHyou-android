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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.SelectableGenre
import com.axiel7.anihyou.ui.screens.explore.search.genretag.GenresTagsSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MediaSearchGenresChips(
    externalGenre: SelectableGenre?,
    externalTag: SelectableGenre?,
    onGenreTagSelected: (selectedGenres: List<String>, selectedTags: List<String>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val selectedGenres = remember { mutableStateListOf<String>() }
    val selectedTags = remember { mutableStateListOf<String>() }

    if (sheetState.isVisible) {
        GenresTagsSheet(
            sheetState = sheetState,
            bottomPadding = bottomBarPadding,
            externalGenre = externalGenre,
            externalTag = externalTag,
            onDismiss = { genres, tags ->
                scope.launch {
                    selectedGenres.clear()
                    selectedGenres.addAll(genres)

                    selectedTags.clear()
                    selectedTags.addAll(tags)

                    sheetState.hide()
                    onGenreTagSelected(genres, tags)
                }
            }
        )
    }

    FlowRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        selectedGenres.forEach { genre ->
            InputChip(
                selected = false,
                onClick = {
                    selectedGenres.remove(genre)
                    onGenreTagSelected(selectedGenres, selectedTags)
                },
                label = { Text(text = genre) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.close_20),
                        contentDescription = "remove"
                    )
                }
            )
        }
        selectedTags.forEach { tag ->
            InputChip(
                selected = false,
                onClick = {
                    selectedTags.remove(tag)
                    onGenreTagSelected(selectedGenres, selectedTags)
                },
                label = { Text(text = tag) },
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