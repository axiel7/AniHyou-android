package com.axiel7.anihyou.ui.screens.explore

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.screens.explore.search.SearchContentView
import com.axiel7.anihyou.ui.screens.explore.search.SearchEvent
import com.axiel7.anihyou.ui.screens.explore.search.SearchUiState
import com.axiel7.anihyou.ui.screens.explore.search.SearchViewModel
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun ExploreSearchBar(
    navActionManager: NavActionManager
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ExploreSearchBarContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                performSearch.value = true
            },
            active = isSearchActive,
            onActiveChange = {
                isSearchActive = it
                if (!isSearchActive) query = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = searchHorizontalPadding)
                .padding(bottom = searchBottomPadding),
            placeholder = { Text(text = stringResource(R.string.anime_manga_and_more)) },
            leadingIcon = {
                if (isSearchActive) {
                    IconButton(
                        onClick = {
                            isSearchActive = false
                            query = ""
                        }
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
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close_24),
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            }
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