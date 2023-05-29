package com.axiel7.anihyou.ui.explore

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.ChartType
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.IconCard
import com.axiel7.anihyou.ui.explore.search.SearchView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils

const val EXPLORE_GENRE_DESTINATION = "explore/{mediaType}?genre={genre}?tag={tag}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreView(
    modifier: Modifier = Modifier,
    initialMediaType: MediaType? = null,
    initialGenre: String? = null,
    initialTag: String? = null,
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToStudioDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToMediaChart: (ChartType) -> Unit,
    navigateToAnimeSeason: (year: Int, season: String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val performSearch = remember { mutableStateOf(initialMediaType != null) }
    var isSearchActive by rememberSaveable { mutableStateOf(initialMediaType != null) }
    val searchHorizontalPadding by animateDpAsState(
        targetValue = if (!isSearchActive) 16.dp else 0.dp,
        label = "searchHorizontalPadding"
    )
    val searchBottomPadding by animateDpAsState(
        targetValue = if (!isSearchActive) 4.dp else 0.dp,
        label = "searchBottomPadding"
    )

    Scaffold(
        modifier = modifier,
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
                        if (initialMediaType != null) navigateBack()
                        else {
                            isSearchActive = it
                            if (!isSearchActive) query = ""
                        }
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
                                    if (initialMediaType != null) navigateBack()
                                    else {
                                        isSearchActive = false
                                        query = ""
                                    }
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
                        initialMediaType = initialMediaType,
                        initialGenre = initialGenre,
                        initialTag = initialTag,
                        navigateToMediaDetails = navigateToMediaDetails,
                        navigateToCharacterDetails = navigateToCharacterDetails,
                        navigateToStaffDetails = navigateToStaffDetails,
                        navigateToStudioDetails = navigateToStudioDetails,
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

@Preview
@Composable
fun ExploreViewPreview() {
    AniHyouTheme {
        Surface {
            ExploreView(
                navigateBack = {},
                navigateToMediaDetails = {},
                navigateToMediaChart = {},
                navigateToAnimeSeason = { _, _ -> },
                navigateToCharacterDetails = {},
                navigateToStaffDetails = {},
                navigateToStudioDetails = {},
                navigateToUserDetails = {}
            )
        }
    }
}