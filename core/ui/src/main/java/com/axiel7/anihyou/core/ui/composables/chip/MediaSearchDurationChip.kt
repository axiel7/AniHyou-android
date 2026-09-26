package com.axiel7.anihyou.core.ui.composables.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

const val MAX_EPISODES = 150
const val MAX_DURATION = 170
const val MAX_CHAPTERS = 500
const val MAX_VOLUMES = 50

@Composable
fun MediaSearchEpisodesChaptersChip(
    mediaType: MediaType,
    episodesChaptersRange: IntRange?,
    setEpisodesChapters: (IntRange?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (mediaType == MediaType.ANIME) {
        ChipWithRange(
            title = stringResource(R.string.episodes),
            startValue = episodesChaptersRange?.start?.toFloat(),
            endValue = episodesChaptersRange?.endInclusive?.toFloat(),
            modifier = modifier,
            minValue = 0f,
            maxValue = MAX_EPISODES.toFloat(),
            onValueChanged = setEpisodesChapters,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.play_circle_20),
                    contentDescription = stringResource(R.string.episodes),
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        )
    } else {
        ChipWithRange(
            title = stringResource(R.string.chapters),
            startValue = episodesChaptersRange?.start?.toFloat(),
            endValue = episodesChaptersRange?.endInclusive?.toFloat(),
            modifier = modifier,
            minValue = 0f,
            maxValue = MAX_CHAPTERS.toFloat(),
            onValueChanged = setEpisodesChapters,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.book_20),
                    contentDescription = stringResource(R.string.chapters),
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        )
    }
}

@Composable
fun MediaSearchDurationVolumesChip(
    mediaType: MediaType,
    durationVolumesRange: IntRange?,
    setDurationVolumes: (IntRange?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (mediaType == MediaType.ANIME) {
        ChipWithRange(
            title = stringResource(R.string.duration),
            startValue = durationVolumesRange?.start?.toFloat(),
            endValue = durationVolumesRange?.endInclusive?.toFloat(),
            modifier = modifier,
            minValue = 0f,
            maxValue = MAX_DURATION.toFloat(),
            onValueChanged = setDurationVolumes,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.timer_20),
                    contentDescription = stringResource(R.string.duration),
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        )
    } else {
        ChipWithRange(
            title = stringResource(R.string.volumes),
            startValue = durationVolumesRange?.start?.toFloat(),
            endValue = durationVolumesRange?.endInclusive?.toFloat(),
            modifier = modifier,
            minValue = 0f,
            maxValue = MAX_VOLUMES.toFloat(),
            onValueChanged = setDurationVolumes,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.bookmark_20),
                    contentDescription = stringResource(R.string.volumes),
                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaSearchDurationChipPreview() {
    AniHyouTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MediaSearchEpisodesChaptersChip(
                mediaType = MediaType.ANIME,
                episodesChaptersRange = null,
                setEpisodesChapters = {},
            )
            MediaSearchDurationVolumesChip(
                mediaType = MediaType.MANGA,
                durationVolumesRange = null,
                setDurationVolumes = {},
            )
        }
    }
}