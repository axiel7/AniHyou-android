package com.axiel7.anihyou.ui.screens.explore.season.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.ui.composables.SelectableIconToggleButton
import com.axiel7.anihyou.ui.composables.sheet.ModalBottomSheet
import com.axiel7.anihyou.utils.DateUtils
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonChartFilterSheet(
    initialSeason: AnimeSeason,
    initialSort: MediaSort,
    scope: CoroutineScope,
    onDismiss: () -> Unit,
    setSeason: (AnimeSeason) -> Unit,
    setSort: (MediaSort) -> Unit,
) {
    var selectedYear by remember { mutableIntStateOf(initialSeason.year) }
    var selectedSeason by remember { mutableStateOf(initialSeason.season) }
    var selectedSort by remember { mutableStateOf(initialSort) }

    val listState = rememberLazyListState()

    LaunchedEffect(initialSeason.year) {
        val index = DateUtils.seasonYears.indexOf(initialSeason.year)
        if (index >= 0) listState.scrollToItem(index)
    }

    ModalBottomSheet(
        onDismissed = onDismiss,
        scope = scope,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) { dismiss ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = dismiss) {
                    Text(text = stringResource(R.string.cancel))
                }

                Button(
                    onClick = {
                        setSeason(AnimeSeason(selectedYear, selectedSeason))
                        setSort(selectedSort)
                        dismiss()
                    }
                ) {
                    Text(text = stringResource(R.string.apply))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MediaSeason.knownEntries.forEach { season ->
                    SelectableIconToggleButton(
                        icon = season.icon(),
                        tooltipText = season.localized(),
                        value = season,
                        selectedValue = selectedSeason,
                        onClick = {
                            selectedSeason = season
                        }
                    )
                }
            }

            LazyRow(
                state = listState,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(DateUtils.seasonYears) {
                    FilterChip(
                        selected = selectedYear == it,
                        onClick = { selectedYear = it },
                        label = { Text(text = it.toString()) },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            SortMenu(
                sort = selectedSort,
                setSort = { selectedSort = it }
            )
        }
    }//: Column
}

private val seasonSortEntries = listOf(
    MediaSort.POPULARITY_DESC,
    MediaSort.SCORE_DESC,
    MediaSort.START_DATE_DESC,
    MediaSort.END_DATE_DESC,
)

@Composable
private fun SortMenu(
    sort: MediaSort,
    setSort: (MediaSort) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        AssistChip(
            onClick = { expanded = !expanded },
            label = { Text(text = sort.localized()) },
            modifier = Modifier.padding(top = 8.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.sort_24),
                    contentDescription = stringResource(R.string.sort)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.arrow_drop_down_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                setSort(sort)
                expanded = false
            }
        ) {
            seasonSortEntries.forEach {
                DropdownMenuItem(
                    text = { Text(text = it.localized()) },
                    onClick = {
                        setSort(it)
                        expanded = false
                    },
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
        }
    }
}