package com.axiel7.anihyou.feature.explore.season

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.model.genre.SelectableGenre.Companion.genreTagLocalized
import com.axiel7.anihyou.core.network.SeasonalAnimeQuery
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.core.ui.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.explore.season.composables.SeasonChartFilterSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun SeasonAnimeView(
    navActionManager: NavActionManager
) {
    val viewModel: SeasonAnimeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SeasonAnimeContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeasonAnimeContent(
    uiState: SeasonAnimeUiState,
    event: SeasonAnimeEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    var showFilterSheet by remember { mutableStateOf(false) }
    var showEditSheet by remember { mutableStateOf(false) }

    if (showFilterSheet && uiState.season != null) {
        SeasonChartFilterSheet(
            initialSeason = uiState.season,
            initialSort = uiState.sort,
            scope = scope,
            onDismiss = { showFilterSheet = false },
            setSeason = { event?.setSeason(it) },
            setSort = { event?.onChangeSort(it) }
        )
    }

    if (showEditSheet && uiState.selectedItem != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedItem.basicMediaDetails,
            listEntry = uiState.selectedItem.mediaListEntry?.basicMediaListEntry,
            scope = scope,
            onEntryUpdated = {
                event?.onUpdateListEntry(it)
            },
            onDismissed = { showEditSheet = false }
        )
    }

    DefaultScaffoldWithMediumTopAppBar(
        title = uiState.season?.localized().orEmpty(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showFilterSheet = true },
                modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())
            ) {
                Icon(
                    painter = painterResource(R.drawable.filter_list_24),
                    contentDescription = stringResource(R.string.filter)
                )
            }
        },
        navigationIcon = {
            BackIconButton(onClick = navActionManager::goBack)
        },
        actions = {
            IconButton(
                onClick = {
                    val value = if (uiState.listStyle == ListStyle.STANDARD) ListStyle.GRID
                    else ListStyle.STANDARD
                    event?.onChangeListStyle(value)
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = if (uiState.listStyle == ListStyle.STANDARD) R.drawable.grid_view_24
                        else R.drawable.format_list_bulleted_24
                    ),
                    contentDescription = stringResource(R.string.list_style)
                )
            }
        },
        scrollBehavior = topAppBarScrollBehavior,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        if (uiState.listStyle == ListStyle.STANDARD) {
            SeasonalList(
                uiState = uiState,
                event = event,
                modifier = Modifier
                    .padding(padding)
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                onClickItem = {
                    navActionManager.toMediaDetails(it.id)
                },
                onLongClickItem = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    event?.selectItem(it)
                    showEditSheet = true
                }
            )
        } else {
            SeasonalGrid(
                uiState = uiState,
                event = event,
                modifier = Modifier
                    .padding(padding)
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                onClickItem = {
                    navActionManager.toMediaDetails(it.id)
                },
                onLongClickItem = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    event?.selectItem(it)
                    showEditSheet = true
                }
            )
        }
    }//: Scaffold
}

@Composable
private fun SeasonalGrid(
    uiState: SeasonAnimeUiState,
    event: SeasonAnimeEvent?,
    modifier: Modifier,
    onClickItem: (SeasonalAnimeQuery.Medium) -> Unit,
    onLongClickItem: (SeasonalAnimeQuery.Medium) -> Unit,
) {
    val listState = rememberLazyGridState()
    listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
        modifier = modifier,
        state = listState,
        contentPadding = WindowInsets.navigationBars.asPaddingValues(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        items(
            items = uiState.animeSeasonal,
            contentType = { it }
        ) { item ->
            MediaItemVertical(
                title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                imageUrl = item.coverImage?.large,
                modifier = Modifier.wrapContentWidth(),
                subtitle = {
                    item.meanScore?.let { meanScore ->
                        SmallScoreIndicator(score = meanScore)
                    }
                },
                status = item.mediaListEntry?.basicMediaListEntry?.status,
                minLines = 2,
                onClick = { onClickItem(item) },
                onLongClick = { onLongClickItem(item) }
            )
        }
        if (uiState.isLoading) {
            items(13) {
                MediaItemVerticalPlaceholder()
            }
        }
    }//: Grid
}

@Composable
private fun SeasonalList(
    uiState: SeasonAnimeUiState,
    event: SeasonAnimeEvent?,
    modifier: Modifier,
    onClickItem: (SeasonalAnimeQuery.Medium) -> Unit,
    onLongClickItem: (SeasonalAnimeQuery.Medium) -> Unit,
) {
    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        items(
            items = uiState.animeSeasonal,
            contentType = { it }
        ) { item ->
            MediaItemHorizontal(
                title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                imageUrl = item.coverImage?.large,
                subtitle1 = {
                    item.nextAiringEpisode?.let { nextAiringEpisode ->
                        Text(
                            text = stringResource(
                                R.string.episode_in_time,
                                nextAiringEpisode.episode,
                                nextAiringEpisode.timeUntilAiring.toLong().secondsToLegibleText()
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                subtitle2 = {
                    item.meanScore?.let { meanScore ->
                        SmallScoreIndicator(score = meanScore)
                    }
                    if (!item.genres.isNullOrEmpty()) {
                        Text(
                            text = item.genres!!.take(3)
                                .mapNotNull { it?.genreTagLocalized() }
                                .joinToString(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                status = item.mediaListEntry?.basicMediaListEntry?.status,
                onClick = { onClickItem(item) },
                onLongClick = { onLongClickItem(item) },
            )
        }
        if (uiState.isLoading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        }
    }//: LazyColumn
}

@Preview
@Composable
fun SeasonAnimeViewPreview() {
    AniHyouTheme {
        Surface {
            SeasonAnimeContent(
                uiState = SeasonAnimeUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
            )
        }
    }
}