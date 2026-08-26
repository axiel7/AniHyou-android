package com.axiel7.anihyou.feature.explore.recommendations

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.media.RecommendationSortSearch
import com.axiel7.anihyou.core.network.type.RecommendationSort
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.TriFilterChip
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.explore.recommendations.composables.RecommendationItem
import com.axiel7.anihyou.feature.explore.recommendations.composables.RecommendationsSearchChip
import com.axiel7.anihyou.feature.login.LoginView
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun RecommendationsView(
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val viewModel: RecommendationsViewModel = koinViewModel {
        parametersOf(isLoggedIn)
    }
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    RecommendationsContent(
        isLoggedIn = isLoggedIn,
        uiState = uiState.value,
        event = viewModel,
        modifier = modifier,
        contentPadding = contentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun RecommendationsContent(
    isLoggedIn: Boolean,
    uiState: RecommendationsUiState,
    event: RecommendationsEvent?,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val navActionManager = LocalNavActionManager.current
    val snackbarManager = rememberSnackbarManager()
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }

    val haptic = LocalHapticFeedback.current
    var showEditSheet by rememberSaveable { mutableStateOf(false) }

    fun showEditSheetAction() {
        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        if (isLoggedIn) {
            showEditSheet = true
        } else {
            snackbarManager.showNotLoggedInSnackbar()
        }
    }

    if (showEditSheet && uiState.selectedMediaDetails != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedMediaDetails,
            listEntry = uiState.selectedMediaListEntry,
            onEntryUpdated = { newListEntry ->
                event?.onUpdateListEntry(newListEntry)
            },
            onDismissed = { showEditSheet = false }
        )
    }


    val errorString = uiState.errorId?.let { stringResource(uiState.errorId) }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    // I hope this error handling is okay like that
    LaunchedEffect(errorString) {
        errorString?.let {
            event?.showError(it)
            event?.clearErrorId() // needed otherwise the login error would only appear on the first time
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = snackbarManager::SnackbarHost,
        modifier = modifier,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0)
    ) { padding ->
        if (!isLoggedIn && uiState.onMyList == true) {
            RecommendationsFilterRow(
                onMyList = true,
                sort = uiState.sort,
                onSortChange = { event?.onSortChange(it) },
                onMyListChange = { event?.onMyListChange(it) }
            )
            LoginView(modifier = Modifier.padding(padding))
        } else {
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { event?.refresh() },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                state = pullToRefreshState,
                indicator = {
                    PullToRefreshDefaults.LoadingIndicator(
                        state = pullToRefreshState,
                        isRefreshing = uiState.isLoading,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = contentPadding
                ) {
                    item {
                        RecommendationsFilterRow(
                            onMyList = uiState.onMyList,
                            sort = uiState.sort,
                            onSortChange = { event?.onSortChange(it) },
                            onMyListChange = { event?.onMyListChange(it) }
                        )
                    }


                    items(
                        items = uiState.recommendations,
                        key = { it.id },
                        contentType = { it }
                    ) { item ->
                        RecommendationItem(
                            recommendation = item,
                            modifier = Modifier.fillMaxWidth(),
                            onClickMedia = navActionManager::toMediaDetails,
                            onLongClickMedia = { details, listEntry ->
                                event?.selectItem(details, listEntry)
                                showEditSheetAction()
                            },
                            onClickMediaRecommended = navActionManager::toMediaDetails,
                            onVoteClick = { recommendedMediaId, baseMediaId, recommendationId, rating ->
                                event?.onVoteClick(
                                    recommendedMediaId,
                                    baseMediaId,
                                    recommendationId,
                                    rating
                                )
                            },
                            blurAdult = uiState.displayAdult
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationsFilterRow(
    onMyList: Boolean?,
    sort: RecommendationSort,
    onSortChange: (RecommendationSort) -> Unit,
    onMyListChange: (Boolean?) -> Unit,
) {
    Row (
        modifier = Modifier
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TriFilterChip(
            text = stringResource(R.string.on_my_list),
            value = onMyList,
            onValueChanged = { onMyListChange(it) },
            modifier = Modifier.padding(start = 16.dp)
        )
        RecommendationsSearchChip(
            recommendationSortSearch = RecommendationSortSearch.valueOf(sort)
                ?: RecommendationSortSearch.ID,
            onSortChanged = { onSortChange(it) },
        )
    }
}
