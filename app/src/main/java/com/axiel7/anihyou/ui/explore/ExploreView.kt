package com.axiel7.anihyou.ui.explore

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.ChartType
import com.axiel7.anihyou.data.model.MediaSortSearch
import com.axiel7.anihyou.data.model.SearchType
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.composables.IconCard
import com.axiel7.anihyou.ui.composables.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreView(
    navigateToMediaDetails: (Int) -> Unit,
    navigateToMediaChart: (ChartType) -> Unit,
    navigateToAnimeSeason: (year: Int, season: String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    val performSearch = remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
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
                    modifier = if (!isSearchActive) Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                    else Modifier,
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
                                    contentDescription = "back"
                                )
                            }
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.search_24),
                                contentDescription = "search"
                            )
                        }
                    },
                    trailingIcon = {
                        if (isSearchActive && query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    painter = painterResource(R.drawable.close_24),
                                    contentDescription = "delete"
                                )
                            }
                        }
                    }
                ) {
                    SearchView(
                        query = query,
                        performSearch = performSearch,
                        navigateToMediaDetails = navigateToMediaDetails
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Anime
            Text(
                text = stringResource(R.string.anime),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
            
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconCard(
                    title = stringResource(R.string.top_100),
                    icon = R.drawable.star_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToMediaChart(ChartType.TOP_ANIME)
                    }
                )
                IconCard(
                    title = stringResource(R.string.top_popular),
                    icon = R.drawable.trending_up_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToMediaChart(ChartType.POPULAR_ANIME)
                    }
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconCard(
                    title = stringResource(R.string.spring),
                    icon = R.drawable.local_florist_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToAnimeSeason(DateUtils.currentYear, MediaSeason.SPRING.name)
                    }
                )
                IconCard(
                    title = stringResource(R.string.summer),
                    icon = R.drawable.sunny_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToAnimeSeason(DateUtils.currentYear, MediaSeason.SUMMER.name)
                    }
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconCard(
                    title = stringResource(R.string.fall),
                    icon = R.drawable.rainy_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToAnimeSeason(DateUtils.currentYear, MediaSeason.FALL.name)
                    }
                )
                IconCard(
                    title = stringResource(R.string.winter),
                    icon = R.drawable.ac_unit_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToAnimeSeason(DateUtils.currentYear, MediaSeason.WINTER.name)
                    }
                )
            }

            // Manga
            Text(
                text = stringResource(R.string.manga),
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconCard(
                    title = stringResource(R.string.top_100),
                    icon = R.drawable.star_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToMediaChart(ChartType.TOP_MANGA)
                    }
                )
                IconCard(
                    title = stringResource(R.string.top_popular),
                    icon = R.drawable.trending_up_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToMediaChart(ChartType.POPULAR_MANGA)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    query: String,
    performSearch: MutableState<Boolean>,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val viewModel: SearchViewModel = viewModel()
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState
    ) {
        item(contentType = 0) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp)
            ) {
                SearchType.values().forEach {
                    FilterChip(
                        selected = viewModel.searchType == it,
                        onClick = {
                            viewModel.searchType = it
                            performSearch.value = true
                        },
                        label = { Text(text = it.localized()) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        leadingIcon = {
                            if (viewModel.searchType == it) {
                                Icon(painter = painterResource(R.drawable.check_24), contentDescription = "check")
                            }
                        }
                    )
                }
            }
            if (viewModel.searchType == SearchType.ANIME || viewModel.searchType == SearchType.MANGA) {
                MediaSearchSortChip(viewModel = viewModel)
            }
        }
        when (viewModel.searchType) {
            SearchType.ANIME, SearchType.MANGA -> {
                items(
                    items = viewModel.searchedMedia,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    MediaItemHorizontal(
                        title = item.title?.userPreferred ?: "",
                        imageUrl = item.coverImage?.large,
                        score = item.meanScore ?: 0,
                        format = item.format ?: MediaFormat.UNKNOWN__,
                        year = item.startDate?.year,
                        onClick = {
                            navigateToMediaDetails(item.id)
                        }
                    )
                }
                if (viewModel.isLoading) {
                    items(10) {
                        MediaItemHorizontalPlaceholder()
                    }
                }
            }
            SearchType.CHARACTER -> { item { Text(text = "Coming soon") } }
            SearchType.STAFF -> { item { Text(text = "Coming soon") } }
            SearchType.STUDIO -> { item { Text(text = "Coming soon") } }
            SearchType.USER -> { item { Text(text = "Coming soon") } }
        }
    }

    LaunchedEffect(performSearch.value, viewModel.searchType, viewModel.mediaSort) {
        if (performSearch.value && query.isNotBlank()) {
            listState.scrollToItem(0)
            viewModel.runSearch(query)
        }
        performSearch.value = false
    }
}

@Composable
fun MediaSearchSortChip(
    viewModel: SearchViewModel
) {
    var openDialog by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = { openDialog = !openDialog },
            label = { Text(text = viewModel.mediaSort.localized()) },
            modifier = Modifier.padding(8.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.sort_24),
                    contentDescription = stringResource(R.string.sort)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.arrow_drop_down_24),
                    contentDescription = "dropdown",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
    if (openDialog) {
        DialogWithRadioSelection(
            values = MediaSortSearch.values(),
            defaultValue = MediaSortSearch.valueOf(viewModel.mediaSort),
            title = stringResource(R.string.sort),
            onConfirm = {
                viewModel.mediaSort = it.value
                openDialog = false
            },
            onDismiss = { openDialog = false }
        )
    }
}

@Preview
@Composable
fun ExploreViewPreview() {
    AniHyouTheme {
        Surface {
            ExploreView(
                navigateToMediaDetails = {},
                navigateToMediaChart = {},
                navigateToAnimeSeason = { _, _ -> }
            )
        }
    }
}