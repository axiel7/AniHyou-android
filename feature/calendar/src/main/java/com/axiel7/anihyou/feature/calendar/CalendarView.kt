package com.axiel7.anihyou.feature.calendar

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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampToTimeString
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.TabRowWithPager
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.TriFilterChip
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable
fun CalendarView(
    navActionManager: NavActionManager
) {
    val viewModel: CalendarHostViewModel = koinViewModel()
    val onMyList by viewModel.onMyList.collectAsStateWithLifecycle(initialValue = null)

    CalendarViewContent(
        onMyList = onMyList,
        onMyListChanged = viewModel::onMyListChanged,
        navActionManager = navActionManager
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarViewContent(
    onMyList: Boolean?,
    onMyListChanged: (Boolean?) -> Unit,
    navActionManager: NavActionManager
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val showEditSheet = remember { mutableStateOf(false) }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.calendar),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            TriFilterChip(
                text = stringResource(R.string.on_my_list),
                value = onMyList,
                onValueChanged = onMyListChanged,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        },
        scrollBehavior = topAppBarScrollBehavior
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
        ) { page ->
            val weekday = CalendarTab.tabRows[page].value.ordinal + 1
            val viewModel: CalendarViewModel = koinViewModel(key = weekday.toString())
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(weekday) {
                viewModel.setWeekday(weekday)
            }
            LaunchedEffect(onMyList) {
                if (uiState.onMyList != onMyList)
                    viewModel.setOnMyList(onMyList)
            }

            CalendarDayView(
                uiState = uiState,
                events = viewModel,
                showEditSheet = showEditSheet,
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

@Composable
private fun CalendarDayView(
    uiState: CalendarUiState,
    events: CalendarEvent?,
    showEditSheet: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navActionManager: NavActionManager,
) {
    val haptic = LocalHapticFeedback.current

    val listState = rememberLazyGridState()
    listState.OnBottomReached(buffer = 3) {
        events?.onLoadMore()
    }

    if (showEditSheet.value && uiState.selectedItem?.media != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedItem.media!!.basicMediaDetails,
            listEntry = uiState.selectedItem.media!!.mediaListEntry?.basicMediaListEntry,
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
                status = item.media?.mediaListEntry?.basicMediaListEntry?.status,
                minLines = 1,
                onClick = {
                    navActionManager.toMediaDetails(item.mediaId)
                },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    events?.selectItem(item)
                    showEditSheet.value = true
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
fun CalendarViewPreview() {
    AniHyouTheme {
        Surface {
            CalendarDayView(
                uiState = CalendarUiState(),
                events = null,
                showEditSheet = remember { mutableStateOf(false) },
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}