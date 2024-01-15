package com.axiel7.anihyou.ui.screens.staffdetails

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.StaffCharacterQuery
import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.SegmentedButtons
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.common.ShareIconButton
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaSheet
import com.axiel7.anihyou.ui.screens.staffdetails.content.StaffCharacterView
import com.axiel7.anihyou.ui.screens.staffdetails.content.StaffInfoView
import com.axiel7.anihyou.ui.screens.staffdetails.content.StaffMediaView
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun StaffDetailsView(
    navActionManager: NavActionManager
) {
    val viewModel: StaffDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StaffDetailsContent(
        media = viewModel.media,
        characters = viewModel.characters,
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StaffDetailsContent(
    media: List<Pair<Int, StaffMediaGrouped>>,
    characters: List<StaffCharacterQuery.Edge>,
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

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            FavoriteIconButton(
                isFavorite = uiState.details?.isFavourite ?: false,
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
            SegmentedButtons(
                items = StaffInfoType.tabRows,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                        staffMedia = media,
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
                        staffCharacters = characters,
                        isLoading = uiState.isLoadingCharacters,
                        loadMore = { event?.loadNextPageCharacters() },
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
                media = emptyList(),
                characters = emptyList(),
                uiState = StaffDetailsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}