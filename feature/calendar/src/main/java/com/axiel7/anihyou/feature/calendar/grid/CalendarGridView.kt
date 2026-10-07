package com.axiel7.anihyou.feature.calendar.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampToTimeString
import com.axiel7.anihyou.core.model.ListStyle
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.SnackbarManager
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.TabRowWithPager
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.composables.rememberTopBarContainerColor
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.calendar.AppBarActions
import com.axiel7.anihyou.feature.calendar.CalendarHostViewModel
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate

@Composable
fun CalendarGridView(
    isLoggedIn: Boolean,
    viewModel: CalendarHostViewModel,
) {
    val onMyList by viewModel.onMyList.collectAsStateWithLifecycle(initialValue = null)

    CalendarViewContent(
        isLoggedIn = isLoggedIn,
        onMyList = onMyList,
        onChangeListStyle = viewModel::onChangeListStyle,
        onChangeOnMyList = viewModel::onMyListChanged,
    )
}

@Composable
private fun CalendarViewContent(
    isLoggedIn: Boolean,
    onMyList: Boolean?,
    onChangeListStyle: (ListStyle) -> Unit,
    onChangeOnMyList: (Boolean?) -> Unit,
) {
    val navActionManager = LocalNavActionManager.current
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val snackbarManager = rememberSnackbarManager()
    val showEditSheet = remember { mutableStateOf(false) }
    val topAppBarColors = TopAppBarDefaults.topAppBarColors()
    val appBarContainerColor by rememberTopBarContainerColor(topAppBarColors, topAppBarScrollBehavior)

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.calendar),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            AppBarActions(
                listStyle = ListStyle.GRID,
                onMyList = onMyList,
                onChangeListStyle = onChangeListStyle,
                onChangeOnMyList = onChangeOnMyList,
            )
        },
        snackbarHost = snackbarManager::SnackbarHost,
        scrollBehavior = topAppBarScrollBehavior,
        topAppBarColors = topAppBarColors,
    ) { padding ->
        TabRowWithPager(
            tabs = CalendarTab.tabRows,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                ),
            initialPage = LocalDate.now().dayOfWeek.value - 1,
            isTabScrollable = true,
            containerColor = appBarContainerColor,
        ) { page ->
            val weekday = CalendarTab.tabRows[page].value.ordinal + 1
            val viewModel: CalendarGridViewModel = koinViewModel(key = weekday.toString())
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            ErrorDialogHandler(uiState, onDismiss = viewModel::onErrorDisplayed)

            LaunchedEffect(weekday) {
                viewModel.setWeekday(weekday)
            }
            LaunchedEffect(onMyList) {
                if (uiState.onMyList != onMyList)
                    viewModel.setOnMyList(onMyList)
            }

            CalendarDayView(
                isLoggedIn = isLoggedIn,
                snackbarManager = snackbarManager,
                uiState = uiState,
                events = viewModel,
                showEditSheet = showEditSheet,
                modifier = Modifier
                    .fillMaxHeight()
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    bottom = padding.calculateBottomPadding()
                )
            )
        }
    }
}

@Composable
private fun CalendarDayView(
    isLoggedIn: Boolean,
    snackbarManager: SnackbarManager,
    uiState: CalendarGridUiState,
    events: CalendarGridEvent?,
    showEditSheet: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val navActionManager = LocalNavActionManager.current
    val blurAdult = LocalBlurAdult.current
    val haptic = LocalHapticFeedback.current

    val listState = rememberLazyGridState()
    listState.OnBottomReached(buffer = 3) {
        events?.onLoadMore()
    }

    if (showEditSheet.value && uiState.selectedItem != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedItem.basicMediaDetails,
            listEntry = uiState.selectedItem.mediaListEntry?.basicMediaListEntry,
            onEntryUpdated = {
                events?.onUpdateListEntry(it)
            },
            onDismissed = {
                showEditSheet.value = false
            }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        items(
            items = uiState.weeklyAnime,
            contentType = { it }
        ) { item ->
            MediaItemVertical(
                title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                imageUrl = item.coverImage?.large,
                blurImage = blurAdult && item.basicMediaDetails.isAdult == true,
                modifier = Modifier.wrapContentWidth(),
                subtitle = {
                    item.nextAiringEpisode?.let { nextAiringEpisode ->
                        Text(
                            text = stringResource(
                                R.string.episode_airing_at,
                                nextAiringEpisode.episode,
                                nextAiringEpisode.airingAt.toLong().timestampToTimeString()
                                    ?: UNKNOWN_CHAR
                            ),
                            color = MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                },
                status = item.mediaListEntry?.basicMediaListEntry?.status,
                minLines = 1,
                onClick = {
                    navActionManager.toMediaDetails(item.id)
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (isLoggedIn) {
                        events?.selectItem(item)
                        showEditSheet.value = true
                    } else {
                        snackbarManager.showNotLoggedInSnackbar()
                    }
                }
            )
        }
        if (uiState.isLoading) {
            items(13) {
                MediaItemVerticalPlaceholder()
            }
        }
    }//: LazyVerticalGrid
}

@Preview
@Composable
private fun CalendarViewPreview() {
    AniHyouTheme {
        Surface {
            CalendarDayView(
                isLoggedIn = true,
                snackbarManager = rememberSnackbarManager(),
                uiState = CalendarGridUiState(),
                events = null,
                showEditSheet = remember { mutableStateOf(false) },
            )
        }
    }
}
