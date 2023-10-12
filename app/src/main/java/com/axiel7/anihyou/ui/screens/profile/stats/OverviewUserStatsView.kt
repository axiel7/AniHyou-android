package com.axiel7.anihyou.ui.screens.profile.stats

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
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.data.model.stats.formatDistribution
import com.axiel7.anihyou.data.model.stats.planned
import com.axiel7.anihyou.data.model.stats.scoreStatsCount
import com.axiel7.anihyou.data.model.stats.scoreStatsTime
import com.axiel7.anihyou.data.model.stats.statusDistribution
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
    TITLES, TIME;

    @Composable
    override fun localized() = when (this) {
        TITLES -> stringResource(R.string.title_count)
        TIME -> stringResource(R.string.time_spent)
    }
}

@Composable
fun OverviewUserStatsView(
    uiState: UserStatsUiState,
    setMediaType: (MediaType) -> Unit,
    setScoreCountType: (ScoreStatCountType) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            MediaType.knownEntries.forEach {
                FilterSelectionChip(
                    selected = uiState.mediaType == it,
                    text = it.localized(),
                    onClick = { setMediaType(it) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            TextSubtitleVertical(
                text = if (uiState.mediaType == MediaType.ANIME)
                    uiState.animeOverview?.count?.format()
                else uiState.mangaOverview?.count?.format(),
                subtitle = stringResource(R.string.total),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = if (uiState.mediaType == MediaType.ANIME)
                    uiState.animeOverview?.episodesWatched?.format()
                else uiState.mangaOverview?.chaptersRead?.format(),
                subtitle = if (uiState.mediaType == MediaType.ANIME) stringResource(R.string.episodes_watched)
                else stringResource(R.string.chapters_read),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = if (uiState.mediaType == MediaType.ANIME)
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
                .padding(8.dp)
        ) {
            TextSubtitleVertical(
                text = if (uiState.mediaType == MediaType.ANIME)
                    uiState.animeOverview?.planned()?.minutesWatched?.toLong()?.minutesToDays()
                        ?.format()
                else uiState.mangaOverview?.planned()?.chaptersRead?.toLong()?.format(),
                subtitle = if (uiState.mediaType == MediaType.ANIME) stringResource(R.string.days_planned)
                else stringResource(R.string.chapters_planned),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = if (uiState.mediaType == MediaType.ANIME)
                    uiState.animeOverview?.meanScore?.format()
                else uiState.mangaOverview?.meanScore?.format(),
                subtitle = stringResource(R.string.mean_score),
                modifier = Modifier.weight(1f),
                isLoading = uiState.isLoading
            )
            TextSubtitleVertical(
                text = if (uiState.mediaType == MediaType.ANIME)
                    uiState.animeOverview?.standardDeviation?.format()
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
                .padding(horizontal = 8.dp)
        ) {
            ScoreStatCountType.entries.forEach {
                FilterSelectionChip(
                    selected = uiState.scoreCountType == it,
                    text = it.localized(),
                    onClick = { setScoreCountType(it) }
                )
            }
        }
        VerticalStatsBar(
            stats = if (uiState.mediaType == MediaType.ANIME) {
                when (uiState.scoreCountType) {
                    ScoreStatCountType.TITLES -> uiState.animeOverview?.scoreStatsCount().orEmpty()
                    ScoreStatCountType.TIME -> uiState.animeOverview?.scoreStatsTime().orEmpty()
                }
            } else {
                when (uiState.scoreCountType) {
                    ScoreStatCountType.TITLES -> uiState.mangaOverview?.scoreStatsCount().orEmpty()
                    ScoreStatCountType.TIME -> uiState.mangaOverview?.scoreStatsTime().orEmpty()
                }
            },
            modifier = Modifier.padding(8.dp),
            isLoading = uiState.isLoading
        )

        // Status distribution
        InfoTitle(text = stringResource(R.string.status_distribution))
        HorizontalStatsBar(
            stats = if (uiState.mediaType == MediaType.ANIME)
                uiState.animeOverview?.statusDistribution().orEmpty()
            else uiState.mangaOverview?.statusDistribution().orEmpty(),
            verticalPadding = 8.dp,
            showTotal = false,
            isLoading = uiState.isLoading
        )

        // Format distribution
        InfoTitle(text = stringResource(R.string.format_distribution))
        HorizontalStatsBar(
            stats = if (uiState.mediaType == MediaType.ANIME)
                uiState.animeOverview?.formatDistribution().orEmpty()
            else uiState.mangaOverview?.formatDistribution().orEmpty(),
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
                setScoreCountType = {}
            )
        }
    }
}