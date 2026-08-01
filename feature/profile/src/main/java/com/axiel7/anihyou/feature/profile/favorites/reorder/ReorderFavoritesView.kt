package com.axiel7.anihyou.feature.profile.favorites.reorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.result.LocalResultEventBus
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.common.rememberSnackbarManager
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.profile.favorites.favoritesItems
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun ReorderFavoritesView(
    arguments: Routes.ReorderFavorites,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier,
) {
    val viewModel: ReorderFavoritesViewModel = koinViewModel {
        parametersOf(arguments)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val resultBus = LocalResultEventBus.current

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            resultBus.sendResult(uiState.getList)
            navActionManager.goBack()
        }
    }

    ReorderFavoriteContent(
        uiState = uiState,
        event = viewModel,
        modifier = modifier,
        navActionManager = navActionManager
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ReorderFavoriteContent(
    uiState: ReorderFavoritesUiState,
    event: ReorderFavoritesEvent?,
    modifier: Modifier,
    navActionManager: NavActionManager
) {
    val snackbarManager = rememberSnackbarManager()
    val blurAdult = LocalBlurAdult.current
    val listState = rememberLazyGridState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
        }
    }

    val reorderableLazyGridState = rememberReorderableLazyGridState(listState) { from, to ->
        event?.onMove(from.index, to.index)
    }

    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = {event?.onLoadMore() })
    }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    DefaultScaffoldWithMediumTopAppBar(
        title = stringResource(id = R.string.reorder),
        snackbarHost = snackbarManager::SnackbarHost,
        navigationIcon = {
            BackIconButton(onClick = navActionManager::goBack)
        },
        scrollBehavior = scrollBehavior,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    event?.saveNewOrder()
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.save_24),
                        contentDescription = stringResource(R.string.save)
                    )
                },
                text = { Text(text = stringResource(id = R.string.save)) },
                expanded = isAtTop,
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
            modifier = modifier
                .padding(horizontal = 8.dp)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = padding + PaddingValues(top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            favoritesItems(
                type = uiState.type,
                anime = uiState.anime,
                manga = uiState.manga,
                characters = uiState.characters,
                staff = uiState.staff,
                studios = uiState.studios,
                isLoading = uiState.isLoading,
                blurAdult = blurAdult,
                reorderableState = reorderableLazyGridState,
                onMediaClick = { snackbarManager.showMessage(R.string.reorder_hint) },
                onCharacterClick = { snackbarManager.showMessage(R.string.reorder_hint) },
                onStaffClick = { snackbarManager.showMessage(R.string.reorder_hint) },
                onStudioClick = { snackbarManager.showMessage(R.string.reorder_hint) },
            )
        }
    }
}


@Preview
@Composable
private fun ReorderFavoriteViewPreview() {
    AniHyouTheme {
        Surface {
            ReorderFavoriteContent(
                uiState = ReorderFavoritesUiState(
                    userId = 0,
                    type = FavoritesType.ANIME,
                ),
                event = null,
                modifier = Modifier,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}