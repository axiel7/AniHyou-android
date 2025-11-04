package com.axiel7.anihyou.feature.characterdetails

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.axiel7.anihyou.core.ui.composables.character.CharacterVoiceActorsSheet
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.FavoriteIconButton
import com.axiel7.anihyou.core.ui.composables.common.ShareIconButton
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.characterdetails.content.CharacterInfoView
import com.axiel7.anihyou.feature.characterdetails.content.CharacterMediaView
import com.axiel7.anihyou.feature.editmedia.EditMediaSheet
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CharacterDetailsView(
    arguments: Routes.CharacterDetails,
    navActionManager: NavActionManager
) {
    val viewModel: CharacterDetailsViewModel = koinViewModel(parameters = { parametersOf(arguments) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CharacterDetailsContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterDetailsContent(
    uiState: CharacterDetailsUiState,
    event: CharacterDetailsEvent?,
    navActionManager: NavActionManager,
) {
    val scope = rememberCoroutineScope()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val haptic = LocalHapticFeedback.current
    var showEditSheet by remember { mutableStateOf(false) }
    var showVaSheet by remember { mutableStateOf(false) }

    if (showVaSheet) {
        CharacterVoiceActorsSheet(
            voiceActors = uiState.selectedMediaVoiceActors.orEmpty(),
            scope = scope,
            navigateToStaffDetails = navActionManager::toStaffDetails,
            onDismiss = {
                showVaSheet = false
            }
        )
    }

    if (showEditSheet && uiState.selectedMediaItem?.node != null) {
        EditMediaSheet(
            mediaDetails = uiState.selectedMediaItem.node!!.basicMediaDetails,
            listEntry = uiState.selectedMediaItem.node?.mediaListEntry?.basicMediaListEntry,
            scope = scope,
            onEntryUpdated = {
                event?.onUpdateListEntry(it)
            },
            onDismissed = { showEditSheet = false }
        )
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            FavoriteIconButton(
                isFavorite = uiState.character?.isFavourite ?: false,
                favoritesCount = uiState.character?.favourites ?: 0,
                onClick = {
                    event?.toggleFavorite()
                }
            )
            ShareIconButton(url = uiState.character?.siteUrl.orEmpty())
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
                items = CharacterDetailsTab.tabRows,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                selectedIndex = selectedTabIndex,
                onItemSelection = {
                    selectedTabIndex = it
                }
            )

            when (CharacterDetailsTab.tabRows[selectedTabIndex].value) {
                CharacterDetailsTab.INFO ->
                    CharacterInfoView(
                        uiState = uiState,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToFullscreenImage = navActionManager::toFullscreenImage,
                    )

                CharacterDetailsTab.MEDIA -> {
                    LaunchedEffect(uiState.page) {
                        if (uiState.page == 0) event?.onLoadMore()
                    }
                    CharacterMediaView(
                        media = uiState.media,
                        isLoading = uiState.isLoadingMedia,
                        loadMore = { event?.onLoadMore() },
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToMediaDetails = navActionManager::toMediaDetails,
                        showVoiceActorsSheet = {
                            event?.onShowVoiceActorsSheet(it)
                            showVaSheet = true
                        },
                        showEditSheet = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            event?.selectMediaItem(it)
                            showEditSheet = true
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
            CharacterDetailsContent(
                uiState = CharacterDetailsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}