package com.axiel7.anihyou.feature.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarScrollBehavior
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.model.SearchType
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.composables.common.singleClick
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.explore.search.SearchContentView
import com.axiel7.anihyou.feature.explore.search.SearchEvent
import com.axiel7.anihyou.feature.explore.search.SearchUiState
import kotlinx.coroutines.launch

@Composable
fun ExploreSearchBar(
    isLoggedIn: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    CustomSearchBar(
        scrollBehavior = scrollBehavior,
    )
    // ExpandedSearchBar has a bug that prevents returning from a navigation destination
    // TODO: revert back when the bug is fixed
    /*
    val viewModel: SearchViewModel = koinViewModel {
        parametersOf(Route.Search(), isLoggedIn)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExploreSearchBarContent(
        uiState = uiState,
        event = viewModel,
        scrollBehavior = scrollBehavior,
    )*/
}

@Composable
private fun CustomSearchBar(
    scrollBehavior: TopAppBarScrollBehavior
) {
    val navActionManager = LocalNavActionManager.current
    TopAppBar(
        title = {
            Card(
                onClick = singleClick { navActionManager.toSearch() },
                modifier = Modifier
                    .widthIn(min = 360.dp, max = 720.dp)
                    .height(56.dp)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(50),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.search_24),
                        contentDescription = stringResource(R.string.search),
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.anime_manga_and_more),
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExploreSearchBarContent(
    uiState: SearchUiState,
    event: SearchEvent?,
    scrollBehavior: SearchBarScrollBehavior
) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val performSearch = remember { mutableStateOf(false) }
    val searchBarState = rememberSearchBarState()
    val isSearchExpanded by remember {
        derivedStateOf {
            searchBarState.currentValue == SearchBarValue.Expanded
        }
    }
    val textFieldState = rememberTextFieldState()
    val appBarWithSearchColors =
        SearchBarDefaults.appBarWithSearchColors(
            searchBarColors = SearchBarDefaults.containedColors(state = searchBarState)
        )

    LaunchedEffect(isSearchExpanded) {
        if (!isSearchExpanded) {
            textFieldState.clearText()
        }
    }

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                textFieldState = textFieldState,
                searchBarState = searchBarState,
                colors = appBarWithSearchColors.searchBarColors.inputFieldColors,
                onSearch = {
                    performSearch.value = true
                    keyboardController?.hide()
                },
                placeholder = { Text(text = stringResource(R.string.anime_manga_and_more)) },
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
                        Icon(
                            painter = painterResource(R.drawable.search_24),
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                },
                trailingIcon = {
                    if (isSearchExpanded && textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textFieldState.clearText()
                                performSearch.value = true
                            },
                            shapes = IconButtonDefaults.shapes()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close_24),
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                }
            )
        }

    AppBarWithSearch(
        state = searchBarState,
        inputField = inputField,
        colors = appBarWithSearchColors,
        contentPadding = WindowInsets.statusBars.asPaddingValues(),
        windowInsets = SearchBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal),
        scrollBehavior = scrollBehavior,
    )
    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = inputField,
        modifier = Modifier.fillMaxWidth()
    ) {
        SearchContentView(
            textFieldState = textFieldState,
            performSearch = performSearch,
            initialGenre = null,
            initialTag = null,
            uiState = uiState,
            event = event,
        )
    }
}

@Preview
@Composable
fun ExploreSearchBarPreview() {
    AniHyouTheme {
        Column {
            ExploreSearchBarContent(
                uiState = SearchUiState(
                    searchType = SearchType.ANIME,
                    mediaSort = MediaSort.SEARCH_MATCH,
                    isLoggedIn = false,
                ),
                event = null,
                scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(),
            )
            CustomSearchBar(
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }
    }
}