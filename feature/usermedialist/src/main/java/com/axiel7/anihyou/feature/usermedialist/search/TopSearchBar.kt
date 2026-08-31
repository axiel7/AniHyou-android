package com.axiel7.anihyou.feature.usermedialist.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExpandedFullScreenContainedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.appBarWithSearchColors
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.common.utils.DateUtils
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.network.type.MediaStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.composables.chip.FilterChipWithMenu
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.ErrorTextButton
import com.axiel7.anihyou.core.ui.composables.common.singleClick
import com.axiel7.anihyou.feature.genrestags.composables.SearchGenresTagsChips
import com.axiel7.anihyou.feature.usermedialist.UserMediaListEvent
import com.axiel7.anihyou.feature.usermedialist.UserMediaListUiState
import com.axiel7.anihyou.feature.usermedialist.composables.SortMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopSearchBar(
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    scrollBehavior: SearchBarScrollBehavior,
) {
    val isPreview = LocalInspectionMode.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val navActionManager = LocalNavActionManager.current
    val scope = rememberCoroutineScope()
    val searchBarState = rememberSearchBarState()
    val textFieldState = rememberTextFieldState()
    val appBarWithSearchColors =
        appBarWithSearchColors(
            searchBarColors = SearchBarDefaults.containedColors(state = searchBarState)
        )
    val isSearchExpanded by remember {
        derivedStateOf { searchBarState.currentValue == SearchBarValue.Expanded }
    }

    SideEffect(isSearchExpanded) {
        if (!isSearchExpanded)
            event?.onSearch(textFieldState.text.toString())
    }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = {
                    keyboardController?.hide()
                    scope.launch {
                        searchBarState.animateToCollapsed()
                    }
                },
                placeholder = { Text(text = stringResource(R.string.search_my_list)) },
                leadingIcon = {
                    if (isSearchExpanded) {
                        IconButton(
                            onClick = {
                                scope.launch { searchBarState.animateToCollapsed() }
                            },
                            shapes = IconButtonDefaults.shapes()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_back_24),
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    } else {
                        BadgedBox(
                            badge = {
                                if (uiState.filterCount > 0) {
                                    Badge {
                                        Text(uiState.filterCount.toString())
                                    }
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.search_24),
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    }
                },
                trailingIcon = {
                    if (isSearchExpanded && textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textFieldState.clearText()
                                event?.onSearch("")
                            },
                            shapes = IconButtonDefaults.shapes()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                },
            )
        }

    Column {
        AppBarWithSearch(
            state = searchBarState,
            inputField = inputField,
            navigationIcon = {
                if (!uiState.isMyList) {
                    BackIconButton(onClick = navActionManager::goBack)
                }
            },
            actions = {
                Box(
                    modifier = Modifier.wrapContentSize(Alignment.TopStart)
                ) {
                    IconButton(
                        onClick = { event?.toggleSortMenu(true) },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.sort_24),
                            contentDescription = stringResource(R.string.sort)
                        )
                    }
                    SortMenu(
                        expanded = uiState.sortMenuExpanded,
                        sort = uiState.sort,
                        onDismiss = {
                            event?.toggleSortMenu(false)
                            event?.setSort(it)
                        }
                    )
                }
            },
            colors = appBarWithSearchColors,
            contentPadding = WindowInsets.statusBars.asPaddingValues(),
            windowInsets = SearchBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal),
            scrollBehavior = scrollBehavior,
        )
        ExpandedFullScreenContainedSearchBar(
            state = searchBarState,
            inputField = inputField,
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipWithMenu(
                    title = stringResource(R.string.format),
                    values = if (uiState.mediaType == MediaType.ANIME) MediaFormatLocalizable.animeEntries
                    else MediaFormatLocalizable.mangaEntries,
                    selectedValue = uiState.mediaFormat,
                    onValueSelected = { event?.setMediaFormat(it) },
                    valueString = { it.localized() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                FilterChipWithMenu(
                    title = stringResource(R.string.media_status),
                    values = MediaStatus.knownEntries,
                    selectedValue = uiState.mediaStatus,
                    onValueSelected = { event?.setMediaStatus(it) },
                    valueString = { it.localized() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                FilterChipWithMenu(
                    title = stringResource(R.string.country),
                    values = CountryOfOrigin.entries,
                    selectedValue = uiState.country,
                    onValueSelected = { event?.setCountry(it) },
                    valueString = { it.localized() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                FilterChipWithMenu(
                    title = stringResource(R.string.year),
                    values = DateUtils.seasonYears,
                    selectedValue = uiState.year,
                    onValueSelected = { event?.setYear(it) },
                    valueString = { it.toString() },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                )
                FilterChip(
                    selected = false,
                    onClick = singleClick { event?.getRandomEntry() },
                    label = { Text(text = stringResource(R.string.random)) },
                    enabled = !uiState.isLoadingRandom,
                )
            }
            if (!isPreview) {
                SearchGenresTagsChips(
                    clearedFilters = uiState.clearedFilters,
                    onGenreTagStateChanged = { event?.onGenreTagStateChanged(it) },
                )
            }
            if (uiState.filterCount > 0) {
                ErrorTextButton(
                    text = stringResource(R.string.clear),
                    onClick = { event?.clearFilters() },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}