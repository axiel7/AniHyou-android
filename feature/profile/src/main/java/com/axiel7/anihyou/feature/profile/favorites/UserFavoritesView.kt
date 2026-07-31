package com.axiel7.anihyou.feature.profile.favorites

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.result.LocalResultEventBus
import androidx.navigation3.runtime.result.ResultEffect
import androidx.navigation3.runtime.result.ResultEventBus
import com.axiel7.anihyou.core.model.FavoritesType
import com.axiel7.anihyou.core.network.UserFavoritesAnimeQuery
import com.axiel7.anihyou.core.network.UserFavoritesCharacterQuery
import com.axiel7.anihyou.core.network.UserFavoritesMangaQuery
import com.axiel7.anihyou.core.network.UserFavoritesStaffQuery
import com.axiel7.anihyou.core.network.UserFavoritesStudioQuery
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.composables.person.PersonItemVertical
import com.axiel7.anihyou.core.ui.composables.person.PersonItemVerticalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserFavoritesView(
    userId: Int,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val viewModel: UserFavoritesViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.setUserId(userId)
    }

    UserFavoritesContent(
        uiState = uiState,
        event = viewModel,
        modifier = modifier,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UserFavoritesContent(
    uiState: UserFavoritesUiState,
    event: UserFavoritesEvent?,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    val blurAdult = LocalBlurAdult.current
    val pullRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyGridState()

    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = { event?.onLoadMore() })
    }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    ResultEffect<List<*>> { result ->
        event?.updateAfterReorderSaved(result)
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navActionManager.toReorderFavorites(userId = uiState.userId, type = uiState.type) },
                modifier = Modifier.safeGesturesPadding(),
                icon = { Icon(painter = painterResource(R.drawable.baseline_swap_vert_24), contentDescription = stringResource(R.string.reorder)) },
                text = { Text(text = stringResource(R.string.reorder)) }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { event?.onRefresh() },
            state = pullRefreshState,
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullRefreshState,
                    isRefreshing = uiState.isLoading,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    FavoritesType.entries.forEach {
                        FilterSelectionChip(
                            selected = uiState.type == it,
                            text = it.localized(),
                            onClick = { event?.setType(it) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }//: Row

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
                    modifier = modifier.padding(horizontal = 8.dp),
                    state = listState,
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
                        onMediaClick = navActionManager::toMediaDetails,
                        onCharacterClick = navActionManager::toCharacterDetails,
                        onStaffClick = navActionManager::toStaffDetails,
                        onStudioClick = navActionManager::toStudioDetails
                    )
                }//: LazyVerticalGrid
            }//: Column
        }
    }
}

@Preview
@Composable
private fun UserFavoritesViewPreview() {
    AniHyouTheme {
        Surface {
            CompositionLocalProvider(LocalResultEventBus provides ResultEventBus()) {
                UserFavoritesContent(
                    uiState = UserFavoritesUiState(),
                    event = null,
                    navActionManager = NavActionManager.rememberNavActionManager()
                )
            }
        }
    }
}