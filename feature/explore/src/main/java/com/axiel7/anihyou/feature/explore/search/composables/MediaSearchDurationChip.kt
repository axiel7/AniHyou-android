package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.chip.ChipWithRange
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

const val MAX_EPISODES = 150
const val MAX_DURATION = 170
const val MAX_CHAPTERS = 500
const val MAX_VOLUMES = 50

@Composable
fun MediaSearchDurationChip(
    mediaType: MediaType,
    minEpCh: Int?,
    maxEpCh: Int?,
    minDuration: Int?,
    maxDuration: Int?,
    setEpCh: (IntRange?) -> Unit,
    setDuration: (IntRange?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (mediaType == MediaType.ANIME) {
            ChipWithRange(
                title = stringResource(R.string.episodes),
                startValue = minEpCh?.toFloat(),
                endValue = maxEpCh?.toFloat(),
                minValue = 0f,
                maxValue = MAX_EPISODES.toFloat(),
                onValueChanged = setEpCh,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.play_circle_24),
                        contentDescription = stringResource(R.string.episodes),
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                }
            )
            ChipWithRange(
                title = stringResource(R.string.duration),
                startValue = minDuration?.toFloat(),
                endValue = maxDuration?.toFloat(),
                minValue = 0f,
                maxValue = MAX_DURATION.toFloat(),
                onValueChanged = setDuration,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.timer_24),
                        contentDescription = stringResource(R.string.duration),
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                }
            )
        } else {
            ChipWithRange(
                title = stringResource(R.string.chapters),
                startValue = minEpCh?.toFloat(),
                endValue = maxEpCh?.toFloat(),
                minValue = 0f,
                maxValue = MAX_CHAPTERS.toFloat(),
                onValueChanged = setEpCh,
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.book_24),
                        contentDescription = stringResource(R.string.chapters),
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                }
            )
            ChipWithRange(
                title = stringResource(R.string.volumes),
                startValue = minDuration?.toFloat(),
                endValue = maxDuration?.toFloat(),
                minValue = 0f,
                maxValue = MAX_VOLUMES.toFloat(),
                onValueChanged = setDuration,
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
}

@Preview(showBackground = true)
@Composable
private fun MediaSearchDurationChipPreview() {
    AniHyouTheme {
        MediaSearchDurationChip(
            mediaType = MediaType.ANIME,
            minEpCh = null,
            maxEpCh = null,
            minDuration = null,
            maxDuration = null,
            setEpCh = {},
            setDuration = {},
        )
    }
}