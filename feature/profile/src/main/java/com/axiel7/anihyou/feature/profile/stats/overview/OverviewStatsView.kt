package com.axiel7.anihyou.feature.profile.stats.overview

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
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.model.point100PrimaryColor
import com.axiel7.anihyou.core.model.point10PrimaryColor
import com.axiel7.anihyou.core.model.point5PrimaryColor
import com.axiel7.anihyou.core.model.smileyPrimaryColor
import com.axiel7.anihyou.core.model.stats.StatDistributionType
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.InfoTitle
import com.axiel7.anihyou.core.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.core.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.core.ui.composables.stats.HorizontalStatsBar
import com.axiel7.anihyou.core.ui.composables.stats.VerticalStatsBar
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.profile.stats.UserStatsEvent
import com.axiel7.anihyou.feature.profile.stats.UserStatsUiState
import com.axiel7.anihyou.feature.profile.stats.composables.DistributionTypeChips
import com.axiel7.anihyou.feature.profile.stats.composables.MediaTypeChips

@Composable
fun OverviewStatsView(
    uiState: UserStatsUiState,
    event: UserStatsEvent?,
    modifier: Modifier = Modifier,
) {
    val isAnime = uiState.mediaType == MediaType.ANIME
    val stats = if (isAnime) uiState.animeOverview else uiState.mangaOverview
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MediaTypeChips(
            value = uiState.mediaType,
            onValueChanged = { event?.setMediaType(it) }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextSubtitleVertical(
                text = stats?.count?.format(),
                subtitle = stringResource(R.string.total),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = stats?.episodeOrChapterCount?.format(),
                subtitle = if (isAnime) stringResource(R.string.episodes_watched)
                else stringResource(R.string.chapters_read),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = stats?.daysOrVolumes?.format(),
                subtitle = if (isAnime) stringResource(R.string.days_watched)
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
                text = stats?.plannedCount?.format(),
                subtitle = if (isAnime) stringResource(R.string.days_planned)
                else stringResource(R.string.chapters_planned),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = stats?.meanScore?.format(),
                subtitle = stringResource(R.string.mean_score),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = stats?.standardDeviation?.format(),
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
            arrayOf(
                StatDistributionType.TITLES,
                StatDistributionType.TIME
            ).forEach {
                FilterSelectionChip(
                    selected = uiState.scoreType == it,
                    text = it.localized(),
                    onClick = { event?.setScoreType(it) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        VerticalStatsBar(
            stats = when (uiState.scoreType) {
                StatDistributionType.TITLES -> stats?.scoreCount.orEmpty()
                StatDistributionType.TIME -> stats?.scoreTime.orEmpty()
                else -> emptyList()
            },
            modifier = Modifier.padding(8.dp),
            mapColorTo = {
                when (stats?.scoreFormat) {
                    ScoreFormat.POINT_3 -> it.score.smileyPrimaryColor()
                    ScoreFormat.POINT_5 -> it.score.point5PrimaryColor()
                    ScoreFormat.POINT_10 -> it.score.point10PrimaryColor()
                    else -> it.score.point100PrimaryColor()
                }
            },
            isLoading = uiState.isLoading
        )

        // Episode/Chapter count
        InfoTitle(
            text = stringResource(if (isAnime) R.string.episode_count else R.string.chapter_count)
        )
        DistributionTypeChips(
            value = uiState.lengthType,
            onValueChanged = { event?.setLengthType(it) },
        )
        VerticalStatsBar(
            stats = when (uiState.lengthType) {
                StatDistributionType.TITLES -> stats?.lengthCount.orEmpty()
                StatDistributionType.TIME -> stats?.lengthTime.orEmpty()
                StatDistributionType.SCORE -> stats?.lengthScore.orEmpty()
            },
            modifier = Modifier.padding(8.dp),
            isLoading = uiState.isLoading
        )

        // Status distribution
        InfoTitle(text = stringResource(R.string.status_distribution))
        HorizontalStatsBar(
            stats = stats?.statusDistribution.orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = uiState.isLoading
        )

        // Format distribution
        InfoTitle(text = stringResource(R.string.format_distribution))
        HorizontalStatsBar(
            stats = stats?.formatDistribution.orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = uiState.isLoading
        )

        // Country distribution
        InfoTitle(text = stringResource(R.string.country_distribution))
        HorizontalStatsBar(
            stats = stats?.countryDistribution.orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = uiState.isLoading
        )

        // Release year
        InfoTitle(text = stringResource(R.string.release_year))
        DistributionTypeChips(
            value = uiState.releaseYearType,
            onValueChanged = { event?.setReleaseYearType(it) },
        )
        VerticalStatsBar(
            stats = when (uiState.releaseYearType) {
                StatDistributionType.TITLES -> stats?.releaseYearCount.orEmpty()
                StatDistributionType.TIME -> stats?.releaseYearTime.orEmpty()
                StatDistributionType.SCORE -> stats?.releaseYearScore.orEmpty()
            },
            modifier = Modifier.padding(8.dp),
            isLoading = uiState.isLoading
        )

        // Watch/Read year
        InfoTitle(
            text = stringResource(if (isAnime) R.string.watch_year else R.string.read_year)
        )
        DistributionTypeChips(
            value = uiState.startYearType,
            onValueChanged = { event?.setStartYearType(it) },
        )
        VerticalStatsBar(
            stats = when (uiState.startYearType) {
                StatDistributionType.TITLES -> stats?.startYearCount.orEmpty()
                StatDistributionType.TIME -> stats?.startYearTime.orEmpty()
                StatDistributionType.SCORE -> stats?.startYearScore.orEmpty()
            },
            modifier = Modifier.padding(8.dp),
            isLoading = uiState.isLoading
        )
    }//: Column
}

@Preview
@Composable
fun OverviewUserStatsViewPreview() {
    AniHyouTheme {
        Surface {
            OverviewStatsView(
                uiState = UserStatsUiState(),
                event = null,
            )
        }
    }
}