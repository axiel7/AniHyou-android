package com.axiel7.anihyou.ui.screens.profile.favorites

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.ui.composables.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.ui.composables.media.MediaItemVerticalPlaceholder
import com.axiel7.anihyou.ui.composables.person.PersonItemVertical
import com.axiel7.anihyou.ui.composables.person.PersonItemVerticalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFavoritesView(
    userId: Int,
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToStudioDetails: (Int) -> Unit,
) {
    val viewModel: UserFavoritesViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.setUserId(userId)
    }

    val listState = rememberLazyGridState()
    listState.OnBottomReached(buffer = 3, onLoadMore = viewModel::loadNextPage)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            FavoritesType.entries.forEach {
                FilterSelectionChip(
                    selected = uiState.type == it,
                    text = it.localized(),
                    onClick = { viewModel.setType(it) }
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
            when (uiState.type) {
                FavoritesType.ANIME -> {
                    items(
                        items = viewModel.anime,
                        key = { it.id },
                        contentType = { it }
                    ) { item ->
                        MediaItemVertical(
                            title = item.title?.userPreferred ?: "",
                            imageUrl = item.coverImage?.large,
                            modifier = Modifier.wrapContentWidth(),
                            onClick = {
                                navigateToMediaDetails(item.id)
                            }
                        )
                    }
                    if (uiState.isLoading) {
                        items(14) {
                            MediaItemVerticalPlaceholder()
                        }
                    }
                }

                FavoritesType.MANGA -> {
                    items(
                        items = viewModel.manga,
                        key = { it.id },
                        contentType = { it }
                    ) { item ->
                        MediaItemVertical(
                            title = item.title?.userPreferred ?: "",
                            imageUrl = item.coverImage?.large,
                            modifier = Modifier.wrapContentWidth(),
                            onClick = {
                                navigateToMediaDetails(item.id)
                            }
                        )
                    }
                    if (uiState.isLoading) {
                        items(14) {
                            MediaItemVerticalPlaceholder()
                        }
                    }
                }

                FavoritesType.CHARACTERS -> {
                    items(
                        items = viewModel.characters,
                        key = { it.id },
                        contentType = { it }
                    ) { item ->
                        PersonItemVertical(
                            title = item.name?.userPreferred ?: "",
                            imageUrl = item.image?.large,
                            onClick = {
                                navigateToCharacterDetails(item.id)
                            }
                        )
                    }
                    if (uiState.isLoading) {
                        items(14) {
                            PersonItemVerticalPlaceholder()
                        }
                    }
                }

                FavoritesType.STAFF -> {
                    items(
                        items = viewModel.staff,
                        key = { it.id },
                        contentType = { it }
                    ) { item ->
                        PersonItemVertical(
                            title = item.name?.userPreferred ?: "",
                            imageUrl = item.image?.large,
                            onClick = {
                                navigateToStaffDetails(item.id)
                            }
                        )
                    }
                    if (uiState.isLoading) {
                        items(14) {
                            PersonItemVerticalPlaceholder()
                        }
                    }
                }

                FavoritesType.STUDIOS -> {
                    items(
                        items = viewModel.studios,
                        key = { it.id },
                        contentType = { it }
                    ) { item ->
                        Card(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            onClick = { navigateToStudioDetails(item.id) }
                        ) {
                            Text(
                                text = item.name,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontSize = 16.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    if (uiState.isLoading) {
                        items(14) {
                            Card(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .defaultPlaceholder(visible = true),
                            ) {
                                Text(
                                    text = "Loading",
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    fontSize = 16.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }//: LazyVerticalGrid
    }//: Column
}

@Preview
@Composable
fun UserFavoritesViewPreview() {
    AniHyouTheme {
        Surface {
            UserFavoritesView(
                userId = 1,
                navigateToMediaDetails = {},
                navigateToCharacterDetails = {},
                navigateToStaffDetails = {},
                navigateToStudioDetails = {}
            )
        }
    }
}