package com.axiel7.anihyou.ui.screens.calendar

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.AiringAnimesQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DefaultTabRowWithPager
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.common.TriFilterChip
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.timestampToTimeString
import com.axiel7.anihyou.utils.UNKNOWN_CHAR
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun CalendarView(
    navActionManager: NavActionManager
) {
    CalendarViewContent(
        navActionManager = navActionManager
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarViewContent(
    navActionManager: NavActionManager
) {
    var onMyList by rememberSaveable { mutableStateOf<Boolean?>(null) }

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val editSheetState = rememberModalBottomSheetState()

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.calendar),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            TriFilterChip(
                text = stringResource(R.string.on_my_list),
                value = onMyList,
                onValueChanged = { onMyList = it },
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        DefaultTabRowWithPager(
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
        ) { page ->
            val weekday = CalendarTab.tabRows[page].value.ordinal + 1
            val viewModel: CalendarViewModel = hiltViewModel(key = weekday.toString())
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(weekday) {
                viewModel.setWeekday(weekday)
            }
            LaunchedEffect(onMyList) {
                if (uiState.onMyList != onMyList)
                    viewModel.setOnMyList(onMyList)
            }

            CalendarDayView(
                list = viewModel.weeklyAnime,
                uiState = uiState,
                events = viewModel,
                editSheetState = editSheetState,
                navActionManager = navActionManager,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarDayView(
    list: List<AiringAnimesQuery.AiringSchedule>,
    uiState: CalendarUiState,
    events: CalendarEvent?,
    editSheetState: SheetState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navActionManager: NavActionManager,
) {
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val listState = rememberLazyGridState()
    listState.OnBottomReached(buffer = 3) {
        events?.onLoadMore()
    }

    if (editSheetState.isVisible && uiState.selectedItem?.media != null) {
        EditMediaSheet(
            sheetState = editSheetState,
            mediaDetails = uiState.selectedItem.media.basicMediaDetails,
            listEntry = uiState.selectedItem.media.mediaListEntry?.basicMediaListEntry,
            onDismiss = { updatedListEntry ->
                scope.launch {
                    events?.onUpdateListEntry(updatedListEntry)
                    editSheetState.hide()
                }
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
            items = list,
            contentType = { it }
        ) { item ->
            MediaItemVertical(
                title = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                imageUrl = item.media?.coverImage?.large,
                modifier = Modifier.wrapContentWidth(),
                subtitle = {
                    Text(
                        text = stringResource(
                            R.string.episode_airing_at,
                            item.episode,
                            item.airingAt.toLong().timestampToTimeString() ?: UNKNOWN_CHAR
                        ),
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 14.sp,
                        lineHeight = 17.sp
                    )
                },
                badgeContent = item.media?.mediaListEntry?.basicMediaListEntry?.status?.let { status ->
                    {
                        Icon(
                            painter = painterResource(status.icon()),
                            contentDescription = status.localized()
                        )
                    }
                },
                minLines = 1,
                onClick = {
                    navActionManager.toMediaDetails(item.mediaId)
                },
                onLongClick = {
                    scope.launch {
                        events?.selectItem(item)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        editSheetState.show()
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun CalendarViewPreview() {
    AniHyouTheme {
        Surface {
            CalendarDayView(
                list = emptyList(),
                uiState = CalendarUiState(),
                events = null,
                editSheetState = rememberModalBottomSheetState(),
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}