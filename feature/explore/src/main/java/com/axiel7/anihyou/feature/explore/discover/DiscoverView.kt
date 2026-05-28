package com.axiel7.anihyou.feature.explore.discover

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.media.ChartType
import com.axiel7.anihyou.core.model.media.currentAnimeSeason
import com.axiel7.anihyou.core.model.media.icon
import com.axiel7.anihyou.core.model.media.nextAnimeSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.explore.ExploreSearchBar
import com.axiel7.anihyou.feature.explore.discover.content.AiringContent
import com.axiel7.anihyou.feature.explore.discover.content.DiscoverMediaContent
import com.axiel7.anihyou.feature.explore.discover.content.SeasonAnimeContent
import org.koin.compose.viewmodel.koinActivityViewModel
import java.time.LocalDateTime

enum class DiscoverInfo {
    AIRING,
    THIS_SEASON,
    TRENDING_ANIME,
    NEXT_SEASON,
    TRENDING_MANGA,
    NEWLY_ANIME,
    NEWLY_MANGA,
}

@Composable
fun DiscoverView(
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navActionManager: NavActionManager,
) {
    val viewModel: DiscoverViewModel = koinActivityViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverContent(
        topBar = {
            ExploreSearchBar(
                isLoggedIn = isLoggedIn,
                navActionManager = navActionManager
            )
        },
        isLoggedIn = isLoggedIn,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
        modifier = modifier,
        contentPadding = contentPadding,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DiscoverContent(
    topBar: @Composable () -> Unit,
    isLoggedIn: Boolean,
    uiState: DiscoverUiState,
    event: DiscoverEvent?,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val snackbarManager = rememberSnackbarManager()
    val pullRefreshState = rememberPullToRefreshState()
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
            onEntryUpdated = {
                //TODO: update corresponding list item
                //viewModel.onUpdateListEntry(updatedListEntry)
            },
            onDismissed = { showEditSheet = false }
        )
    }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        snackbarHost = snackbarManager::SnackbarHost,
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { event?.refresh() },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = pullRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullRefreshState,
                    isRefreshing = uiState.isLoading,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        ) {
            LazyColumn(
                modifier = modifier,
                state = listState,
                contentPadding = contentPadding
            ) {
                item {
                    Text(
                        text = stringResource(R.string.anime),
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        AssistChip(
                            onClick = {
                                navActionManager.toAnimeSeason(uiState.currentSeason.year, uiState.currentSeason.season)
                            },
                            label = { Text(text = stringResource(R.string.season)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(uiState.currentSeason.season.icon()),
                                    contentDescription = null
                                )
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        AssistChip(
                            onClick = { navActionManager.toCalendar() },
                            label = { Text(text = stringResource(R.string.calendar)) },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.calendar_month_24),
                                    contentDescription = null
                                )
                            }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        ChartType.animeCharts.forEach { chartType ->
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

                    Text(
                        text = stringResource(R.string.manga),
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
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
                        DiscoverInfo.AIRING -> {
                            LaunchedEffect(uiState.airingOnMyList) {
                                if (uiState.airingOnMyList == true) event?.fetchAiringAnimeOnMyList()
                                else if (uiState.airingOnMyList == false) event?.fetchAiringAnime()
                            }
                            AiringContent(
                                airingOnMyList = uiState.airingOnMyList,
                                airingAnime = uiState.airingAnime,
                                airingAnimeOnMyList = uiState.airingAnimeOnMyList,
                                isLoading = uiState.isLoadingAiring,
                                onLongClickItem = { details, listEntry ->
                                    event?.selectItem(details, listEntry)
                                    showEditSheetAction()
                                },
                                navigateToCalendar = navActionManager::toCalendar,
                                navigateToMediaDetails = navActionManager::toMediaDetails,
                            )
                        }

                        DiscoverInfo.THIS_SEASON -> {
                            LaunchedEffect(uiState.nowAnimeSeason) {
                                event?.fetchThisSeasonAnime()
                            }
                            SeasonAnimeContent(
                                animeSeason = uiState.nowAnimeSeason,
                                seasonAnime = uiState.thisSeasonAnime,
                                isLoading = uiState.isLoadingThisSeason,
                                isNextSeason = false,
                                onLongClickItem = {
                                    event?.selectItem(
                                        details = it.basicMediaDetails,
                                        listEntry = it.mediaListEntry?.basicMediaListEntry
                                    )
                                    showEditSheetAction()
                                },
                                navigateToAnimeSeason = navActionManager::toAnimeSeason,
                                navigateToMediaDetails = navActionManager::toMediaDetails,
                            )
                        }

                        DiscoverInfo.TRENDING_ANIME -> {
                            LaunchedEffect(MediaType.ANIME) {
                                event?.fetchTrendingAnime()
                            }
                            DiscoverMediaContent(
                                title = stringResource(R.string.trending_now),
                                media = uiState.trendingAnime,
                                isLoading = uiState.isLoadingTrendingAnime,
                                onLongClickItem = {
                                    event?.selectItem(
                                        details = it.basicMediaDetails,
                                        listEntry = it.mediaListEntry?.basicMediaListEntry
                                    )
                                    showEditSheetAction()
                                },
                                onClickHeader = {
                                    navActionManager.toExplore(
                                        MediaType.ANIME,
                                        MediaSort.TRENDING_DESC
                                    )
                                },
                                navigateToMediaDetails = navActionManager::toMediaDetails,
                            )
                        }

                        DiscoverInfo.NEXT_SEASON -> {
                            LaunchedEffect(uiState.nextAnimeSeason) {
                                event?.fetchNextSeasonAnime()
                            }
                            SeasonAnimeContent(
                                animeSeason = uiState.nextAnimeSeason,
                                seasonAnime = uiState.nextSeasonAnime,
                                isLoading = uiState.isLoadingNextSeason,
                                isNextSeason = true,
                                onLongClickItem = {
                                    event?.selectItem(
                                        details = it.basicMediaDetails,
                                        listEntry = it.mediaListEntry?.basicMediaListEntry
                                    )
                                    showEditSheetAction()
                                },
                                navigateToAnimeSeason = navActionManager::toAnimeSeason,
                                navigateToMediaDetails = navActionManager::toMediaDetails,
                            )
                        }

                        DiscoverInfo.TRENDING_MANGA -> {
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

                        DiscoverInfo.NEWLY_ANIME -> {
                            LaunchedEffect(MediaType.ANIME) {
                                event?.fetchNewlyAnime()
                            }
                            DiscoverMediaContent(
                                title = stringResource(R.string.newly_anime),
                                media = uiState.newlyAnime,
                                isLoading = uiState.isLoadingNewlyAnime,
                                onLongClickItem = {
                                    event?.selectItem(
                                        details = it.basicMediaDetails,
                                        listEntry = it.mediaListEntry?.basicMediaListEntry
                                    )
                                    showEditSheetAction()
                                },
                                onClickHeader = {
                                    navActionManager.toExplore(MediaType.ANIME, MediaSort.ID_DESC)
                                },
                                navigateToMediaDetails = navActionManager::toMediaDetails,
                            )
                        }

                        DiscoverInfo.NEWLY_MANGA -> {
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
                                    navActionManager.toExplore(MediaType.MANGA, MediaSort.ID_DESC)
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

@Preview
@Composable
private fun DiscoverViewPreview() {
    val now = remember { LocalDateTime.now() }
    AniHyouTheme {
        Surface {
            DiscoverContent(
                topBar = {},
                isLoggedIn = true,
                uiState = DiscoverUiState(
                    infos = DiscoverInfo.entries.toMutableStateList(),
                    nowAnimeSeason = now.currentAnimeSeason(),
                    nextAnimeSeason = now.nextAnimeSeason(),
                ),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
            )
        }
    }
}
