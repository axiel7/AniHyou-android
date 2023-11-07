package com.axiel7.anihyou.ui.screens.profile.stats.overview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.stats.StatDistributionType
import com.axiel7.anihyou.data.model.stats.overview.OverviewStats
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.stats.HorizontalStatsBar
import com.axiel7.anihyou.ui.composables.stats.VerticalStatsBar
import com.axiel7.anihyou.ui.screens.profile.stats.composables.DistributionTypeChips
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format

@Composable
fun OverviewStatsView(
    stats: OverviewStats?,
    isLoading: Boolean,
    mediaType: MediaType,
    setMediaType: (MediaType) -> Unit,
    scoreType: StatDistributionType,
    setScoreType: (StatDistributionType) -> Unit,
    lengthType: StatDistributionType,
    setLengthType: (StatDistributionType) -> Unit,
    releaseYearType: StatDistributionType,
    setReleaseYearType: (StatDistributionType) -> Unit,
    startYearType: StatDistributionType,
    setStartYearType: (StatDistributionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isAnime = mediaType == MediaType.ANIME
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            MediaType.knownEntries.forEach {
                FilterSelectionChip(
                    selected = mediaType == it,
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
                text = stats?.count?.format(),
                subtitle = stringResource(R.string.total),
                modifier = Modifier.weight(1f),
                isLoading = isLoading
            )
            TextSubtitleVertical(
                text = stats?.episodeOrChapterCount?.format(),
                subtitle = if (isAnime) stringResource(R.string.episodes_watched)
                else stringResource(R.string.chapters_read),
                modifier = Modifier.weight(1f),
                isLoading = isLoading
            )
            TextSubtitleVertical(
                text = stats?.daysOrVolumes?.format(),
                subtitle = if (isAnime) stringResource(R.string.days_watched)
                else stringResource(R.string.volumes_read),
                modifier = Modifier.weight(1f),
                isLoading = isLoading
            )
        }//: Row

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextSubtitleVertical(
                text = stats?.plannedCount?.format(),
                subtitle = if (isAnime) stringResource(R.string.days_planned)
                else stringResource(R.string.chapters_planned),
                modifier = Modifier.weight(1f),
                isLoading = isLoading
            )
            TextSubtitleVertical(
                text = stats?.meanScore?.format(),
                subtitle = stringResource(R.string.mean_score),
                modifier = Modifier.weight(1f),
                isLoading = isLoading
            )
            TextSubtitleVertical(
                text = stats?.standardDeviation?.format(),
                subtitle = stringResource(R.string.standard_deviation),
                modifier = Modifier.weight(1f),
                isLoading = isLoading
            )
        }//: Row

        // Score stats
        InfoTitle(text = stringResource(R.string.score))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            arrayOf(
                StatDistributionType.TITLES,
                StatDistributionType.TIME
            ).forEach {
                FilterSelectionChip(
                    selected = scoreType == it,
                    text = it.localized(),
                    onClick = { setScoreType(it) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        VerticalStatsBar(
            stats = when (scoreType) {
                StatDistributionType.TITLES -> stats?.scoreCount.orEmpty()
                StatDistributionType.TIME -> stats?.scoreTime.orEmpty()
                else -> emptyList()
            },
            modifier = Modifier.padding(8.dp),
            isLoading = isLoading
        )

        // Episode/Chapter count
        InfoTitle(
            text = stringResource(if (isAnime) R.string.episode_count else R.string.chapter_count)
        )
        DistributionTypeChips(
            value = lengthType,
            onValueChanged = setLengthType,
        )
        VerticalStatsBar(
            stats = when (lengthType) {
                StatDistributionType.TITLES -> stats?.lengthCount.orEmpty()
                StatDistributionType.TIME -> stats?.lengthTime.orEmpty()
                StatDistributionType.SCORE -> stats?.lengthScore.orEmpty()
            },
            modifier = Modifier.padding(8.dp),
            isLoading = isLoading
        )

        // Status distribution
        InfoTitle(text = stringResource(R.string.status_distribution))
        HorizontalStatsBar(
            stats = stats?.statusDistribution.orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = isLoading
        )

        // Format distribution
        InfoTitle(text = stringResource(R.string.format_distribution))
        HorizontalStatsBar(
            stats = stats?.formatDistribution.orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = isLoading
        )

        // Country distribution
        InfoTitle(text = stringResource(R.string.country_distribution))
        HorizontalStatsBar(
            stats = stats?.countryDistribution.orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = isLoading
        )

        // Release year
        InfoTitle(text = stringResource(R.string.release_year))
        DistributionTypeChips(
            value = releaseYearType,
            onValueChanged = setReleaseYearType,
        )
        VerticalStatsBar(
            stats = when (releaseYearType) {
                StatDistributionType.TITLES -> stats?.releaseYearCount.orEmpty()
                StatDistributionType.TIME -> stats?.releaseYearTime.orEmpty()
                StatDistributionType.SCORE -> stats?.releaseYearScore.orEmpty()
            },
            modifier = Modifier.padding(8.dp),
            isLoading = isLoading
        )

        // Watch/Read year
        InfoTitle(
            text = stringResource(if (isAnime) R.string.watch_year else R.string.read_year)
        )
        DistributionTypeChips(
            value = startYearType,
            onValueChanged = setStartYearType,
        )
        VerticalStatsBar(
            stats = when (startYearType) {
                StatDistributionType.TITLES -> stats?.startYearCount.orEmpty()
                StatDistributionType.TIME -> stats?.startYearTime.orEmpty()
                StatDistributionType.SCORE -> stats?.startYearScore.orEmpty()
            },
            modifier = Modifier.padding(8.dp),
            isLoading = isLoading
        )
    }//: Column
}

@Preview
@Composable
fun OverviewUserStatsViewPreview() {
    AniHyouTheme {
        Surface {
            OverviewStatsView(
                stats = null,
                isLoading = true,
                mediaType = MediaType.ANIME,
                setMediaType = {},
                scoreType = StatDistributionType.TITLES,
                setScoreType = {},
                lengthType = StatDistributionType.TITLES,
                setLengthType = {},
                releaseYearType = StatDistributionType.TITLES,
                setReleaseYearType = {},
                startYearType = StatDistributionType.TITLES,
                setStartYearType = {},
            )
        }
    }
}