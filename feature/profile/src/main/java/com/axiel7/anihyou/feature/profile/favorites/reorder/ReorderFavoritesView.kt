package com.axiel7.anihyou.feature.profile.favorites.reorder

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
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
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithMediumTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.compose.viewmodel.koinViewModel
import sh.calvin.reorderable.rememberReorderableLazyGridState
import com.axiel7.anihyou.feature.profile.favorites.favoritesItems

@Composable
fun ReorderFavoritesView(
    userId: Int?,
    type: FavoritesType,
    navActionManager: NavActionManager,
    modifier: Modifier = Modifier,
) {
    val viewModel: ReorderFavoritesViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val resultBus = LocalResultEventBus.current

    LaunchedEffect(userId, type) {
        viewModel.setUserId(userId)
        viewModel.setType(type)
    }

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
        navigationBarOverride = navActionManager
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ReorderFavoriteContent(
    uiState: ReorderFavoritesUiState,
    event: ReorderFavoritesEvent?,
    modifier: Modifier,
    navigationBarOverride: NavActionManager
) {
    val blurAdult = LocalBlurAdult.current
    val listState = rememberLazyGridState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    val reorderableLazyGridState = rememberReorderableLazyGridState(listState) { from, to ->
        when (uiState.type) {
            // type 1: uiState.anime.apply { add(to.index, removeAt(from.index)) }
            // type 2: uiState.anime.apply { this[to.index] = this[from.index].also { this[from.index] = this[to.index] } }
            FavoritesType.ANIME -> { uiState.anime.apply { add(to.index, removeAt(from.index)) } }
            FavoritesType.MANGA -> { uiState.manga.apply { add(to.index, removeAt(from.index)) } }
            FavoritesType.CHARACTERS -> { uiState.characters.apply { add(to.index, removeAt(from.index)) } }
            FavoritesType.STAFF -> { uiState.staff.apply { add(to.index, removeAt(from.index)) } }
            FavoritesType.STUDIOS -> { uiState.studios.apply { add(to.index, removeAt(from.index)) } }
        }
    }

    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = {event?.onLoadMore() })
    }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    DefaultScaffoldWithMediumTopAppBar(
        title = stringResource(id = R.string.reorder),
        navigationIcon = {
            BackIconButton(onClick = navigationBarOverride::goBack)
        },
        scrollBehavior = scrollBehavior,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    event?.saveNewOrder()
                },
                icon = { Icon(painter = painterResource(id = R.drawable.save_24), contentDescription = stringResource(R.string.save))},
                text = { Text(text = stringResource(id = R.string.save)) }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            state = listState,
            contentPadding = PaddingValues(
                start = padding.calculateStartPadding(LocalLayoutDirection.current) + 8.dp,
                top = padding.calculateTopPadding() + 8.dp,
                end = padding.calculateEndPadding(LocalLayoutDirection.current) + 8.dp,
                bottom = padding.calculateBottomPadding() + 8.dp
            ),
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
                reorderableState = reorderableLazyGridState
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
                uiState = ReorderFavoritesUiState(),
                event = null,
                modifier = Modifier,
                navigationBarOverride = NavActionManager.rememberNavActionManager()
            )
        }
    }
}