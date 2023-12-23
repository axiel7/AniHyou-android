package com.axiel7.anihyou.ui.screens.home.discover

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.AiringOnMyListQuery
import com.axiel7.anihyou.MediaSortedQuery
import com.axiel7.anihyou.SeasonalAnimeQuery
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.screens.home.discover.content.AiringContent
import com.axiel7.anihyou.ui.screens.home.discover.content.SeasonAnimeContent
import com.axiel7.anihyou.ui.screens.home.discover.content.TrendingMediaContent
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.currentAnimeSeason
import com.axiel7.anihyou.utils.DateUtils.nextAnimeSeason
import kotlinx.coroutines.launch
import java.time.LocalDateTime

enum class DiscoverInfo {
    AIRING,
    THIS_SEASON,
    TRENDING_ANIME,
    NEXT_SEASON,
    TRENDING_MANGA,
}

@Composable
fun DiscoverView(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navActionManager: NavActionManager
) {
    val viewModel: DiscoverViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DiscoverContent(
        infos = viewModel.infos,
        airingAnime = viewModel.airingAnime,
        airingAnimeOnMyList = viewModel.airingAnimeOnMyList,
        thisSeasonAnime = viewModel.thisSeasonAnime,
        trendingAnime = viewModel.trendingAnime,
        nextSeasonAnime = viewModel.nextSeasonAnime,
        trendingManga = viewModel.trendingManga,
        modifier = modifier,
        contentPadding = contentPadding,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiscoverContent(
    infos: List<DiscoverInfo>,
    airingAnime: List<AiringAnimesQuery.AiringSchedule>,
    airingAnimeOnMyList: List<AiringOnMyListQuery.Medium>,
    thisSeasonAnime: List<SeasonalAnimeQuery.Medium>,
    trendingAnime: List<MediaSortedQuery.Medium>,
    nextSeasonAnime: List<SeasonalAnimeQuery.Medium>,
    trendingManga: List<MediaSortedQuery.Medium>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    uiState: DiscoverUiState,
    event: DiscoverEvent?,
    navActionManager: NavActionManager,
) {
    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 0, onLoadMore = { event?.addNextInfo() })

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val editSheetState = rememberModalBottomSheetState()

    suspend fun showEditSheet() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        editSheetState.show()
    }

    if (editSheetState.isVisible && uiState.selectedMediaDetails != null) {
        EditMediaSheet(
            sheetState = editSheetState,
            mediaDetails = uiState.selectedMediaDetails,
            listEntry = uiState.selectedMediaListEntry,
            onDismiss = { _ ->
                scope.launch {
                    //TODO: update corresponding list item
                    //viewModel.onUpdateListEntry(updatedListEntry)
                    editSheetState.hide()
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding
    ) {
        items(infos) { item ->
            when (item) {
                DiscoverInfo.AIRING -> {
                    LaunchedEffect(uiState.airingOnMyList) {
                        if (uiState.airingOnMyList == true) event?.fetchAiringAnimeOnMyList()
                        else if (uiState.airingOnMyList == false) event?.fetchAiringAnime()
                    }
                    AiringContent(
                        airingOnMyList = uiState.airingOnMyList,
                        airingAnime = airingAnime,
                        airingAnimeOnMyList = airingAnimeOnMyList,
                        isLoading = uiState.isLoadingAiring,
                        onLongClickItem = { details, listEntry ->
                            scope.launch {
                                event?.selectItem(details, listEntry)
                                showEditSheet()
                            }
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
                        seasonAnime = thisSeasonAnime,
                        isLoading = uiState.isLoadingThisSeason,
                        isNextSeason = false,
                        onLongClickItem = {
                            scope.launch {
                                event?.selectItem(
                                    details = it.basicMediaDetails,
                                    listEntry = it.mediaListEntry?.basicMediaListEntry
                                )
                                showEditSheet()
                            }
                        },
                        navigateToAnimeSeason = navActionManager::toAnimeSeason,
                        navigateToMediaDetails = navActionManager::toMediaDetails,
                    )
                }

                DiscoverInfo.TRENDING_ANIME -> {
                    LaunchedEffect(MediaType.ANIME) {
                        event?.fetchTrendingAnime()
                    }
                    TrendingMediaContent(
                        mediaType = MediaType.ANIME,
                        trendingMedia = trendingAnime,
                        isLoading = uiState.isLoadingTrendingAnime,
                        onLongClickItem = {
                            scope.launch {
                                event?.selectItem(
                                    details = it.basicMediaDetails,
                                    listEntry = it.mediaListEntry?.basicMediaListEntry
                                )
                                showEditSheet()
                            }
                        },
                        navigateToExplore = navActionManager::toExplore,
                        navigateToMediaDetails = navActionManager::toMediaDetails,
                    )
                }

                DiscoverInfo.NEXT_SEASON -> {
                    LaunchedEffect(uiState.nextAnimeSeason) {
                        event?.fetchNextSeasonAnime()
                    }
                    SeasonAnimeContent(
                        animeSeason = uiState.nextAnimeSeason,
                        seasonAnime = nextSeasonAnime,
                        isLoading = uiState.isLoadingNextSeason,
                        isNextSeason = true,
                        onLongClickItem = {
                            scope.launch {
                                event?.selectItem(
                                    details = it.basicMediaDetails,
                                    listEntry = it.mediaListEntry?.basicMediaListEntry
                                )
                                showEditSheet()
                            }
                        },
                        navigateToAnimeSeason = navActionManager::toAnimeSeason,
                        navigateToMediaDetails = navActionManager::toMediaDetails,
                    )
                }

                DiscoverInfo.TRENDING_MANGA -> {
                    LaunchedEffect(MediaType.MANGA) {
                        event?.fetchTrendingManga()
                    }
                    TrendingMediaContent(
                        mediaType = MediaType.MANGA,
                        trendingMedia = trendingManga,
                        isLoading = uiState.isLoadingTrendingManga,
                        onLongClickItem = {
                            scope.launch {
                                event?.selectItem(
                                    details = it.basicMediaDetails,
                                    listEntry = it.mediaListEntry?.basicMediaListEntry
                                )
                                showEditSheet()
                            }
                        },
                        navigateToExplore = navActionManager::toExplore,
                        navigateToMediaDetails = navActionManager::toMediaDetails,
                    )
                }
            }
        }
    }//: LazyColumn
}

@Preview
@Composable
fun DiscoverViewPreview() {
    val now = remember { LocalDateTime.now() }
    AniHyouTheme {
        Surface {
            DiscoverContent(
                infos = DiscoverInfo.entries,
                airingAnime = emptyList(),
                airingAnimeOnMyList = emptyList(),
                thisSeasonAnime = emptyList(),
                trendingAnime = emptyList(),
                nextSeasonAnime = emptyList(),
                trendingManga = emptyList(),
                uiState = DiscoverUiState(
                    nowAnimeSeason = now.currentAnimeSeason(),
                    nextAnimeSeason = now.nextAnimeSeason(),
                ),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
            )
        }
    }
}