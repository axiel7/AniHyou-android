package com.axiel7.anihyou.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.AnimeSeason
import com.axiel7.anihyou.data.model.icon
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.SelectableIconToggleButton
import com.axiel7.anihyou.ui.composables.SmallScoreIndicator
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils
import kotlinx.coroutines.launch

const val SEASON_ANIME_DESTINATION = "season/{year}/{season}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonAnimeView(
    initialSeason: AnimeSeason,
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val viewModel: ExploreViewModel = viewModel()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val listState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    var season by remember { mutableStateOf(initialSeason) }

    DefaultScaffoldWithMediumTopAppBar(
        title = season.localized(),
        floatingActionButton = {
            FloatingActionButton(onClick = { scope.launch { sheetState.show() } }) {
                Icon(painter = painterResource(R.drawable.filter_list_24), contentDescription = "filter")
            }
        },
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            items(
                items = viewModel.animeSeasonal,
                key = { it.id },
                contentType = { it }
            ) { item ->
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
            if (viewModel.isLoading) {
                items(13) {
                    MediaItemVerticalPlaceholder()
                }
            }
        }//: Grid
    }

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getAnimeSeasonal(
            season = season.season,
            year = season.year
        )
    }

    if (sheetState.isVisible) {
        SeasonChartFilterSheet(
            sheetState = sheetState,
            initialSeason = season,
            onDismiss = { scope.launch { sheetState.hide() } },
            onConfirm = {
                season = it
                scope.launch { sheetState.hide() }
            }
        )
    }

    LaunchedEffect(season) {
        if (!viewModel.isLoading) {
            viewModel.getAnimeSeasonal(
                season = season.season,
                year = season.year,
                resetPage = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonChartFilterSheet(
    sheetState: SheetState,
    initialSeason: AnimeSeason,
    onDismiss: () -> Unit,
    onConfirm: (AnimeSeason) -> Unit,
) {
    var selectedYear by remember { mutableStateOf(initialSeason.year) }
    var selectedSeason by remember { mutableStateOf(initialSeason.season) }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
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
    }
}

@Preview
@Composable
fun SeasonAnimeViewPreview() {
    AniHyouTheme {
        Surface {
            SeasonAnimeView(
                initialSeason = AnimeSeason(
                    year = 2023,
                    season = MediaSeason.SPRING
                ),
                navigateBack = {},
                navigateToMediaDetails = {}
            )
        }
    }
}