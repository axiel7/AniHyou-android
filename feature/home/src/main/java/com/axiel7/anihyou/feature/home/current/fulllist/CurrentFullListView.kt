package com.axiel7.anihyou.feature.home.current.fulllist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.CurrentListType
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.editmedia.composables.SetScoreDialog
import com.axiel7.anihyou.feature.home.current.CurrentEvent
import com.axiel7.anihyou.feature.home.current.CurrentUiState
import com.axiel7.anihyou.feature.home.current.CurrentViewModel
import com.axiel7.anihyou.feature.home.current.composables.CurrentListItem
import com.axiel7.anihyou.feature.home.current.composables.CurrentListItemPlaceholder
import org.koin.androidx.compose.koinViewModel

@Composable
fun CurrentFullListView(
    listType: CurrentListType,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier
) {
    val viewModel: CurrentViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CurrentFullListContent(
        listType = listType,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CurrentFullListContent(
    listType: CurrentListType,
    uiState: CurrentUiState,
    event: CurrentEvent?,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val pullRefreshState = rememberPullToRefreshState()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val bottomBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var showEditSheet by remember { mutableStateOf(false) }

    val items = remember(listType) {
        when (listType) {
            CurrentListType.AIRING -> uiState.airingList
            CurrentListType.BEHIND -> uiState.behindList
            CurrentListType.ANIME -> uiState.animeList
            CurrentListType.MANGA -> uiState.mangaList
        }
    }

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

    DefaultScaffoldWithMediumTopAppBar(
        title = listType.localized(),
        modifier = modifier,
        navigationIcon = {
            BackIconButton(onClick = navActionManager::goBack)
        },
        scrollBehavior = topAppBarScrollBehavior,
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.fetchFromNetwork,
            onRefresh = { event?.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            state = pullRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullRefreshState,
                    isRefreshing = uiState.fetchFromNetwork,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(bottom = bottomBarPadding),
            ) {
                items(
                    items = items,
                    contentType = { it }
                ) { item ->
                    CurrentListItem(
                        modifier = Modifier.fillMaxWidth(),
                        item = item,
                        scoreFormat = uiState.scoreFormat,
                        isPlusEnabled = !uiState.isLoadingPlusOne,
                        onClick = { navActionManager.toMediaDetails(item.mediaId) },
                        onClickPlus = { increment ->
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            event?.onClickPlusOne(increment, item, listType)
                        },
                        onLongClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            event?.selectItem(item, listType)
                            showEditSheet = true
                        },
                        blockPlus = { event?.blockPlusOne() }
                    )
                }
                if (items.isEmpty() && uiState.isLoading) {
                    items(4) {
                        CurrentListItemPlaceholder()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CurrentFullListViewPreview() {
    val exampleList = remember {
        mutableStateListOf(
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
            exampleCommonMediaListEntry,
        )
    }
    AniHyouTheme {
        Surface {
            CurrentFullListContent(
                listType = CurrentListType.AIRING,
                uiState = CurrentUiState(
                    airingList = exampleList,
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