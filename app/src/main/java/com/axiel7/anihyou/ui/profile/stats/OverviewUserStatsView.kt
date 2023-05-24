package com.axiel7.anihyou.ui.profile.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.stats.HorizontalStatsBar
import com.axiel7.anihyou.ui.composables.stats.VerticalStatsBar
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.minutesToDays
import com.axiel7.anihyou.utils.NumberUtils.format

enum class ScoreStatCountType : Localizable {
    TITLES {
        @Composable
        override fun localized() = stringResource(R.string.title_count)
    },
    TIME {
        @Composable
        override fun localized() = stringResource(R.string.time_spent)
    },
}

@Composable
fun OverviewUserStatsView(
    viewModel: UserStatsViewModel
) {

    LaunchedEffect(viewModel.mediaType) {
        if (viewModel.animeOverview == null || viewModel.mangaOverview == null)
            viewModel.getOverview()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            MediaType.knownValues().forEach {
                FilterSelectionChip(
                    selected = viewModel.mediaType == it,
                    text = it.localized(),
                    onClick = { viewModel.mediaType = it }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextSubtitleVertical(
                text = if (viewModel.isAnime)
                    viewModel.animeOverview?.count?.format()
                else viewModel.mangaOverview?.count?.format(),
                subtitle = stringResource(R.string.total),
                modifier = Modifier.weight(1f),
                isLoading = viewModel.isLoading
            )
            TextSubtitleVertical(
                text = if (viewModel.isAnime)
                    viewModel.animeOverview?.episodesWatched?.format()
                else viewModel.mangaOverview?.chaptersRead?.format(),
                subtitle = if (viewModel.isAnime) stringResource(R.string.episodes_watched)
                else stringResource(R.string.chapters_read),
                modifier = Modifier.weight(1f),
                isLoading = viewModel.isLoading
            )
            TextSubtitleVertical(
                text = if (viewModel.isAnime)
                    viewModel.animeOverview?.minutesWatched?.toLong()?.minutesToDays()?.format()
                else viewModel.mangaOverview?.volumesRead?.format(),
                subtitle = if (viewModel.isAnime) stringResource(R.string.days_watched)
                else stringResource(R.string.volumes_read),
                modifier = Modifier.weight(1f),
                isLoading = viewModel.isLoading
            )
        }//: Row

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextSubtitleVertical(
                text = if (viewModel.isAnime)
                    viewModel.plannedAnime?.minutesWatched?.toLong()?.minutesToDays()?.format()
                else viewModel.plannedManga?.chaptersRead?.toLong()?.format(),
                subtitle = if (viewModel.isAnime) stringResource(R.string.days_planned)
                else stringResource(R.string.chapters_planned),
                modifier = Modifier.weight(1f),
                isLoading = viewModel.isLoading
            )
            TextSubtitleVertical(
                text = if (viewModel.isAnime)
                    viewModel.animeOverview?.meanScore?.format()
                else viewModel.mangaOverview?.meanScore?.format(),
                subtitle = stringResource(R.string.mean_score),
                modifier = Modifier.weight(1f),
                isLoading = viewModel.isLoading
            )
            TextSubtitleVertical(
                text = if (viewModel.isAnime)
                    viewModel.animeOverview?.standardDeviation?.format()
                else viewModel.mangaOverview?.standardDeviation?.format(),
                subtitle = stringResource(R.string.standard_deviation),
                modifier = Modifier.weight(1f),
                isLoading = viewModel.isLoading
            )
        }//: Row

        // Score stats
        InfoTitle(text = stringResource(R.string.score))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
           ScoreStatCountType.values().forEach {
                FilterSelectionChip(
                    selected = viewModel.scoreCountType == it,
                    text = it.localized(),
                    onClick = { viewModel.scoreCountType = it }
                )
            }
        }
        VerticalStatsBar(
            stats = if (viewModel.isAnime) {
               when (viewModel.scoreCountType) {
                   ScoreStatCountType.TITLES -> viewModel.animeScoreStatsCount
                   ScoreStatCountType.TIME -> viewModel.animeScoreStatsTime
               }
            } else {
                when (viewModel.scoreCountType) {
                    ScoreStatCountType.TITLES -> viewModel.mangaScoreStatsCount
                    ScoreStatCountType.TIME -> viewModel.mangaScoreStatsTime
                }
            },
            modifier = Modifier.padding(8.dp),
            isLoading = viewModel.isLoading
        )

        // Status distribution
        InfoTitle(text = stringResource(R.string.status_distribution))
        HorizontalStatsBar(
            stats = if (viewModel.isAnime) viewModel.animeStatusDistribution
            else viewModel.mangaStatusDistribution,
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = viewModel.isLoading
        )

        // Format distribution
        InfoTitle(text = stringResource(R.string.format_distribution))
        HorizontalStatsBar(
            stats = if (viewModel.isAnime) viewModel.animeFormatDistribution
            else viewModel.mangaFormatDistribution,
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = viewModel.isLoading
        )
    }//: Column
}

@Preview
@Composable
fun OverviewUserStatsViewPreview() {
    AniHyouTheme {
        Surface {
            OverviewUserStatsView(
                viewModel = UserStatsViewModel(userId = 1)
            )
        }
    }
}