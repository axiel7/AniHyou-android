package com.axiel7.anihyou.feature.explore

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.model.SearchType
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.explore.search.SearchContentView
import com.axiel7.anihyou.feature.explore.search.SearchEvent
import com.axiel7.anihyou.feature.explore.search.SearchUiState
import com.axiel7.anihyou.feature.explore.search.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ExploreSearchBar(
    navActionManager: NavActionManager
) {
    val viewModel: SearchViewModel = koinViewModel(parameters = { parametersOf(Routes.Search()) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExploreSearchBarContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ExploreSearchBarContent(
    uiState: SearchUiState,
    event: SearchEvent?,
    navActionManager: NavActionManager,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val performSearch = remember { mutableStateOf(false) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val searchHorizontalPadding by animateDpAsState(
        targetValue = if (!isSearchActive) 16.dp else 0.dp,
        label = "searchHorizontalPadding"
    )
    val searchBottomPadding by animateDpAsState(
        targetValue = if (!isSearchActive) 4.dp else 0.dp,
        label = "searchBottomPadding"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val onActiveChange: (Boolean) -> Unit = {
            isSearchActive = it
            if (!isSearchActive) query = ""
        }
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {
                        performSearch.value = true
                    },
                    expanded = isSearchActive,
                    onExpandedChange = onActiveChange,
                    placeholder = { Text(text = stringResource(R.string.anime_manga_and_more)) },
                    leadingIcon = {
                        if (isSearchActive) {
                            IconButton(
                                onClick = {
                                    isSearchActive = false
                                    query = ""
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
                        if (isSearchActive && query.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    query = ""
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
            },
            expanded = isSearchActive,
            onExpandedChange = onActiveChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = searchHorizontalPadding)
                .padding(bottom = searchBottomPadding)
        ) {
            SearchContentView(
                query = query,
                performSearch = performSearch,
                initialGenre = null,
                initialTag = null,
                uiState = uiState,
                event = event,
                navActionManager = navActionManager,
            )
        }//:SearchBar
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
                    mediaSort = MediaSort.SEARCH_MATCH
                ),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager(),
            )
        }
    }
}