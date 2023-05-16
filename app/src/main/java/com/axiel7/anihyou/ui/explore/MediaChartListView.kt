package com.axiel7.anihyou.ui.explore

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.ChartType
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val MEDIA_CHART_DESTINATION = "media_chart/{type}"

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
                .padding(padding),
            state = listState
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
                            text = "#${index+1}",
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
    }

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) when (type) {
            ChartType.TOP_ANIME -> viewModel.getMediaChart(MediaType.ANIME, MediaSort.SCORE_DESC)
            ChartType.POPULAR_ANIME -> viewModel.getMediaChart(MediaType.ANIME, MediaSort.POPULARITY_DESC)
            ChartType.TOP_MANGA -> viewModel.getMediaChart(MediaType.MANGA, MediaSort.SCORE_DESC)
            ChartType.POPULAR_MANGA -> viewModel.getMediaChart(MediaType.MANGA, MediaSort.POPULARITY_DESC)
        }
    }
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