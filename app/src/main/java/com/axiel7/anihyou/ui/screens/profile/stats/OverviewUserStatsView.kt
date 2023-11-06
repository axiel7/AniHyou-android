package com.axiel7.anihyou.ui.screens.profile.stats

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.stats.LengthDistribution
import com.axiel7.anihyou.data.model.stats.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.countryDistribution
import com.axiel7.anihyou.data.model.stats.formatDistribution
import com.axiel7.anihyou.data.model.stats.lengthStatsCount
import com.axiel7.anihyou.data.model.stats.lengthStatsScore
import com.axiel7.anihyou.data.model.stats.lengthStatsTime
import com.axiel7.anihyou.data.model.stats.planned
import com.axiel7.anihyou.data.model.stats.scoreStatsCount
import com.axiel7.anihyou.data.model.stats.scoreStatsTime
import com.axiel7.anihyou.data.model.stats.statusDistribution
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.stats.HorizontalStatsBar
import com.axiel7.anihyou.ui.composables.stats.VerticalStatsBar
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.minutesToDays
import com.axiel7.anihyou.utils.NumberUtils.format

@Composable
fun OverviewUserStatsView(
    uiState: UserStatsUiState,
    setMediaType: (MediaType) -> Unit,
    setScoreType: (ScoreDistribution.Type) -> Unit,
    setLengthType: (LengthDistribution.Type) -> Unit,
) {
    val isAnime = uiState.mediaType == MediaType.ANIME
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            MediaType.knownEntries.forEach {
                FilterSelectionChip(
                    selected = uiState.mediaType == it,
                    text = it.localized(),
                    onClick = { setMediaType(it) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextSubtitleVertical(
                text = if (isAnime) uiState.animeOverview?.count?.format()
                else uiState.mangaOverview?.count?.format(),
                subtitle = stringResource(R.string.total),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = if (isAnime) uiState.animeOverview?.episodesWatched?.format()
                else uiState.mangaOverview?.chaptersRead?.format(),
                subtitle = if (uiState.mediaType == MediaType.ANIME) stringResource(R.string.episodes_watched)
                else stringResource(R.string.chapters_read),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = if (isAnime)
                    uiState.animeOverview?.minutesWatched?.toLong()?.minutesToDays()?.format()
                else uiState.mangaOverview?.volumesRead?.format(),
                subtitle = if (uiState.mediaType == MediaType.ANIME) stringResource(R.string.days_watched)
                else stringResource(R.string.volumes_read),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
        }//: Row

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextSubtitleVertical(
                text = if (isAnime)
                    uiState.animeOverview?.planned()?.minutesWatched?.toLong()?.minutesToDays()
                        ?.format()
                else uiState.mangaOverview?.planned()?.chaptersRead?.toLong()?.format(),
                subtitle = if (uiState.mediaType == MediaType.ANIME) stringResource(R.string.days_planned)
                else stringResource(R.string.chapters_planned),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = if (isAnime) uiState.animeOverview?.meanScore?.format()
                else uiState.mangaOverview?.meanScore?.format(),
                subtitle = stringResource(R.string.mean_score),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = if (isAnime) uiState.animeOverview?.standardDeviation?.format()
                else uiState.mangaOverview?.standardDeviation?.format(),
                subtitle = stringResource(R.string.standard_deviation),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
        }//: Row

        // Score stats
        InfoTitle(text = stringResource(R.string.score))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            ScoreDistribution.Type.entries.forEach {
                FilterSelectionChip(
                    selected = uiState.scoreType == it,
                    text = it.localized(),
                    onClick = { setScoreType(it) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        VerticalStatsBar(
            stats = if (isAnime) {
                when (uiState.scoreType) {
                    ScoreDistribution.Type.TITLES -> uiState.animeOverview?.scoreStatsCount()
                        .orEmpty()

                    ScoreDistribution.Type.TIME -> uiState.animeOverview?.scoreStatsTime().orEmpty()
                }
            } else {
                when (uiState.scoreType) {
                    ScoreDistribution.Type.TITLES -> uiState.mangaOverview?.scoreStatsCount()
                        .orEmpty()

                    ScoreDistribution.Type.TIME -> uiState.mangaOverview?.scoreStatsTime().orEmpty()
                }
            },
            modifier = Modifier.padding(8.dp),
            isLoading = uiState.isLoading
        )

        // Episode/Chapter count
        InfoTitle(
            text = stringResource(if (isAnime) R.string.episode_count else R.string.chapter_count)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            LengthDistribution.Type.entries.forEach {
                FilterSelectionChip(
                    selected = uiState.lengthType == it,
                    text = it.localized(),
                    onClick = { setLengthType(it) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        VerticalStatsBar(
            stats = if (isAnime) {
                when (uiState.lengthType) {
                    LengthDistribution.Type.TITLES -> uiState.animeOverview?.lengthStatsCount()
                        .orEmpty()

                    LengthDistribution.Type.TIME -> uiState.animeOverview?.lengthStatsTime()
                        .orEmpty()

                    LengthDistribution.Type.SCORE -> uiState.animeOverview?.lengthStatsScore()
                        .orEmpty()
                }
            } else {
                when (uiState.lengthType) {
                    LengthDistribution.Type.TITLES -> uiState.mangaOverview?.lengthStatsCount()
                        .orEmpty()

                    LengthDistribution.Type.TIME -> uiState.mangaOverview?.lengthStatsTime()
                        .orEmpty()

                    LengthDistribution.Type.SCORE -> uiState.mangaOverview?.lengthStatsScore()
                        .orEmpty()
                }
            },
            modifier = Modifier.padding(8.dp),
            isLoading = uiState.isLoading
        )

        // Status distribution
        InfoTitle(text = stringResource(R.string.status_distribution))
        HorizontalStatsBar(
            stats = if (isAnime) uiState.animeOverview?.statusDistribution().orEmpty()
            else uiState.mangaOverview?.statusDistribution().orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = uiState.isLoading
        )

        // Format distribution
        InfoTitle(text = stringResource(R.string.format_distribution))
        HorizontalStatsBar(
            stats = if (isAnime) uiState.animeOverview?.formatDistribution().orEmpty()
            else uiState.mangaOverview?.formatDistribution().orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = uiState.isLoading
        )

        // Country distribution
        InfoTitle(text = stringResource(R.string.country_distribution))
        HorizontalStatsBar(
            stats = if (isAnime) uiState.animeOverview?.countryDistribution().orEmpty()
            else uiState.mangaOverview?.countryDistribution().orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = uiState.isLoading
        )
    }//: Column
}

@Preview
@Composable
fun OverviewUserStatsViewPreview() {
    AniHyouTheme {
        Surface {
            OverviewUserStatsView(
                uiState = UserStatsUiState(),
                setMediaType = {},
                setScoreType = {},
                setLengthType = {},
            )
        }
    }
}