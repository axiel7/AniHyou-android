package com.axiel7.anihyou.feature.usermedialist.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInspectionMode
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
import com.axiel7.anihyou.core.ui.composables.chip.FilterChipWithMenu
import com.axiel7.anihyou.core.ui.composables.common.ErrorTextButton
import com.axiel7.anihyou.core.ui.composables.common.singleClick
import com.axiel7.anihyou.feature.genrestags.composables.SearchGenresTagsChips
import com.axiel7.anihyou.feature.usermedialist.UserMediaListEvent
import com.axiel7.anihyou.feature.usermedialist.UserMediaListUiState
import com.axiel7.anihyou.feature.usermedialist.composables.SortMenu
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopSearchBar(
    uiState: UserMediaListUiState,
    event: UserMediaListEvent?,
    textFieldState: TextFieldState,
    isSearchFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior)
{
    val isPreview = LocalInspectionMode.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler(enabled = isSearchFocused) {
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    Column {
        TopAppBar(
            title = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(min = 360.dp, max = 720.dp)
                        .height(48.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(50),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(start = 4.dp, end = 16.dp)
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Crossfade(
                                targetState = isSearchFocused,
                                label = "SearchIconSwap"
                            ) { focused ->
                                if (focused) {
                                    IconButton(
                                        onClick = {
                                            focusManager.clearFocus()
                                            keyboardController?.hide()
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.arrow_back_24),
                                            contentDescription = stringResource(R.string.action_back),
                                            tint = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            focusRequester.requestFocus()
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.search_24),
                                            contentDescription = stringResource(R.string.search),
                                            tint = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            if (textFieldState.text.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.search_my_list),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                            BasicTextField(
                                state = textFieldState,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                lineLimits = TextFieldLineLimits.SingleLine,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { onFocusChange(it.isFocused) }
                                    .focusRequester(focusRequester)
                            )                        }
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(
                                onClick = { textFieldState.clearText() },
                                modifier = Modifier.wrapContentSize()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.close_24),
                                    contentDescription = stringResource(R.string.delete),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            actions = {
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
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
            scrollBehavior = scrollBehavior,
        )
        val expandedFraction = 1f - scrollBehavior.state.collapsedFraction
        val fastSize = spring<IntSize>(stiffness = Spring.StiffnessMedium)
        val fastAlpha = tween<Float>(100)
        if(expandedFraction > 0f){
            Column(
                modifier = Modifier.animateContentSize(animationSpec = fastSize)
            ) {
                val chipEnter = fadeIn(fastAlpha) + expandHorizontally(fastSize)
                val chipExit = fadeOut(fastAlpha) + shrinkHorizontally(fastSize)

                val rowEnter = fadeIn(fastAlpha) + expandVertically(fastSize)
                val rowExit = fadeOut(fastAlpha) + shrinkVertically(fastSize)


                AnimatedVisibility(
                    visible = isSearchFocused || uiState.filterCount > 0,
                    enter = rowEnter,
                    exit = rowExit
                ) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .animateContentSize(animationSpec = fastSize),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        AnimatedVisibility(
                            visible = isSearchFocused || uiState.mediaFormat != null,
                            enter = chipEnter, exit = chipExit
                        ) {
                            FilterChipWithMenu(
                                title = stringResource(R.string.format),
                                values = if (uiState.mediaType == MediaType.ANIME) MediaFormatLocalizable.animeEntries
                                else MediaFormatLocalizable.mangaEntries,
                                selectedValue = uiState.mediaFormat,
                                onValueSelected = { event?.setMediaFormat(it) },
                                valueString = { it.localized() },
                            )
                        }

                        AnimatedVisibility(
                            visible = isSearchFocused || uiState.mediaStatus != null,
                            enter = chipEnter, exit = chipExit
                        ) {
                            FilterChipWithMenu(
                                title = stringResource(R.string.media_status),
                                values = MediaStatus.knownEntries,
                                selectedValue = uiState.mediaStatus,
                                onValueSelected = { event?.setMediaStatus(it) },
                                valueString = { it.localized() },
                            )
                        }

                        AnimatedVisibility(
                            visible = isSearchFocused || uiState.country != null,
                            enter = chipEnter, exit = chipExit
                        ) {
                            FilterChipWithMenu(
                                title = stringResource(R.string.country),
                                values = CountryOfOrigin.entries,
                                selectedValue = uiState.country,
                                onValueSelected = { event?.setCountry(it) },
                                valueString = { it.localized() },
                            )
                        }

                        AnimatedVisibility(
                            visible = isSearchFocused || uiState.year != null,
                            enter = chipEnter, exit = chipExit
                        ) {
                            FilterChipWithMenu(
                                title = stringResource(R.string.year),
                                values = DateUtils.seasonYears,
                                selectedValue = uiState.year,
                                onValueSelected = { event?.setYear(it) },
                                valueString = { it.toString() },
                            )
                        }

                        AnimatedVisibility(
                            visible = isSearchFocused,
                            enter = chipEnter, exit = chipExit
                        ) {
                            FilterChip(
                                selected = false,
                                onClick = singleClick { event?.getRandomEntry() },
                                label = { Text(text = stringResource(R.string.random)) },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(R.drawable.shuffle_24),
                                        contentDescription = stringResource(R.string.random),
                                        tint = MaterialTheme.colorScheme.onSurface,
                                    )
                                },
                                enabled = !uiState.isLoadingRandom,
                            )
                        }
                    }
                }

                val hasGenreTags = uiState.genresAndTagsForSearch.genreIn.isNotEmpty() ||
                        uiState.genresAndTagsForSearch.genreNot.isNotEmpty() ||
                        uiState.genresAndTagsForSearch.tagIn.isNotEmpty() ||
                        uiState.genresAndTagsForSearch.tagNot.isNotEmpty()

                AnimatedVisibility(
                    visible = !isPreview && (isSearchFocused || hasGenreTags),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    SearchGenresTagsChips(
                        clearedFilters = uiState.clearedFilters,
                        onGenreTagStateChanged = { event?.onGenreTagStateChanged(it) },
                    )
                }

                AnimatedVisibility(
                    visible = uiState.filterCount > 0,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ErrorTextButton(
                        text = stringResource(R.string.clear),
                        onClick = { event?.clearFilters() },
                    )
                }
            }
        }
    }
}