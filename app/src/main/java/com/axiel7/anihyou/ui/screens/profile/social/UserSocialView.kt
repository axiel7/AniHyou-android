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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.ui.composables.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_WIDTH
import com.axiel7.anihyou.ui.composables.person.PersonItemVertical
import com.axiel7.anihyou.ui.composables.person.PersonItemVerticalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

enum class UserSocialType : Localizable {
    FOLLOWERS {
        @Composable
        override fun localized() = stringResource(R.string.followers)
    },
    FOLLOWING {
        @Composable
        override fun localized() = stringResource(R.string.following)
    },
}

@Composable
fun UserSocialView(
    userId: Int,
    modifier: Modifier = Modifier,
    navigateToUserDetails: (Int) -> Unit,
) {
    val viewModel: UserSocialViewModel = viewModel()
    val listState = rememberLazyGridState()

    LaunchedEffect(viewModel.userSocialType) {
        viewModel.onUserSocialTypeChanged(userId)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            UserSocialType.values().forEach {
                FilterSelectionChip(
                    selected = viewModel.userSocialType == it,
                    text = it.localized(),
                    onClick = { viewModel.userSocialType = it }
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
            when (viewModel.userSocialType) {
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
                    if (viewModel.isLoading) {
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
                    if (viewModel.isLoading) {
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