package com.axiel7.anihyou.feature.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SelectableDropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampToTimeString
import com.axiel7.anihyou.core.network.fragment.ExploreMedia
import com.axiel7.anihyou.core.resources.ColorUtils.colorFromHex
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.feature.calendar.composables.CalendarAiringHorizontalItem
import com.axiel7.anihyou.feature.calendar.composables.CalendarAiringHorizontalItemPlaceholder
import com.axiel7.anihyou.feature.calendar.composables.CalendarBanner
import com.axiel7.anihyou.feature.calendar.composables.CalendarBannerPlaceholder
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.time.DayOfWeek

@Composable
fun CalendarView(
    isLoggedIn: Boolean
) {
    val viewModel: CalendarViewModel = koinViewModel()
    val onMyList by viewModel.onMyList.collectAsStateWithLifecycle(initialValue = null)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalendarViewContent(
        isLoggedIn = isLoggedIn,
        onMyList = onMyList,
        onMyListChanged = viewModel::onMyListChanged,
        uiState = uiState,
        event = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CalendarViewContent(
    isLoggedIn: Boolean,
    onMyList: Boolean?,
    onMyListChanged: (Boolean?) -> Unit,
    uiState: CalendarUiState,
    event: CalendarEvent?
) {
    val navActionManager = LocalNavActionManager.current
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val scope = rememberCoroutineScope()
    val snackbarManager = rememberSnackbarManager()
    val pullToRefreshState = rememberPullToRefreshState()
    var showEditSheet by remember { mutableStateOf(false) }
    val blurAdult = LocalBlurAdult.current
    val haptic = LocalHapticFeedback.current

    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 0) {
        event?.onLoadMore()
    }

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

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.calendar),
        navigationIcon = {
            BackIconButton(onClick = navActionManager::goBack)
        },
        actions = {
            AppBarActions(
                onMyList = onMyList,
                onMyListChanged = onMyListChanged,
            )
        },
        snackbarHost = snackbarManager::SnackbarHost,
        scrollBehavior = topAppBarScrollBehavior,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_upward_24),
                    contentDescription = stringResource(R.string.move_to_top)
                )
            }
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { event?.refresh() },
            modifier = Modifier.fillMaxSize(),
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = uiState.isLoading,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                state = listState,
            ) {
                uiState.weeklyAnime.entries.forEach { (date, mediaList) ->
                    stickyHeader {
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
                                .thenBy { it.averageScore ?: Int.MIN_VALUE } // if popularity is the same, fallback to score
                        )
                        val banner = media?.bannerImage ?: mediaList.firstNotNullOfOrNull { it.bannerImage }
                        val imageColor = colorFromHex(media?.coverImage?.color)

                        CalendarBanner(
                            title = title,
                            date = date.atStartOfDay(),
                            imageUrl = banner,
                            height = 120.dp,
                            color = imageColor,
                            onLongClick = {
                                event?.refreshDay(date)
                            },
                            modifier = Modifier.fillMaxWidth()
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
                                    nextAiringEpisode.airingAt.toLong().timestampToTimeString() ?: UNKNOWN_CHAR
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
                            modifier = Modifier.padding(bottom = if (isLast) 24.dp else 8.dp, top = if (isFirst) 8.dp else 0.dp)
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
    }
}


@Composable
private fun AppBarActions(
    onMyList: Boolean?,
    onMyListChanged: (Boolean?) -> Unit,
    modifier: Modifier = Modifier
) {
    var menuOpened by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        IconButton(
            onClick = { menuOpened = !menuOpened },
            shapes = IconButtonDefaults.shapes(),
        ) {
            Icon(
                painter = painterResource(R.drawable.more_vert_24),
                contentDescription = stringResource(R.string.show_more),
            )
        }
        DropdownMenuPopup(
            expanded = menuOpened,
            onDismissRequest = { menuOpened = false },
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShapes(),
            ) {
                SelectableDropdownMenuItem(
                    selected = onMyList != null,
                    onClick = {
                        onMyListChanged(
                            when (onMyList) {
                                null -> true
                                true -> false
                                false -> null
                            }
                        )
                        menuOpened = false
                    },
                    text = { Text(text = stringResource(R.string.on_my_list)) },
                    shapes = MenuDefaults.itemShape(0, 1),
                    selectedLeadingIcon = {
                        if (onMyList != null) {
                            Icon(
                                painter = painterResource(
                                    id = if (onMyList) R.drawable.check_20 else R.drawable.close_20
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(MenuDefaults.LeadingIconSize)
                            )
                        }
                    },
                )
            }
        }
    }
}

