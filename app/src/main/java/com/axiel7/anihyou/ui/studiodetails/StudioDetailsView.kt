package com.axiel7.anihyou.ui.studiodetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.abbreviated
import kotlinx.coroutines.launch

const val STUDIO_DETAILS_DESTINATION = "studio/{id}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioDetailsView(
    studioId: Int,
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: StudioDetailsViewModel = viewModel()
    val listState = rememberLazyGridState()
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getStudioDetails(studioId)
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = viewModel.studioDetails?.name ?: stringResource(R.string.loading),
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            FavoriteIconButton(
                isFavorite = viewModel.studioDetails?.isFavourite ?: false,
                onClick = {
                    scope.launch { viewModel.toggleFavorite() }
                }
            )
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = (MEDIA_POSTER_SMALL_WIDTH + 8).dp),
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            items(
                items = viewModel.studioMedia,
                key = { it.id },
                contentType = { it }
            ) { item ->
                MediaItemVertical(
                    title = item.title?.userPreferred ?: "",
                    imageUrl = item.coverImage?.large,
                    modifier = Modifier.wrapContentWidth(),
                    subtitle = {
                        Text(
                            text = item.startDate?.year?.toString() ?: "TBA",
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
            if (viewModel.isLoading) {
                items(13) {
                    MediaItemVerticalPlaceholder()
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
                studioId = 1,
                navigateBack = {},
                navigateToMediaDetails = {}
            )
        }
    }
}