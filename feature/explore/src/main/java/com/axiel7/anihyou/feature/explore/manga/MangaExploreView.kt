package com.axiel7.anihyou.feature.explore.manga

import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.media.ChartType
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.explore.discover.content.DiscoverMediaContent
import org.koin.compose.viewmodel.koinViewModel

enum class MangaDiscoverInfo {
    NEWLY_MANGA,
    TRENDING_MANGA,
}

@Composable
fun MangaDiscoverView(
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val viewModel: MangaExploreViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MangaDiscoverContent(
        isLoggedIn = isLoggedIn,
        uiState = uiState,
        event = viewModel,
        modifier = modifier,
        contentPadding = contentPadding,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MangaDiscoverContent(
    isLoggedIn: Boolean,
    uiState: MangaExploreUiState,
    event: MangaExploreEvent?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val navActionManager = LocalNavActionManager.current
    val snackbarManager = rememberSnackbarManager()
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 0, onLoadMore = { event?.addNextInfo() })

    val haptic = LocalHapticFeedback.current
    var showEditSheet by rememberSaveable { mutableStateOf(false) }

    fun showEditSheetAction() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        if (isLoggedIn) {
            showEditSheet = true
        } else {
            snackbarManager.showNotLoggedInSnackbar()
        }
    }

    if (showEditSheet && uiState.selectedMediaDetails != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedMediaDetails,
            listEntry = uiState.selectedMediaListEntry,
            onEntryUpdated = { newListEntry ->
                event?.onUpdateListEntry(newListEntry)
            },
            onDismissed = { showEditSheet = false }
        )
    }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    Scaffold(
        modifier = modifier,
        snackbarHost = snackbarManager::SnackbarHost,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { event?.refresh() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isLoading,
                    modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            LazyColumn(
                modifier = modifier,
                state = listState,
                contentPadding = contentPadding
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        ChartType.mangaCharts.forEach { chartType ->
                            AssistChip(
                                onClick = { navActionManager.toMediaChart(chartType) },
                                label = { Text(text = chartType.localized()) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(chartType.icon()),
                                        contentDescription = null
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                    }
                }

                items(uiState.infos) { item ->
                    when (item) {
                        MangaDiscoverInfo.TRENDING_MANGA -> {
                            LaunchedEffect(MediaType.MANGA) {
                                event?.fetchTrendingManga()
                            }
                            DiscoverMediaContent(
                                title = stringResource(R.string.trending_manga),
                                media = uiState.trendingManga,
                                isLoading = uiState.isLoadingTrendingManga,
                                onLongClickItem = {
                                    event?.selectItem(
                                        details = it.basicMediaDetails,
                                        listEntry = it.mediaListEntry?.basicMediaListEntry
                                    )
                                    showEditSheetAction()
                                },
                                onClickHeader = {
                                    navActionManager.toExplore(
                                        MediaType.MANGA,
                                        MediaSort.TRENDING_DESC
                                    )
                                },
                                navigateToMediaDetails = navActionManager::toMediaDetails,
                            )
                        }

                        MangaDiscoverInfo.NEWLY_MANGA -> {
                            LaunchedEffect(MediaType.MANGA) {
                                event?.fetchNewlyManga()
                            }
                            DiscoverMediaContent(
                                title = stringResource(R.string.newly_manga),
                                media = uiState.newlyManga,
                                isLoading = uiState.isLoadingNewlyManga,
                                onLongClickItem = {
                                    event?.selectItem(
                                        details = it.basicMediaDetails,
                                        listEntry = it.mediaListEntry?.basicMediaListEntry
                                    )
                                    showEditSheetAction()
                                },
                                onClickHeader = {
                                    navActionManager.toExplore(
                                        MediaType.MANGA,
                                        MediaSort.ID_DESC
                                    )
                                },
                                navigateToMediaDetails = navActionManager::toMediaDetails,
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
private fun MangaDiscoverPreview() {
    AniHyouTheme {
        Surface {
            MangaDiscoverContent(
                isLoggedIn = true,
                uiState = MangaExploreUiState(
                    infos = mutableStateListOf(
                        MangaDiscoverInfo.TRENDING_MANGA,
                        MangaDiscoverInfo.NEWLY_MANGA
                    )
                ),
                event = null
            )
        }
    }
}