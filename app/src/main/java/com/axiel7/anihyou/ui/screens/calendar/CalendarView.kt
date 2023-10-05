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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.common.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DefaultTabRowWithPager
import com.axiel7.anihyou.ui.composables.OnMyListChip
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.timestampToTimeString
import com.axiel7.anihyou.utils.UNKNOWN_CHAR
import java.time.DayOfWeek
import java.time.LocalDate

const val CALENDAR_DESTINATION = "CALENDAR/{day}"

private val calendarTabs = arrayOf(
    TabRowItem(value = DayOfWeek.MONDAY, title = R.string.monday),
    TabRowItem(value = DayOfWeek.TUESDAY, title = R.string.tuesday),
    TabRowItem(value = DayOfWeek.WEDNESDAY, title = R.string.wednesday),
    TabRowItem(value = DayOfWeek.THURSDAY, title = R.string.thursday),
    TabRowItem(value = DayOfWeek.FRIDAY, title = R.string.friday),
    TabRowItem(value = DayOfWeek.SATURDAY, title = R.string.saturday),
    TabRowItem(value = DayOfWeek.SUNDAY, title = R.string.sunday)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarView(
    navigateToMediaDetails: (Int) -> Unit,
    navigateBack: () -> Unit
) {
    var onMyList by rememberSaveable { mutableStateOf(false) }

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.calendar),
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            OnMyListChip(
                selected = onMyList,
                onClick = { onMyList = !onMyList },
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        DefaultTabRowWithPager(
            tabs = calendarTabs,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current),
                ),
            initialPage = LocalDate.now().dayOfWeek.value - 1,
            isTabScrollable = true,
        ) {
            CalendarDayView(
                weekday = calendarTabs[it].value.value,
                onMyList = onMyList,
                navigateToMediaDetails = navigateToMediaDetails,
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
fun CalendarDayView(
    weekday: Int,
    onMyList: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToMediaDetails: (Int) -> Unit,
) {
    val viewModel: CalendarViewModel = hiltViewModel()
    val pagingItems = viewModel.weeklyAnime.collectAsLazyPagingItems()

    // TODO: pass these to SavedStateHandle
    LaunchedEffect(weekday) {
        viewModel.setWeekday(weekday)
    }
    LaunchedEffect(onMyList) {
        viewModel.setOnMyList(onMyList)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        if (pagingItems.loadState.refresh is LoadState.Loading) {
            item {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
        items(
            count = pagingItems.itemCount,
            key = pagingItems.itemKey { it.id },
            contentType = { it }
        ) { index ->
            pagingItems[index]?.let { item ->
                MediaItemVertical(
                    title = item.media?.title?.userPreferred ?: "",
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
                    minLines = 1,
                    onClick = {
                        navigateToMediaDetails(item.mediaId)
                    }
                )
            }
        }
        if (pagingItems.loadState.append is LoadState.Loading) {
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
            CalendarView(
                navigateToMediaDetails = {},
                navigateBack = {}
            )
        }
    }
}