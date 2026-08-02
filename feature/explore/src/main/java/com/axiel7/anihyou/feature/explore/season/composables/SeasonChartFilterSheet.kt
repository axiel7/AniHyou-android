package com.axiel7.anihyou.feature.explore.season.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.axiel7.anihyou.core.common.utils.DateUtils
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.model.media.iconSmall
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.SelectableIconToggleButton
import com.axiel7.anihyou.core.ui.composables.sheet.ModalBottomSheet
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonChartFilterSheet(
    initialSeason: AnimeSeason,
    initialSort: MediaSort,
    scope: CoroutineScope,
    sheetState: SheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden),
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
        sheetState = sheetState,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) { dismiss ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(bottom = 42.dp),
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
                        icon = season.iconSmall(),
                        tooltipText = season.localized(),
                        value = season,
                        selectedValue = selectedSeason,
                        onClick = {
                            selectedSeason = season
                        }
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SortMenu(
                    sort = selectedSort,
                    setSort = { selectedSort = it }
                )
                YearMenu(
                    year = selectedYear,
                    setYear = { selectedYear = it }
                )
            }
        }
    }//: Column
}

@Composable
fun YearMenu(
    year: Int,
    setYear: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        FilterChip(
            selected = true,
            onClick = { expanded = !expanded },
            label = { Text(text = year.toString()) },
        )
        DropdownMenuPopup(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 300.dp)
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShapes(),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                DateUtils.seasonYears.fastForEachIndexed { index, item ->
                    DropdownMenuItem(
                        checked = year == item,
                        onCheckedChange = {
                            setYear(item)
                            expanded = false
                        },
                        text = { Text(text = item.toString()) },
                        modifier = Modifier.padding(end = 8.dp),
                        shapes = MenuDefaults.itemShape(index, DateUtils.seasonYears.size)
                    )
                }
            }
        }
    }
}

private val seasonSortEntries = listOf(
    MediaSort.POPULARITY_DESC,
    MediaSort.SCORE_DESC,
    MediaSort.START_DATE_DESC,
    MediaSort.END_DATE_DESC,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SortMenu(
    sort: MediaSort,
    setSort: (MediaSort) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        AssistChip(
            onClick = { expanded = !expanded },
            label = { Text(text = sort.localized()) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.sort_20),
                    contentDescription = stringResource(R.string.sort)
                )
            },
        )
        DropdownMenuPopup(
            expanded = expanded,
            onDismissRequest = {
                setSort(sort)
                expanded = false
            }
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShapes()
            ) {
                seasonSortEntries.fastForEachIndexed { index, item ->
                    DropdownMenuItem(
                        checked = sort == item,
                        onCheckedChange = {
                            setSort(item)
                            expanded = false
                        },
                        text = { Text(text = item.localized()) },
                        modifier = Modifier.padding(end = 8.dp),
                        shapes = MenuDefaults.itemShape(index, seasonSortEntries.size)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun SeasonChartFilterSheetPreview() {
    AniHyouTheme {
        SeasonChartFilterSheet(
            initialSeason = AnimeSeason(2026, MediaSeason.SPRING),
            initialSort = MediaSort.POPULARITY_DESC,
            scope = rememberCoroutineScope(),
            sheetState = rememberBottomSheetState(initialValue = SheetValue.Expanded),
            onDismiss = {},
            setSeason = {},
            setSort = {}
        )
        Box(modifier = Modifier.fillMaxSize())
    }
}