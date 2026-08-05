package com.axiel7.anihyou.feature.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.SearchType
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalNavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Route
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.explore.search.SearchContentView
import com.axiel7.anihyou.feature.explore.search.SearchEvent
import com.axiel7.anihyou.feature.explore.search.SearchUiState
import com.axiel7.anihyou.feature.explore.search.SearchViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ExploreSearchBar(
    isLoggedIn: Boolean,
) {
    val viewModel: SearchViewModel = koinViewModel {
        parametersOf(Route.Search(), isLoggedIn)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExploreSearchBarContent(
        uiState = uiState,
        event = viewModel,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExploreSearchBarContent(
    uiState: SearchUiState,
    event: SearchEvent?,
) {
    val navActionManager = LocalNavActionManager.current
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
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()
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
                                scope.launch {
                                    textFieldState.clearText()
                                    searchBarState.animateToCollapsed()
                                }
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

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBarWithSearch(
            scrollBehavior = scrollBehavior,
            state = searchBarState,
            colors = appBarWithSearchColors,
            inputField = inputField,
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
}

@Preview
@Composable
fun ExploreSearchBarPreview() {
    AniHyouTheme {
        Surface {
            ExploreSearchBarContent(
                uiState = SearchUiState(
                    searchType = SearchType.ANIME,
                    mediaSort = MediaSort.SEARCH_MATCH,
                    isLoggedIn = false,
                ),
                event = null,
            )
        }
    }
}