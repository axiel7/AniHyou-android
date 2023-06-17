package com.axiel7.anihyou.ui.calendar

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DefaultTabRowWithPager
import com.axiel7.anihyou.ui.composables.OnBottomReached
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
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var onMyList by rememberSaveable { mutableStateOf(false) }

    DefaultScaffoldWithSmallTopAppBar(
        title = stringResource(R.string.calendar),
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
              OnMyListChip(
                  selected = onMyList,
                  onClick = {
                      onMyList = !onMyList
                  }
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
    val viewModel: CalendarViewModel = viewModel(key = "$weekday")

    val listState = rememberLazyGridState()
    
    LaunchedEffect(onMyList) {
        viewModel.resetPage()
        viewModel.getAiringAnime(weekday, onMyList)
    }

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage)
            viewModel.getAiringAnime(weekday, onMyList)
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
            items = viewModel.weeklyAnime,
            key = { it.id },
            contentType = { it }
        ) { item ->
            MediaItemVertical(
                title = item.media?.title?.userPreferred ?: "",
                imageUrl = item.media?.coverImage?.large,
                modifier = Modifier.wrapContentWidth(),
                subtitle = {
                    Text(
                        text = stringResource(R.string.episode_airing_at,
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
        if (viewModel.isLoading) {
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