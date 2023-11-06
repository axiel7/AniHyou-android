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
import com.axiel7.anihyou.data.model.stats.OverviewStats
import com.axiel7.anihyou.data.model.stats.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.YearDistribution
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.ui.composables.stats.HorizontalStatsBar
import com.axiel7.anihyou.ui.composables.stats.VerticalStatsBar
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format

@Composable
fun OverviewUserStatsView(
    stats: OverviewStats?,
    isLoading: Boolean,
    mediaType: MediaType,
    setMediaType: (MediaType) -> Unit,
    scoreType: ScoreDistribution.Type,
    setScoreType: (ScoreDistribution.Type) -> Unit,
    lengthType: LengthDistribution.Type,
    setLengthType: (LengthDistribution.Type) -> Unit,
    releaseYearType: YearDistribution.Type,
    setReleaseYearType: (YearDistribution.Type) -> Unit,
    startYearType: YearDistribution.Type,
    setStartYearType: (YearDistribution.Type) -> Unit,
) {
    val isAnime = mediaType == MediaType.ANIME
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
            ScoreDistribution.Type.entries.forEach {
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
                ScoreDistribution.Type.TITLES -> stats?.scoreCount.orEmpty()
                ScoreDistribution.Type.TIME -> stats?.scoreTime.orEmpty()
            },
            modifier = Modifier.padding(8.dp),
            isLoading = isLoading
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
                    selected = lengthType == it,
                    text = it.localized(),
                    onClick = { setLengthType(it) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        VerticalStatsBar(
            stats = when (lengthType) {
                LengthDistribution.Type.TITLES -> stats?.lengthCount.orEmpty()
                LengthDistribution.Type.TIME -> stats?.lengthTime.orEmpty()
                LengthDistribution.Type.SCORE -> stats?.lengthScore.orEmpty()
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            YearDistribution.Type.entries.forEach {
                FilterSelectionChip(
                    selected = releaseYearType == it,
                    text = it.localized(),
                    onClick = { setReleaseYearType(it) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        VerticalStatsBar(
            stats = when (releaseYearType) {
                YearDistribution.Type.TITLES -> stats?.releaseYearCount.orEmpty()
                YearDistribution.Type.TIME -> stats?.releaseYearTime.orEmpty()
                YearDistribution.Type.SCORE -> stats?.releaseYearScore.orEmpty()
            },
            modifier = Modifier.padding(8.dp),
            isLoading = isLoading
        )

        // Watch/Read year
        InfoTitle(
            text = stringResource(if (isAnime) R.string.watch_year else R.string.read_year)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            YearDistribution.Type.entries.forEach {
                FilterSelectionChip(
                    selected = startYearType == it,
                    text = it.localized(),
                    onClick = { setStartYearType(it) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        VerticalStatsBar(
            stats = when (startYearType) {
                YearDistribution.Type.TITLES -> stats?.startYearCount.orEmpty()
                YearDistribution.Type.TIME -> stats?.startYearTime.orEmpty()
                YearDistribution.Type.SCORE -> stats?.startYearScore.orEmpty()
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
            OverviewUserStatsView(
                stats = null,
                isLoading = true,
                mediaType = MediaType.ANIME,
                setMediaType = {},
                scoreType = ScoreDistribution.Type.TITLES,
                setScoreType = {},
                lengthType = LengthDistribution.Type.TITLES,
                setLengthType = {},
                releaseYearType = YearDistribution.Type.TITLES,
                setReleaseYearType = {},
                startYearType = YearDistribution.Type.TITLES,
                setStartYearType = {},
            )
        }
    }
}