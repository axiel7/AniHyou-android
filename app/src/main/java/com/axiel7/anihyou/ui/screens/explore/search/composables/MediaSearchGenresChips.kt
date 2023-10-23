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
import com.axiel7.anihyou.data.model.genre.GenresAndTagsForSearch
import com.axiel7.anihyou.data.model.genre.SelectableGenre
import com.axiel7.anihyou.data.model.genre.SelectableGenre.Companion.genreTagLocalized
import com.axiel7.anihyou.ui.composables.common.InputChipError
import com.axiel7.anihyou.ui.screens.explore.search.genretag.GenresTagsSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MediaSearchGenresChips(
    externalGenre: SelectableGenre?,
    externalTag: SelectableGenre?,
    onGenreTagStateChanged: (GenresAndTagsForSearch) -> Unit
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    val selectedGenres = remember {
        if (externalGenre != null) mutableStateListOf(externalGenre.name)
        else mutableStateListOf()
    }
    val excludedGenres = remember { mutableStateListOf<String>() }
    val selectedTags = remember {
        if (externalTag != null) mutableStateListOf(externalTag.name)
        else mutableStateListOf()
    }
    val excludedTags = remember { mutableStateListOf<String>() }

    fun getGenresAndTagsForSearch() =
        GenresAndTagsForSearch(
            genreIn = selectedGenres,
            genreNot = excludedGenres,
            tagIn = selectedTags,
            tagNot = excludedTags,
        )

    if (sheetState.isVisible) {
        GenresTagsSheet(
            sheetState = sheetState,
            bottomPadding = bottomBarPadding,
            externalGenre = externalGenre,
            externalTag = externalTag,
            onDismiss = {
                scope.launch {
                    selectedGenres.clear()
                    selectedGenres.addAll(it.genreIn)
                    excludedGenres.clear()
                    excludedGenres.addAll(it.genreNot)

                    selectedTags.clear()
                    selectedTags.addAll(it.tagIn)
                    excludedTags.clear()
                    excludedTags.addAll(it.tagNot)

                    sheetState.hide()
                    onGenreTagStateChanged(it)
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
                    selectedGenres.remove(genre)
                    onGenreTagStateChanged(getGenresAndTagsForSearch())
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
                    excludedGenres.remove(genre)
                    onGenreTagStateChanged(getGenresAndTagsForSearch())
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
                    selectedTags.remove(tag)
                    onGenreTagStateChanged(getGenresAndTagsForSearch())
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
                    excludedTags.remove(tag)
                    onGenreTagStateChanged(getGenresAndTagsForSearch())
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