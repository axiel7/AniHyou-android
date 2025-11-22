package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.chip.ChipWithMenu

@Composable
fun MediaSearchCountryChip(
    value: CountryOfOrigin?,
    onValueChanged: (CountryOfOrigin?) -> Unit,
) {
    ChipWithMenu(
        title = stringResource(R.string.country),
        values = CountryOfOrigin.entries,
        selectedValue = value,
        onValueSelected = onValueChanged,
        valueString = { it.localized() },
    )
}