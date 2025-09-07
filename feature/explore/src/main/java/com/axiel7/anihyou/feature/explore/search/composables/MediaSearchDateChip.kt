package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.common.utils.DateUtils
import com.axiel7.anihyou.core.model.media.icon
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.chip.ChipWithMenu
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun MediaSearchDateChip(
    startYear: Int?,
    endYear: Int?,
    season: MediaSeason?,
    onStartYearChanged: (Int?) -> Unit,
    onEndYearChanged: (Int?) -> Unit,
    onSeasonChanged: (MediaSeason?) -> Unit,
) {
    val startYears = remember { DateUtils.seasonYears }
    val endYears = remember(startYear) {
        if (startYear != null) {
            startYears.filter { it >= startYear }
        } else startYears
    }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChipWithMenu(
            title = stringResource(R.string.from_year),
            values = startYears,
            selectedValue = startYear,
            onValueSelected = onStartYearChanged,
            valueString = { it.toString() },
        )
        Text(text = " - ")
        ChipWithMenu(
            title = stringResource(R.string.to_year),
            values = endYears,
            selectedValue = endYear,
            onValueSelected = onEndYearChanged,
            valueString = { it.toString() },
        )

        ChipWithMenu(
            title = stringResource(R.string.season),
            values = MediaSeason.knownEntries,
            selectedValue = season,
            onValueSelected = onSeasonChanged,
            modifier = Modifier.padding(horizontal = 8.dp),
            valueString = { it.localized() },
            valueIcon = { it.icon() }
        )
    }
}

@Preview
@Composable
fun MediaSearchYearChipPreview() {
    AniHyouTheme {
        Surface {
            MediaSearchDateChip(
                startYear = null,
                endYear = null,
                season = null,
                onStartYearChanged = {},
                onEndYearChanged = {},
                onSeasonChanged = {},
            )
        }
    }
}