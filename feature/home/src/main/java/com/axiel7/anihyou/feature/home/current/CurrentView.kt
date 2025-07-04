package com.axiel7.anihyou.feature.home.current

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.CurrentListType
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.list.HorizontalListHeader
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_COMPACT_HEIGHT
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.editmedia.composables.SetScoreDialog
import com.axiel7.anihyou.feature.home.current.composables.CurrentListItem
import com.axiel7.anihyou.feature.home.current.composables.CurrentListItemPlaceholder
import org.koin.androidx.compose.koinViewModel

@Composable
fun CurrentView(
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier,
) {
    val viewModel: CurrentViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CurrentContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrentContent(
    uiState: CurrentUiState,
    event: CurrentEvent?,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val pullRefreshState = rememberPullToRefreshState()
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var showEditSheet by remember { mutableStateOf(false) }

    if (showEditSheet && uiState.selectedItem?.media != null && uiState.selectedType != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedItem.media!!.basicMediaDetails,
            listEntry = uiState.selectedItem.basicMediaListEntry,
            bottomPadding = bottomBarPadding,
            onEntryUpdated = {
                event?.onUpdateListEntry(it, uiState.selectedType)
            },
            onDismissed = { showEditSheet = false }
        )
    }

    if (uiState.openSetScoreDialog) {
        SetScoreDialog(
            onDismiss = { event?.toggleSetScoreDialog(false) },
            onConfirm = { event?.setScore(it) },
            scoreFormat = uiState.scoreFormat,
        )
    }

    PullToRefreshBox(
        isRefreshing = uiState.fetchFromNetwork,
        onRefresh = { event?.refresh() },
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            CurrentListType.entries.forEach { type ->
                val list = when (type) {
                    CurrentListType.AIRING -> uiState.airingList
                    CurrentListType.BEHIND -> uiState.behindList
                    CurrentListType.ANIME -> uiState.animeList
                    CurrentListType.MANGA -> uiState.mangaList
                }
                if (list.isNotEmpty()) {
                    HorizontalListHeader(
                        text = type.localized(),
                        onClick = { navActionManager.toCurrentFullList(type) }
                    )

                    CurrentLazyGrid(
                        items = list,
                        scoreFormat = uiState.scoreFormat,
                        isLoading = uiState.isLoading,
                        isPlusEnabled = !uiState.isLoadingPlusOne,
                        onClick = { navActionManager.toMediaDetails(it.mediaId) },
                        onClickPlus = {
                            if (!uiState.isLoadingPlusOne) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                event?.onClickPlusOne(it, type)
                            }
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            event?.selectItem(it, type)
                            showEditSheet = true
                        }
                    )
                }
            }
            if (!uiState.isLoading && uiState.hasNothing) {
                Text(
                    text = stringResource(R.string.no_information),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CurrentLazyGrid(
    items: List<CommonMediaListEntry>,
    scoreFormat: ScoreFormat,
    isLoading: Boolean,
    isPlusEnabled: Boolean,
    onClick: (CommonMediaListEntry) -> Unit,
    onLongClick: (CommonMediaListEntry) -> Unit,
    onClickPlus: (CommonMediaListEntry) -> Unit,
) {
    val state = rememberLazyGridState()
    val rows = if (items.size == 1) 1 else 2
    LazyHorizontalGrid(
        rows = GridCells.Fixed(rows),
        modifier = Modifier
            .heightIn(
                max = (MEDIA_POSTER_COMPACT_HEIGHT * rows + 20).dp
            ),
        state = state,
        contentPadding = PaddingValues(end = 32.dp),
        flingBehavior = rememberSnapFlingBehavior(
            lazyGridState = state,
            snapPosition = SnapPosition.Start
        )
    ) {
        items(
            items = items,
            contentType = { it }
        ) { item ->
            CurrentListItem(
                modifier = Modifier.width(350.dp),
                item = item,
                scoreFormat = scoreFormat,
                isPlusEnabled = isPlusEnabled,
                onClick = { onClick(item) },
                onLongClick = { onLongClick(item) },
                onClickPlus = { onClickPlus(item) },
            )
        }
        if (items.isEmpty() && isLoading) {
            items(4) {
                CurrentListItemPlaceholder()
            }
        }
    }
}

@Preview
@Composable
private fun CurrentViewPreview() {
    val exampleList = remember {
        mutableStateListOf(
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
        )
    }
    AniHyouTheme {
        Surface {
            CurrentContent(
                uiState = CurrentUiState(
                    airingList = remember {
                        mutableStateListOf(exampleCommonMediaListEntry)
                    },
                    behindList = exampleList,
                    animeList = exampleList,
                    mangaList = exampleList
                ),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
            )
        }
    }
}