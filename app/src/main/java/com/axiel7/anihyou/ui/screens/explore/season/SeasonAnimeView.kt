package com.axiel7.anihyou.ui.screens.explore.season

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.composables.scores.SmallScoreIndicator
import com.axiel7.anihyou.ui.screens.explore.season.composables.SeasonChartFilterSheet
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun SeasonAnimeView(
    navActionManager: NavActionManager
) {
    val viewModel: SeasonAnimeViewModel = hiltViewModel()
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

    val listState = rememberLazyGridState()
    listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })

    if (showFilterSheet && uiState.season != null) {
        SeasonChartFilterSheet(
            initialSeason = uiState.season,
            scope = scope,
            onDismiss = { showFilterSheet = false },
            setSeason = { event?.setSeason(it) }
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
        scrollBehavior = topAppBarScrollBehavior,
        contentWindowInsets = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal)
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
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
                        if (item.meanScore != null) {
                            SmallScoreIndicator(score = item.meanScore)
                        }
                    },
                    badgeContent = item.mediaListEntry?.basicMediaListEntry?.status?.let { status ->
                        {
                            Icon(
                                painter = painterResource(status.icon()),
                                contentDescription = status.localized()
                            )
                        }
                    },
                    minLines = 2,
                    onClick = {
                        navActionManager.toMediaDetails(item.id)
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        event?.selectItem(item)
                        showEditSheet = true
                    }
                )
            }
            if (uiState.isLoading) {
                items(13) {
                    MediaItemVerticalPlaceholder()
                }
            }
        }//: Grid
    }//: Scaffold
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