package com.axiel7.anihyou.ui.screens.explore.charts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val CHART_TYPE_ARGUMENT = "{type}"
const val MEDIA_CHART_DESTINATION = "media_chart/$CHART_TYPE_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaChartListView(
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val viewModel: MediaChartViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems = viewModel.mediaChart.collectAsLazyPagingItems()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    DefaultScaffoldWithMediumTopAppBar(
        title = uiState.chartType.localized(),
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
            contentPadding = PaddingValues(
                bottom = padding.calculateBottomPadding()
            ),
        ) {
            if (pagingItems.loadState.refresh is LoadState.Loading) {
                items(10) {
                    MediaItemHorizontalPlaceholder()
                }
            }
            items(
                count = pagingItems.itemCount,
                key = pagingItems.itemKey { it.id },
                contentType = { it }
            ) { index ->
                pagingItems[index]?.let { item ->
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
            }
            if (pagingItems.loadState.append is LoadState.Loading) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
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
                navigateBack = {},
                navigateToMediaDetails = {}
            )
        }
    }
}