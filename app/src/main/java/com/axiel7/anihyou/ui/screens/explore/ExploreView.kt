package com.axiel7.anihyou.ui.screens.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.ChartType
import com.axiel7.anihyou.type.MediaSeason
import com.axiel7.anihyou.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.ui.composables.IconCard
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils
import kotlinx.serialization.Serializable

@Serializable
object Explore

@Composable
fun ExploreView(
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager
) {
    ExploreContent(
        topBar = {
            ExploreSearchBar(
                navActionManager = navActionManager
            )
        },
        modifier = modifier,
        navActionManager = navActionManager,
    )
}

@Composable
private fun ExploreContent(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navActionManager: NavActionManager,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar
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
                        navActionManager.toMediaChart(ChartType.TOP_ANIME)
                    }
                )
                IconCard(
                    title = stringResource(R.string.top_popular),
                    icon = R.drawable.trending_up_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navActionManager.toMediaChart(ChartType.POPULAR_ANIME)
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
                        navActionManager.toMediaChart(ChartType.UPCOMING_ANIME)
                    }
                )
                IconCard(
                    title = stringResource(R.string.airing),
                    icon = R.drawable.rss_feed_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navActionManager.toMediaChart(ChartType.AIRING_ANIME)
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
                        navActionManager.toAnimeSeason(DateUtils.currentYear, MediaSeason.SPRING)
                    }
                )
                IconCard(
                    title = stringResource(R.string.summer),
                    icon = R.drawable.sunny_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navActionManager.toAnimeSeason(DateUtils.currentYear, MediaSeason.SUMMER)
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
                        navActionManager.toAnimeSeason(DateUtils.currentYear, MediaSeason.FALL)
                    }
                )
                IconCard(
                    title = stringResource(R.string.winter),
                    icon = R.drawable.ac_unit_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navActionManager.toAnimeSeason(DateUtils.currentYear, MediaSeason.WINTER)
                    }
                )
            }
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconCard(
                    title = stringResource(R.string.top_movies),
                    icon = R.drawable.movie_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navActionManager.toMediaChart(ChartType.TOP_MOVIES)
                    }
                )
                IconCard(
                    title = stringResource(R.string.calendar),
                    icon = R.drawable.calendar_month_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navActionManager.toCalendar()
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
                        navActionManager.toMediaChart(ChartType.TOP_MANGA)
                    }
                )
                IconCard(
                    title = stringResource(R.string.top_popular),
                    icon = R.drawable.trending_up_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navActionManager.toMediaChart(ChartType.POPULAR_MANGA)
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
                        navActionManager.toMediaChart(ChartType.UPCOMING_MANGA)
                    }
                )
                IconCard(
                    title = stringResource(R.string.publishing),
                    icon = R.drawable.rss_feed_24,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    onClick = {
                        navActionManager.toMediaChart(ChartType.PUBLISHING_MANGA)
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
            ExploreContent(
                topBar = { ExploreSearchBarPreview() },
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}