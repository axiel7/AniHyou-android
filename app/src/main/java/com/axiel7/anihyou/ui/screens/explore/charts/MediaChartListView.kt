package com.axiel7.anihyou.ui.screens.explore.charts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class MediaChartList(val type: String)

@Composable
fun MediaChartListView(
    navActionManager: NavActionManager
) {
    val viewModel: MediaChartViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaChartListContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaChartListContent(
    uiState: MediaChartUiState,
    event: MediaChartEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })

    val haptic = LocalHapticFeedback.current
    var showEditSheet by remember { mutableStateOf(false) }

    if (showEditSheet && uiState.selectedItem != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedItem.basicMediaDetails,
            listEntry = uiState.selectedItem.mediaListEntry?.basicMediaListEntry,
            onEntryUpdated = {
                event?.onUpdateListEntry(it)
            },
            onDismissed = { showEditSheet = false }
        )
    }

    DefaultScaffoldWithMediumTopAppBar(
        title = uiState.chartType?.localized().orEmpty(),
        navigationIcon = {
            BackIconButton(onClick = navActionManager::goBack)
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
                items = uiState.media,
                contentType = { _, item -> item }
            ) { index, item ->
                MediaItemHorizontal(
                    title = item.basicMediaDetails.title?.userPreferred.orEmpty(),
                    imageUrl = item.coverImage?.large,
                    score = item.meanScore ?: 0,
                    format = item.format ?: MediaFormat.UNKNOWN__,
                    year = item.startDate?.year,
                    onClick = {
                        navActionManager.toMediaDetails(item.id)
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        event?.selectItem(item)
                        showEditSheet = true
                    },
                    badgeContent = item.mediaListEntry?.basicMediaListEntry?.status?.let { status ->
                        {
                            Icon(
                                painter = painterResource(status.icon()),
                                contentDescription = status.localized()
                            )
                        }
                    },
                    topBadgeContent = {
                        Text(
                            text = "#${(index + 1).format()}",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                )
            }
            if (uiState.isLoading) {
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
            MediaChartListContent(
                uiState = MediaChartUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
            )
        }
    }
}