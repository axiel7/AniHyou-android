package com.axiel7.anihyou.ui.screens.characterdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.ShareIconButton
import com.axiel7.anihyou.ui.screens.characterdetails.content.CharacterInfoView
import com.axiel7.anihyou.ui.screens.characterdetails.content.CharacterMediaView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

private enum class CharacterInfoType {
    INFO, MEDIA;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(INFO, icon = R.drawable.info_24),
            TabRowItem(MEDIA, icon = R.drawable.movie_24),
        )
    }
}

const val CHARACTER_ID_ARGUMENT = "{id}"
const val CHARACTER_DETAILS_DESTINATION = "character/$CHARACTER_ID_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsView(
    characterId: Int,
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel = viewModel { CharacterDetailsViewModel(characterId) }
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            FavoriteIconButton(
                isFavorite = viewModel.characterDetails?.isFavourite ?: false,
                favoritesCount = viewModel.characterDetails?.favourites ?: 0,
                onClick = {
                    scope.launch { viewModel.toggleFavorite() }
                }
            )
            ShareIconButton(url = viewModel.characterDetails?.siteUrl ?: "")
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            SegmentedButtons(
                items = CharacterInfoType.tabRows,
                modifier = Modifier.padding(horizontal = 16.dp),
                defaultSelectedIndex = selectedTabIndex,
                onItemSelection = {
                    selectedTabIndex = it
                }
            )

            when (CharacterInfoType.tabRows[selectedTabIndex].value) {
                CharacterInfoType.INFO ->
                    CharacterInfoView(
                        viewModel = viewModel,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToFullscreenImage = navigateToFullscreenImage,
                    )

                CharacterInfoType.MEDIA ->
                    CharacterMediaView(
                        viewModel = viewModel,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToMediaDetails = navigateToMediaDetails
                    )
            }
        }//: Column
    }//: Scaffold
}

@Preview
@Composable
fun CharacterDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            CharacterDetailsView(
                characterId = 1,
                navigateBack = {},
                navigateToMediaDetails = {},
                navigateToFullscreenImage = {}
            )
        }
    }
}