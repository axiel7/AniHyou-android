package com.axiel7.anihyou.ui.screens.explore

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.ui.composables.IconCard
import com.axiel7.anihyou.ui.screens.explore.search.SearchContentView
import com.axiel7.anihyou.ui.screens.explore.search.SearchViewModel
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreView(
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    navigateToStudioDetails: (Int) -> Unit,
    navigateToUserDetails: (Int) -> Unit,
    navigateToMediaChart: (ChartType) -> Unit,
    navigateToAnimeSeason: (year: Int, season: String) -> Unit,
    navigateToCalendar: () -> Unit,
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

    Scaffold(
        modifier = modifier,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val searchViewModel: SearchViewModel = hiltViewModel()
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
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    painter = painterResource(R.drawable.close_24),
                                    contentDescription = stringResource(R.string.delete)
                                )
                            }
                        }
                    }
                ) {
                    SearchContentView(
                        viewModel = searchViewModel,
                        query = query,
                        performSearch = performSearch,
                        initialGenre = null,
                        initialTag = null,
                        navigateToMediaDetails = navigateToMediaDetails,
                        navigateToCharacterDetails = navigateToCharacterDetails,
                        navigateToStaffDetails = navigateToStaffDetails,
                        navigateToStudioDetails = navigateToStudioDetails,
                        navigateToUserDetails = navigateToUserDetails
                    )
                }//:SearchBar
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
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
                    title = stringResource(R.string.upcoming),
                    icon = R.drawable.schedule_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToMediaChart(ChartType.UPCOMING_ANIME)
                    }
                )
                IconCard(
                    title = stringResource(R.string.airing),
                    icon = R.drawable.rss_feed_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToMediaChart(ChartType.AIRING_ANIME)
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
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconCard(
                    title = stringResource(R.string.calendar),
                    icon = R.drawable.calendar_month_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToCalendar()
                    }
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
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
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconCard(
                    title = stringResource(R.string.upcoming),
                    icon = R.drawable.schedule_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToMediaChart(ChartType.UPCOMING_MANGA)
                    }
                )
                IconCard(
                    title = stringResource(R.string.publishing),
                    icon = R.drawable.rss_feed_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navigateToMediaChart(ChartType.PUBLISHING_MANGA)
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
                navigateToMediaDetails = {},
                navigateToMediaChart = {},
                navigateToAnimeSeason = { _, _ -> },
                navigateToCharacterDetails = {},
                navigateToStaffDetails = {},
                navigateToStudioDetails = {},
                navigateToUserDetails = {},
                navigateToCalendar = {}
            )
        }
    }
}