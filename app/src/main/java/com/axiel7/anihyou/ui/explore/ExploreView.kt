package com.axiel7.anihyou.ui.explore

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.composables.IconCard
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreView(
    mediaType: MediaType? = null,
    genre: String? = null,
    tag: String? = null,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToMediaChart: (ChartType) -> Unit,
    navigateToAnimeSeason: (year: Int, season: String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val performSearch = remember { mutableStateOf(genre != null || tag != null) }
    var isSearchActive by rememberSaveable { mutableStateOf(genre != null || tag != null) }

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
                    modifier = if (!isSearchActive) Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
                    else Modifier.fillMaxWidth(),
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
                        mediaType = mediaType,
                        genre = genre,
                        tag = tag,
                        navigateToMediaDetails = navigateToMediaDetails,
                        navigateToCharacterDetails = navigateToCharacterDetails,
                        navigateToStaffDetails = navigateToStaffDetails,
                        navigateToUserDetails = navigateToUserDetails
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
        }//: Column
    }//: Scaffold
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    query: String,
    performSearch: MutableState<Boolean>,
    mediaType: MediaType?,
    genre: String?,
    tag: String?,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
) {
    val viewModel: SearchViewModel = viewModel()
    val listState = rememberLazyListState()
    val searchByGenre = remember { mutableStateOf(genre != null || tag != null) }

    LaunchedEffect(mediaType, genre, tag) {
        if (mediaType == MediaType.ANIME) viewModel.searchType = SearchType.ANIME
        else if (mediaType == MediaType.MANGA) viewModel.searchType = SearchType.MANGA

        if (genre != null) viewModel.genreCollection[genre] = true
        if (tag != null) viewModel.tagCollection[tag] = true
    }

    LaunchedEffect(performSearch.value) {
        if (performSearch.value) {
            if (query.isNotBlank() || searchByGenre.value || genre != null || tag != null) {
                listState.scrollToItem(0)
                viewModel.runSearch(query)
                searchByGenre.value = false
            }
            performSearch.value = false
        }
    }

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
                MediaSearchSortChip(
                    viewModel = viewModel,
                    performSearch = performSearch
                )
                MediaSearchGenresChips(
                    viewModel = viewModel,
                    performSearch = performSearch,
                    searchByGenre = searchByGenre,
                )
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
            SearchType.CHARACTER -> {
                items(
                    items = viewModel.searchedCharacters,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    PersonItemHorizontal(
                        title = item.name?.userPreferred ?: "",
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = item.image?.medium,
                        onClick = {
                            navigateToCharacterDetails(item.id)
                        }
                    )
                }
                if (viewModel.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                }
            }
            SearchType.STAFF -> {
                items(
                    items = viewModel.searchedStaff,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    PersonItemHorizontal(
                        title = item.name?.userPreferred ?: "",
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = item.image?.medium,
                        onClick = {
                            navigateToStaffDetails(item.id)
                        }
                    )
                }
                if (viewModel.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                }
            }
            SearchType.STUDIO -> { item { Text(text = "Coming soon") } }
            SearchType.USER -> {
                items(
                    items = viewModel.searchedUsers,
                    key = { it.id },
                    contentType = { it }
                ) { item ->
                    PersonItemHorizontal(
                        title = item.name,
                        modifier = Modifier.fillMaxWidth(),
                        imageUrl = item.avatar?.medium,
                        onClick = {
                            navigateToUserDetails(item.id)
                        }
                    )
                }
                if (viewModel.isLoading) {
                    items(10) {
                        PersonItemHorizontalPlaceholder()
                    }
                }
            }
        }
    }//: LazyColumn
}

@Composable
fun MediaSearchSortChip(
    viewModel: SearchViewModel,
    performSearch: MutableState<Boolean>,
) {
    var openDialog by remember { mutableStateOf(false) }
    var selectedSort by remember {
        mutableStateOf(MediaSortSearch.valueOf(viewModel.mediaSort) ?: MediaSortSearch.SEARCH_MATCH)
    }
    var isDescending by remember { mutableStateOf(true) }

    if (openDialog) {
        DialogWithRadioSelection(
            values = MediaSortSearch.values(),
            defaultValue = selectedSort,
            title = stringResource(R.string.sort),
            onConfirm = {
                selectedSort = it
                viewModel.mediaSort = if (isDescending) it.desc else it.asc
                openDialog = false
                performSearch.value = true
            },
            onDismiss = { openDialog = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = { openDialog = !openDialog },
            label = { Text(text = selectedSort.localized()) },
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

        if (viewModel.mediaSort != MediaSort.SEARCH_MATCH) {
            AssistChip(
                onClick = {
                    isDescending = !isDescending
                    viewModel.mediaSort = if (isDescending) selectedSort.desc else selectedSort.asc
                    performSearch.value = true
                },
                label = {
                    Text(
                        text = if (isDescending) stringResource(R.string.descending)
                        else stringResource(R.string.ascending)
                    )
                },
                modifier = Modifier.padding(8.dp),
            )
        }
    }//: Row
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MediaSearchGenresChips(
    viewModel: SearchViewModel,
    performSearch: MutableState<Boolean>,
    searchByGenre: MutableState<Boolean>
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    if (sheetState.isVisible) {
        GenresTagsSheet(
            viewModel = viewModel,
            sheetState = sheetState,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    searchByGenre.value = true
                    performSearch.value = true
                }
            }
        )
    }

    FlowRow(
        modifier = Modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        viewModel.genreCollection.forEach { (genre, isSelected) ->
            if (isSelected) {
                InputChip(
                    selected = false,
                    onClick = { viewModel.genreCollection[genre] = false },
                    label = { Text(text = genre) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close_20),
                            contentDescription = "remove"
                        )
                    }
                )
            }
        }
        viewModel.tagCollection.forEach { (tag, isSelected) ->
            if (isSelected) {
                InputChip(
                    selected = false,
                    onClick = { viewModel.tagCollection[tag] = false },
                    label = { Text(text = tag) },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.close_20),
                            contentDescription = "remove"
                        )
                    }
                )
            }
        }
        AssistChip(
            onClick = { scope.launch { sheetState.show() } },
            label = { Text(text = stringResource(R.string.add_genre)) },
            leadingIcon = {
                Icon(painter = painterResource(R.drawable.add_24), contentDescription = "add")
            }
        )
    }//: FlowRow
}

@Preview
@Composable
fun ExploreViewPreview() {
    AniHyouTheme {
        Surface {
            ExploreView(
                navigateToMediaDetails = {},
                navigateToMediaChart = {},
                navigateToAnimeSeason = { _, _ -> },
                navigateToCharacterDetails = {},
                navigateToStaffDetails = {},
                navigateToUserDetails = {}
            )
        }
    }
}