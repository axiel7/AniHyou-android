package com.axiel7.anihyou.ui.screens.studiodetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioDetailsView(
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val viewModel: StudioDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )
    val listState = rememberLazyGridState()
    if (!uiState.isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = viewModel::loadNextPage)
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = uiState.details?.name ?: stringResource(R.string.loading),
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            FavoriteIconButton(
                isFavorite = uiState.details?.isFavourite ?: false,
                favoritesCount = uiState.details?.favourites ?: 0,
                onClick = {
                    viewModel.toggleFavorite()
                }
            )
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current)
                )
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                bottom = padding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            items(
                items = viewModel.studioMedia,
                contentType = { it }
            ) { item ->
                MediaItemVertical(
                    title = item.title?.userPreferred.orEmpty(),
                    imageUrl = item.coverImage?.large,
                    modifier = Modifier.wrapContentWidth(),
                    subtitle = {
                        Text(
                            text = item.startDate?.year?.toString() ?: stringResource(R.string.unknown),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    },
                    minLines = 2,
                    onClick = {
                        navigateToMediaDetails(item.id)
                    }
                )
            }
            if (uiState.isLoading) {
                items(13) {
                    MediaItemVerticalPlaceholder()
                }
            } else if (viewModel.studioMedia.isEmpty()) {
                item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    Text(
                        text = stringResource(R.string.no_media),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }//: Scaffold
}

@Preview
@Composable
fun StudioDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            StudioDetailsView(
                navigateBack = {},
                navigateToMediaDetails = {}
            )
        }
    }
}