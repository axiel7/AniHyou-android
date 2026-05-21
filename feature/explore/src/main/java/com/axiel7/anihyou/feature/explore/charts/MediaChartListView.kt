package com.axiel7.anihyou.feature.explore.charts

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.network.type.MediaFormat
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun MediaChartListView(
    isLoggedIn: Boolean,
    arguments: Routes.MediaChartList,
    navActionManager: NavActionManager
) {
    val viewModel: MediaChartViewModel = koinViewModel(parameters = { parametersOf(arguments) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaChartListContent(
        isLoggedIn = isLoggedIn,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@Composable
private fun MediaChartListContent(
    isLoggedIn: Boolean,
    uiState: MediaChartUiState,
    event: MediaChartEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    val snackbarManager = rememberSnackbarManager()
    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })

    val haptic = LocalHapticFeedback.current
    var showEditSheet by rememberSaveable { mutableStateOf(false) }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

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
        scrollBehavior = topAppBarScrollBehavior,
        snackbarHost = snackbarManager::SnackbarHost
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
                        if (isLoggedIn) {
                            event?.selectItem(item)
                            showEditSheet = true
                        } else {
                            snackbarManager.showNotLoggedInSnackbar()
                        }
                    },
                    status = item.mediaListEntry?.basicMediaListEntry?.status,
                    topBadgeContent = {
                        Text(
                            text = "#${(index + 1).format()}",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 14.sp
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
private fun MediaChartListViewPreview() {
    AniHyouTheme {
        Surface {
            MediaChartListContent(
                isLoggedIn = true,
                uiState = MediaChartUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
            )
        }
    }
}
