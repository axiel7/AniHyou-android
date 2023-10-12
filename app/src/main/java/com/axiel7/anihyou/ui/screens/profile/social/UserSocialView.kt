package com.axiel7.anihyou.ui.screens.profile.social

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.ui.composables.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.person.PersonItemVertical
import com.axiel7.anihyou.ui.composables.person.PersonItemVerticalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserSocialView(
    userId: Int,
    modifier: Modifier = Modifier,
    navigateToUserDetails: (Int) -> Unit,
) {
    val viewModel: UserSocialViewModel = hiltViewModel()
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
            UserSocialType.entries.forEach {
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
                UserSocialType.FOLLOWERS -> {
                    items(
                        items = viewModel.followers,
                        key = { it.userFollow.id },
                        contentType = { it }
                    ) { item ->
                        PersonItemVertical(
                            title = item.userFollow.name,
                            imageUrl = item.userFollow.avatar?.large,
                            onClick = {
                                navigateToUserDetails(item.userFollow.id)
                            }
                        )
                    }
                    if (uiState.isLoading) {
                        items(14) {
                            PersonItemVerticalPlaceholder()
                        }
                    }
                }

                UserSocialType.FOLLOWING -> {
                    items(
                        items = viewModel.following,
                        key = { it.userFollow.id },
                        contentType = { it }
                    ) { item ->
                        PersonItemVertical(
                            title = item.userFollow.name,
                            imageUrl = item.userFollow.avatar?.large,
                            onClick = {
                                navigateToUserDetails(item.userFollow.id)
                            }
                        )
                    }
                    if (uiState.isLoading) {
                        items(14) {
                            PersonItemVerticalPlaceholder()
                        }
                    }
                }
            }
        }//: LazyVerticalGrid
    }//: Column
}

@Preview
@Composable
fun UserSocialViewPreview() {
    AniHyouTheme {
        Surface {
            UserSocialView(
                userId = 1,
                navigateToUserDetails = {}
            )
        }
    }
}