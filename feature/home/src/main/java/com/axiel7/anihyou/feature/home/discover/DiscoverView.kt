package com.axiel7.anihyou.feature.home.discover

import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.media.currentAnimeSeason
import com.axiel7.anihyou.core.model.media.nextAnimeSeason
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.home.discover.content.AiringContent
import com.axiel7.anihyou.feature.home.discover.content.DiscoverMediaContent
import com.axiel7.anihyou.feature.home.discover.content.SeasonAnimeContent
import org.koin.androidx.compose.koinViewModel
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
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navActionManager: NavActionManager
) {
    val viewModel: DiscoverViewModel = koinViewModel(
        viewModelStoreOwner = LocalActivity.current as AppCompatActivity
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverContent(
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
    uiState: DiscoverUiState,
    event: DiscoverEvent?,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val pullRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 0, onLoadMore = { event?.addNextInfo() })

    val haptic = LocalHapticFeedback.current
    var showEditSheet by remember { mutableStateOf(false) }

    fun showEditSheetAction() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        showEditSheet = true
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

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { event?.refresh() },
        modifier = Modifier.fillMaxSize(),
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
                                navActionManager.toExplore(MediaType.ANIME, MediaSort.TRENDING_DESC)
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
                                navActionManager.toExplore(MediaType.MANGA, MediaSort.TRENDING_DESC)
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
        }//: LazyColumn
    }
}

@Preview
@Composable
fun DiscoverViewPreview() {
    val now = remember { LocalDateTime.now() }
    AniHyouTheme {
        Surface {
            DiscoverContent(
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