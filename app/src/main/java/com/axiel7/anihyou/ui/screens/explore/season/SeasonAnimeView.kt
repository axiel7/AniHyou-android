package com.axiel7.anihyou.ui.screens.explore.season

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.AnimeSeason
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.SelectableIconToggleButton
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils
import kotlinx.coroutines.launch

const val YEAR_ARGUMENT = "{year}"
const val SEASON_ARGUMENT = "{season}"
const val SEASON_ANIME_DESTINATION = "season/$YEAR_ARGUMENT/$SEASON_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonAnimeView(
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val viewModel: SeasonAnimeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.animeSeasonal.collectAsLazyPagingItems()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()


    if (sheetState.isVisible) {
        SeasonChartFilterSheet(
            sheetState = sheetState,
            initialSeason = uiState.season,
            onDismiss = { scope.launch { sheetState.hide() } },
            onConfirm = {
                viewModel.setSeason(it)
                scope.launch { sheetState.hide() }
            }
        )
    }

    DefaultScaffoldWithMediumTopAppBar(
        title = "${uiState.season.season.localized()} ${uiState.season.year}",
        floatingActionButton = {
            FloatingActionButton(
                onClick = { scope.launch { sheetState.show() } },
                modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())
            ) {
                Icon(
                    painter = painterResource(R.drawable.filter_list_24),
                    contentDescription = "filter"
                )
            }
        },
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        scrollBehavior = topAppBarScrollBehavior,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            if (pagingItems.loadState.refresh is LoadState.Loading) {
                items(13) {
                    MediaItemVerticalPlaceholder()
                }
            }
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { it.id },
                contentType = { it }
            ) { index ->
                pagingItems[index]?.let { item ->
                    MediaItemVertical(
                        title = item.title?.userPreferred ?: "",
                        imageUrl = item.coverImage?.large,
                        modifier = Modifier.wrapContentWidth(),
                        subtitle = {
                            SmallScoreIndicator(score = "${item.meanScore ?: 0}%")
                        },
                        minLines = 2,
                        onClick = {
                            navigateToMediaDetails(item.id)
                        }
                    )
                }
            }
            if (pagingItems.loadState.append is LoadState.Loading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }//: Grid
    }//: Scaffold
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonChartFilterSheet(
    sheetState: SheetState,
    initialSeason: AnimeSeason,
    onDismiss: () -> Unit,
    onConfirm: (AnimeSeason) -> Unit,
) {
    var selectedYear by remember { mutableIntStateOf(initialSeason.year) }
    var selectedSeason by remember { mutableStateOf(initialSeason.season) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
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
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.cancel))
                }

                Button(onClick = {
                    onConfirm(AnimeSeason(selectedYear, selectedSeason))
                }) {
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
                MediaSeason.knownValues().forEach { season ->
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
        }
    }//: Column
}

@Preview
@Composable
fun SeasonAnimeViewPreview() {
    AniHyouTheme {
        Surface {
            SeasonAnimeView(
                navigateBack = {},
                navigateToMediaDetails = {}
            )
        }
    }
}