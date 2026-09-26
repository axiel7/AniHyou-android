package com.axiel7.anihyou.feature.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SelectableDropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampToTimeString
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.network.fragment.ExploreMedia
import com.axiel7.anihyou.core.resources.ColorUtils.colorFromHex
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.IconButtonWithMenu
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.list.rememberIsScrollingUp
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.feature.calendar.composables.CalendarAiringHorizontalItem
import com.axiel7.anihyou.feature.calendar.composables.CalendarAiringHorizontalItemPlaceholder
import com.axiel7.anihyou.feature.calendar.composables.CalendarBanner
import com.axiel7.anihyou.feature.calendar.composables.CalendarBannerPlaceholder
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun CalendarView(
    isLoggedIn: Boolean,
) {
    val viewModel: CalendarViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalendarViewContent(
        isLoggedIn = isLoggedIn,
        uiState = uiState,
        event = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CalendarViewContent(
    isLoggedIn: Boolean,
    uiState: CalendarUiState,
    event: CalendarEvent?
) {
    val haptic = LocalHapticFeedback.current
    val navActionManager = LocalNavActionManager.current
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val scope = rememberCoroutineScope()
    val snackbarManager = rememberSnackbarManager()
    val pullToRefreshState = rememberPullToRefreshState()
    var showEditSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val isScrollingUp by rememberIsScrollingUp(listState, gridState)
    var firstItemIndex by rememberSaveable { mutableIntStateOf(0) }

    fun showEditSheetAction() {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        if (isLoggedIn) {
            showEditSheet = true
        } else {
            snackbarManager.showNotLoggedInSnackbar()
        }
    }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    if (showEditSheet && uiState.selectedItem != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedItem.basicMediaDetails,
            listEntry = uiState.selectedItem.mediaListEntry?.basicMediaListEntry,
            onEntryUpdated = {
                event?.onUpdateListEntry(it)
            },
            onDismissed = {
                showEditSheet = false
            }
        )
    }

    LaunchedEffect(uiState.todayFirstItemIndex) {
        if (uiState.todayFirstItemIndex > 0) firstItemIndex = uiState.todayFirstItemIndex
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.calendar),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            AppBarActions(
                uiState = uiState,
                event = event,
            )
        },
        snackbarHost = snackbarManager::SnackbarHost,
        scrollBehavior = topAppBarScrollBehavior,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        if (uiState.listStyle == ListStyle.GRID) {
                            gridState.animateScrollToItem(firstItemIndex, 500)
                        } else {
                            listState.animateScrollToItem(firstItemIndex, 500)
                        }
                    }
                },
                modifier = Modifier.animateFloatingActionButton(
                    visible = isScrollingUp,
                    alignment = Alignment.BottomEnd
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_upward_24),
                    contentDescription = stringResource(R.string.move_to_top)
                )
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.fetchFromNetwork && uiState.isLoading,
            onRefresh = { event?.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding()),
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isLoading,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            val contentPadding = PaddingValues(
                start = padding.calculateStartPadding(LocalLayoutDirection.current),
                end = padding.calculateStartPadding(LocalLayoutDirection.current),
                bottom = padding.calculateBottomPadding(),
            )
            AnimatedVisibility(
                visible = uiState.listStyle == ListStyle.STANDARD,
                enter = fadeIn(animationSpec = tween()),
                exit = fadeOut(animationSpec = tween()),
            ) {
                ListView(
                    uiState = uiState,
                    event = event,
                    listState = listState,
                    contentPadding = contentPadding,
                    showEditSheetAction = ::showEditSheetAction,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                )
            }
            AnimatedVisibility(
                visible = uiState.listStyle == ListStyle.GRID,
                enter = fadeIn(animationSpec = tween()),
                exit = fadeOut(animationSpec = tween()),
            ) {
                GridView(
                    uiState = uiState,
                    event = event,
                    gridState = gridState,
                    contentPadding = contentPadding,
                    showEditSheetAction = ::showEditSheetAction,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                )
            }
        }
    }
}

@Composable
private fun AppBarActions(
    uiState: CalendarUiState,
    event: CalendarEvent?,
    modifier: Modifier = Modifier
) {
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

    IconButtonWithMenu(
        icon = R.drawable.more_vert_24,
        contentDescription = stringResource(R.string.show_more),
        modifier = modifier,
    ) { onDismiss ->
        SelectableDropdownMenuItem(
            selected = uiState.onMyList != null,
            onClick = {
                event?.onMyListChanged(if (uiState.onMyList == true) null else true)
                onDismiss()
            },
            text = { Text(text = stringResource(R.string.on_my_list)) },
            shapes = MenuDefaults.itemShape(0, 1),
            selectedLeadingIcon = {
                if (uiState.onMyList != null) {
                    Icon(
                        painter = painterResource(
                            id = if (uiState.onMyList) R.drawable.check_20 else R.drawable.close_20
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(MenuDefaults.LeadingIconSize)
                    )
                }
            },
        )
    }
}

@Composable
private fun StickyHeader(
    mediaList: ImmutableList<ExploreMedia>,
    date: LocalDate,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleId = when (date.dayOfWeek) {
        DayOfWeek.MONDAY -> R.string.monday
        DayOfWeek.TUESDAY -> R.string.tuesday
        DayOfWeek.WEDNESDAY -> R.string.wednesday
        DayOfWeek.THURSDAY -> R.string.thursday
        DayOfWeek.FRIDAY -> R.string.friday
        DayOfWeek.SATURDAY -> R.string.saturday
        DayOfWeek.SUNDAY -> R.string.sunday
    }
    val title = stringResource(id = titleId)
    val media = mediaList.maxWithOrNull(
        compareBy<ExploreMedia> { it.popularity ?: Int.MIN_VALUE }
            .thenBy {
                it.averageScore ?: Int.MIN_VALUE
            } // if popularity is the same, fallback to score
    )
    val banner =
        media?.bannerImage ?: mediaList.firstNotNullOfOrNull { it.bannerImage }
    val imageColor = colorFromHex(media?.coverImage?.color)

    CalendarBanner(
        title = title,
        date = date.atStartOfDay(),
        imageUrl = banner,
        height = 100.dp,
        color = imageColor,
        onLongClick = onLongClick,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ListView(
    uiState: CalendarUiState,
    event: CalendarEvent?,
    listState: LazyListState,
    contentPadding: PaddingValues,
    showEditSheetAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navActionManager = LocalNavActionManager.current
    val blurAdult = LocalBlurAdult.current

    listState.OnBottomReached(buffer = 1, debounceDuration = 500.milliseconds) {
        event?.onLoadMore()
    }
    LaunchedEffect(uiState.todayFirstItemIndex) {
        if (uiState.todayFirstItemIndex > 0) {
            listState.animateScrollToItem(uiState.todayFirstItemIndex, 500)
            event?.onAutoScrolled()
        }
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        state = listState,
    ) {
        uiState.weeklyAnime.entries.forEach { (date, mediaList) ->
            if (mediaList.isEmpty()) return@forEach
            stickyHeader {
                StickyHeader(
                    mediaList = mediaList.toImmutableList(),
                    date = date,
                    onLongClick = { event?.refreshDay(date) },
                )
            }

            items(
                items = mediaList,
                contentType = { it }
            ) { item ->
                val isLast = mediaList.lastOrNull() == item
                val isFirst = mediaList.firstOrNull() == item

                CalendarAiringHorizontalItem(
                    title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                    subtitle = item.nextAiringEpisode?.let { nextAiringEpisode ->
                        stringResource(
                            R.string.episode_airing_at,
                            nextAiringEpisode.episode,
                            nextAiringEpisode.airingAt.toLong().timestampToTimeString()
                                ?: UNKNOWN_CHAR
                        )
                    } ?: stringResource(R.string.unknown),
                    blurImage = blurAdult && item.basicMediaDetails.isAdult == true,
                    imageUrl = item.coverImage?.large,
                    score = item.averageScore,
                    status = item.mediaListEntry?.basicMediaListEntry?.status,
                    onClick = {
                        navActionManager.toMediaDetails(item.id)
                    },
                    onLongClick = {
                        event?.selectItem(item)
                        showEditSheetAction()
                    },
                    modifier = Modifier.padding(
                        bottom = if (isLast) 16.dp else 8.dp,
                        top = if (isFirst) 16.dp else 0.dp,
                    )
                )
            }
        }

        if (uiState.isLoading) {
            item {
                CalendarBannerPlaceholder(
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(
                count = 20,
                contentType = { "placeholder" }
            ) {
                CalendarAiringHorizontalItemPlaceholder(
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun GridView(
    uiState: CalendarUiState,
    event: CalendarEvent?,
    gridState: LazyGridState,
    contentPadding: PaddingValues,
    showEditSheetAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navActionManager = LocalNavActionManager.current
    val blurAdult = LocalBlurAdult.current

    gridState.OnBottomReached(buffer = 1, debounceDuration = 500.milliseconds) {
        event?.onLoadMore()
    }
    LaunchedEffect(uiState.todayFirstItemIndex) {
        if (uiState.todayFirstItemIndex > 0) {
            gridState.animateScrollToItem(uiState.todayFirstItemIndex, 500)
            event?.onAutoScrolled()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
        modifier = modifier,
        contentPadding = contentPadding,
        state = gridState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        uiState.weeklyAnime.entries.forEach { (date, mediaList) ->
            if (mediaList.isEmpty()) return@forEach
            stickyHeader {
                StickyHeader(
                    mediaList = mediaList.toImmutableList(),
                    date = date,
                    onLongClick = { event?.refreshDay(date) },
                )
            }

            items(
                items = mediaList,
                contentType = { it }
            ) { item ->
                MediaItemVertical(
                    title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                    imageUrl = item.coverImage?.large,
                    blurImage = blurAdult && item.basicMediaDetails.isAdult == true,
                    modifier = Modifier
                        .wrapContentWidth(),
                    subtitle = {
                        item.nextAiringEpisode?.let { nextAiringEpisode ->
                            val text = stringResource(
                                R.string.episode_airing_at,
                                nextAiringEpisode.episode,
                                nextAiringEpisode.airingAt.toLong().timestampToTimeString()
                                    ?: UNKNOWN_CHAR
                            )
                            Text(
                                text = text,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    },
                    status = item.mediaListEntry?.basicMediaListEntry?.status,
                    minLines = 2,
                    onClick = { navActionManager.toMediaDetails(item.id) },
                    onLongClick = {
                        event?.selectItem(item)
                        showEditSheetAction()
                    }
                )
            }
        }

        if (uiState.isLoading) {
            item {
                CalendarBannerPlaceholder(
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(
                count = 20,
                contentType = { "placeholder" }
            ) {
                MediaItemVerticalPlaceholder(
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}
