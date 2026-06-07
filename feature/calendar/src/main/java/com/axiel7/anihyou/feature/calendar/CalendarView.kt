package com.axiel7.anihyou.feature.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampToTimeString
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.common.TabRowItem
import com.axiel7.anihyou.core.ui.composables.SegmentedButtons
import com.axiel7.anihyou.core.ui.composables.TabRowWithPager
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.TriFilterChip
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

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
    var viewMode by remember { mutableStateOf(CalendarViewMode.WEEK) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                )
        ) {
            SegmentedButtons(
                items = arrayOf(
                    TabRowItem(CalendarViewMode.MONTH, title = R.string.month),
                    TabRowItem(CalendarViewMode.WEEK, title = R.string.week),
                    TabRowItem(CalendarViewMode.DAY, title = R.string.day),
                ),
                selectedIndex = CalendarViewMode.entries.indexOf(viewMode),
                onItemSelection = { viewMode = CalendarViewMode.entries[it] },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when (viewMode) {
                CalendarViewMode.MONTH -> CalendarMonthView(
                    onMyList = onMyList,
                    onMyListChanged = onMyListChanged,
                    navActionManager = navActionManager,
                )

                CalendarViewMode.WEEK -> CalendarWeekView(
                    onMyList = onMyList,
                    onMyListChanged = onMyListChanged,
                    showEditSheet = showEditSheet,
                    scrollBehavior = topAppBarScrollBehavior,
                    navActionManager = navActionManager,
                    padding = padding,
                )

                CalendarViewMode.DAY -> CalendarDayDetailView(
                    onMyList = onMyList,
                    onMyListChanged = onMyListChanged,
                    showEditSheet = showEditSheet,
                    scrollBehavior = topAppBarScrollBehavior,
                    navActionManager = navActionManager,
                    padding = padding,
                )
            }
        }
    }
}

@Composable
private fun CalendarMonthView(
    onMyList: Boolean?,
    onMyListChanged: (Boolean?) -> Unit,
    navActionManager: NavActionManager,
) {
    val now = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.from(now)) }
    var selectedDay by remember { mutableStateOf(now) }

    val dayOfWeek = selectedDay?.dayOfWeek ?: DayOfWeek.from(now)
    val weekday = dayOfWeek.value

    val viewModel: CalendarViewModel = koinViewModel(key = "month_${weekday}")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ErrorDialogHandler(uiState, onDismiss = viewModel::onErrorDisplayed)

    LaunchedEffect(weekday) {
        viewModel.setWeekday(weekday)
    }
    LaunchedEffect(onMyList) {
        if (uiState.onMyList != onMyList)
            viewModel.setOnMyList(onMyList)
    }

    val header = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val firstDayOfMonth = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val startDayOfWeek = firstDayOfMonth.dayOfWeek.value - 1

    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3) {
        viewModel.onLoadMore()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
    ) {
        item {
            MonthCalendarGrid(
                currentMonth = currentMonth,
                selectedDay = selectedDay,
                now = now,
                header = header,
                firstDayOfMonth = firstDayOfMonth,
                daysInMonth = daysInMonth,
                startDayOfWeek = startDayOfWeek,
                onDayClick = { selectedDay = it },
                onMonthChange = { currentMonth = it },
            )
        }

        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }

        item {
            Text(
                text = (selectedDay ?: now).let { "${it.month.name.lowercase().replaceFirstChar { c -> c.uppercase() }} ${it.dayOfWeek.name.lowercase().replaceFirstChar { c -> c.uppercase() }}, ${it.dayOfMonth} ${it.year}" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

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
                onClick = { navActionManager.toMediaDetails(item.mediaId) },
            )
        }
        if (uiState.isLoading) {
            items(6) { MediaItemVerticalPlaceholder() }
        }
    }
}

@Composable
private fun MonthCalendarGrid(
    currentMonth: YearMonth,
    selectedDay: LocalDate?,
    now: LocalDate,
    header: List<String>,
    firstDayOfMonth: LocalDate,
    daysInMonth: Int,
    startDayOfWeek: Int,
    onDayClick: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Previous month"
                )
            }
            Text(
                text = "${currentMonth.year} ${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Next month"
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (dayName in header) {
                Text(
                    text = dayName,
                    modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Column {
            val totalCells = startDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until 7) {
                        val cell = row * 7 + col
                        val day = cell - startDayOfWeek + 1
                        val isValidDay = day in 1..daysInMonth
                        val date = if (isValidDay) currentMonth.atDay(day) else null
                        val isToday = date == now
                        val isSelected = date == selectedDay

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .then(
                                    if (isSelected) Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            CircleShape
                                        )
                                    else Modifier
                                )
                                .then(
                                    if (isToday) Modifier
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.primary,
                                            CircleShape
                                        )
                                    else Modifier
                                )
                                .clickable(enabled = isValidDay) {
                                    if (date != null) onDayClick(date)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isValidDay) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarWeekView(
    onMyList: Boolean?,
    onMyListChanged: (Boolean?) -> Unit,
    showEditSheet: MutableState<Boolean>,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
    navActionManager: NavActionManager,
    padding: PaddingValues,
) {
    TabRowWithPager(
        tabs = CalendarTab.tabRows,
        modifier = Modifier.fillMaxSize(),
        initialPage = LocalDate.now().dayOfWeek.value - 1,
        isTabScrollable = true,
    ) { page ->
        val weekday = CalendarTab.tabRows[page].value.ordinal + 1
        val viewModel: CalendarViewModel = koinViewModel(key = weekday.toString())
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
            uiState = uiState,
            events = viewModel,
            showEditSheet = showEditSheet,
            navActionManager = navActionManager,
            modifier = Modifier
                .fillMaxHeight()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = padding.calculateBottomPadding()
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarDayDetailView(
    onMyList: Boolean?,
    onMyListChanged: (Boolean?) -> Unit,
    showEditSheet: MutableState<Boolean>,
    scrollBehavior: androidx.compose.material3.TopAppBarScrollBehavior,
    navActionManager: NavActionManager,
    padding: PaddingValues,
) {
    val now = LocalDate.now()
    var currentDayOffset by remember { mutableIntStateOf(0) }
    val currentDay = now.plusDays(currentDayOffset.toLong())
    val weekday = currentDay.dayOfWeek.value

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "${currentDay.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentDay.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}, ${currentDay.dayOfMonth} ${currentDay.year}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        val viewModel: CalendarViewModel = koinViewModel(key = "day_${weekday}_${currentDayOffset}")
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        ErrorDialogHandler(uiState, onDismiss = viewModel::onErrorDisplayed)

        LaunchedEffect(weekday) {
            viewModel.setWeekday(weekday)
        }
        LaunchedEffect(onMyList) {
            if (uiState.onMyList != onMyList)
                viewModel.setOnMyList(onMyList)
        }

        val haptic = LocalHapticFeedback.current
        val listState = rememberLazyGridState()
        listState.OnBottomReached(buffer = 3) {
            viewModel.onLoadMore()
        }

        val selectedItem = uiState.selectedItem
        if (showEditSheet.value && selectedItem?.media != null) {
            EditMediaSheet(
                mediaDetails = selectedItem.media!!.basicMediaDetails,
                listEntry = selectedItem.media!!.mediaListEntry?.basicMediaListEntry,
                onEntryUpdated = { viewModel.onUpdateListEntry(it) },
                onDismissed = { showEditSheet.value = false }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = padding.calculateBottomPadding()
            ),
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
                    onClick = { navActionManager.toMediaDetails(item.mediaId) },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.selectItem(item)
                        showEditSheet.value = true
                    }
                )
            }
            if (uiState.isLoading) {
                items(8) { MediaItemVerticalPlaceholder() }
            }
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

    val selectedItem = uiState.selectedItem
    if (showEditSheet.value && selectedItem?.media != null) {
        EditMediaSheet(
            mediaDetails = selectedItem.media!!.basicMediaDetails,
            listEntry = selectedItem.media!!.mediaListEntry?.basicMediaListEntry,
            onEntryUpdated = { events?.onUpdateListEntry(it) },
            onDismissed = { showEditSheet.value = false }
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
                onClick = { navActionManager.toMediaDetails(item.mediaId) },
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    events?.selectItem(item)
                    showEditSheet.value = true
                }
            )
        }
        if (uiState.isLoading) {
            items(13) { MediaItemVerticalPlaceholder() }
        }
    }
}

@Preview
@Composable
private fun CalendarViewPreview() {
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
