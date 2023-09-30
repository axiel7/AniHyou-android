package com.axiel7.anihyou.ui.screens.explore.charts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.screens.explore.ExploreViewModel
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val CHART_TYPE_ARGUMENT = "{type}"
const val MEDIA_CHART_DESTINATION = "media_chart/$CHART_TYPE_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaChartListView(
    type: ChartType,
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val viewModel: ExploreViewModel = viewModel()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.loadMoreChart(type)
    }

    DefaultScaffoldWithMediumTopAppBar(
        title = type.localized(),
        navigationIcon = {
            BackIconButton(onClick = navigateBack)
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current)
                ),
            state = listState,
            contentPadding = PaddingValues(
                bottom = padding.calculateBottomPadding()
            ),
        ) {
            itemsIndexed(
                items = viewModel.mediaChart,
                key = { _, item -> item.id },
                contentType = { _, item -> item }
            ) { index, item ->
                MediaItemHorizontal(
                    title = item.title?.userPreferred ?: "",
                    imageUrl = item.coverImage?.large,
                    badgeContent = {
                        Text(
                            text = "#${index + 1}",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    },
                    score = item.meanScore ?: 0,
                    format = item.format ?: MediaFormat.UNKNOWN__,
                    year = item.startDate?.year,
                    onClick = {
                        navigateToMediaDetails(item.id)
                    }
                )
            }
            if (viewModel.isLoading) {
                items(10) {
                    MediaItemHorizontalPlaceholder()
                }
            }
        }
    }//: Scaffold
}

@Preview
@Composable
fun MediaChartListViewPreview() {
    AniHyouTheme {
        Surface {
            MediaChartListView(
                type = ChartType.TOP_ANIME,
                navigateBack = {},
                navigateToMediaDetails = {}
            )
        }
    }
}