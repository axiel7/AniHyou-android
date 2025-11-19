package com.axiel7.anihyou.feature.staffdetails

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.composables.ConnectedButtonGroup
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorDialogHandler
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.common.ShareIconButton
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import com.axiel7.anihyou.feature.staffdetails.content.StaffCharacterView
import com.axiel7.anihyou.feature.staffdetails.content.StaffInfoView
import com.axiel7.anihyou.feature.staffdetails.content.StaffMediaView
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun StaffDetailsView(
    arguments: Routes.StaffDetails,
    navActionManager: NavActionManager
) {
    val viewModel: StaffDetailsViewModel = koinViewModel(parameters = { parametersOf(arguments) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StaffDetailsContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StaffDetailsContent(
    uiState: StaffDetailsUiState,
    event: StaffDetailsEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val haptic = LocalHapticFeedback.current
    var showEditSheet by remember { mutableStateOf(false) }

    if (showEditSheet) {
        uiState.selectedMediaItem?.second?.value?.node?.let { node ->
            EditMediaSheet(
                mediaDetails = node.basicMediaDetails,
                listEntry = node.mediaListEntry?.basicMediaListEntry,
                onEntryUpdated = {
                    event?.onUpdateListEntry(it)
                },
                onDismissed = { showEditSheet = false }
            )
        }
    }

    ErrorDialogHandler(uiState, onDismiss = { event?.onErrorDisplayed() })

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            FavoriteIconButton(
                isFavorite = uiState.details?.isFavourite == true,
                favoritesCount = uiState.details?.favourites ?: 0,
                onClick = {
                    event?.toggleFavorite()
                }
            )
            ShareIconButton(url = uiState.details?.siteUrl.orEmpty())
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
            ConnectedButtonGroup(
                items = StaffInfoType.tabRows,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                selectedIndex = selectedTabIndex,
                onItemSelection = {
                    selectedTabIndex = it
                }
            )
            when (StaffInfoType.tabRows[selectedTabIndex].value) {
                StaffInfoType.INFO ->
                    StaffInfoView(
                        uiState = uiState,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToFullscreenImage = navActionManager::toFullscreenImage
                    )

                StaffInfoType.MEDIA -> {
                    StaffMediaView(
                        staffMedia = uiState.media,
                        isLoading = uiState.isLoadingMedia,
                        loadMore = { event?.loadNextPageMedia() },
                        mediaOnMyList = uiState.mediaOnMyList,
                        setMediaOnMyList = { event?.setMediaOnMyList(it) },
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        showEditSheet = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            event?.selectMediaItem(it)
                            showEditSheet = true
                        },
                        navigateToMediaDetails = navActionManager::toMediaDetails
                    )
                }

                StaffInfoType.CHARACTER -> {
                    StaffCharacterView(
                        staffCharacters = uiState.characters,
                        isLoading = uiState.isLoadingCharacters,
                        loadMore = { event?.loadNextPageCharacters() },
                        charactersOnMyList = uiState.charactersOnMyList,
                        setCharactersOnMyList = { event?.setCharactersOnMyList(it) },
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToCharacterDetails = navActionManager::toCharacterDetails
                    )
                }
            }
        }//: Column
    }
}

@Preview
@Composable
fun StaffDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            StaffDetailsContent(
                uiState = StaffDetailsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}