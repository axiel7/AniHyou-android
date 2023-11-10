package com.axiel7.anihyou.ui.screens.characterdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.common.TabRowItem
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.common.ShareIconButton
import com.axiel7.anihyou.ui.screens.characterdetails.composables.CharacterVoiceActorsSheet
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsView(
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: CharacterDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val sheetState = rememberModalBottomSheetState()

    if (sheetState.isVisible) {
        CharacterVoiceActorsSheet(
            voiceActors = viewModel.selectedMediaVoiceActors.orEmpty(),
            sheetState = sheetState,
            navigateToStaffDetails = navigateToStaffDetails,
            onDismiss = {
                scope.launch { sheetState.hide() }
            }
        )
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            FavoriteIconButton(
                isFavorite = uiState.character?.isFavourite ?: false,
                favoritesCount = uiState.character?.favourites ?: 0,
                onClick = {
                    viewModel.toggleFavorite()
                }
            )
            ShareIconButton(url = uiState.character?.siteUrl ?: "")
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
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                selectedIndex = selectedTabIndex,
                onItemSelection = {
                    selectedTabIndex = it
                }
            )

            when (CharacterInfoType.tabRows[selectedTabIndex].value) {
                CharacterInfoType.INFO ->
                    CharacterInfoView(
                        uiState = uiState,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToFullscreenImage = navigateToFullscreenImage,
                    )

                CharacterInfoType.MEDIA -> {
                    LaunchedEffect(uiState.page) {
                        if (uiState.page == 0) viewModel.loadNextPage()
                    }
                    CharacterMediaView(
                        media = viewModel.media,
                        isLoading = uiState.isLoadingMedia,
                        loadMore = viewModel::loadNextPage,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToMediaDetails = navigateToMediaDetails,
                        showVoiceActorsSheet = {
                            scope.launch {
                                viewModel.onShowVoiceActorsSheet(it)
                                sheetState.show()
                            }
                        }
                    )
                }
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
                navigateBack = {},
                navigateToMediaDetails = {},
                navigateToStaffDetails = {},
                navigateToFullscreenImage = {}
            )
        }
    }
}