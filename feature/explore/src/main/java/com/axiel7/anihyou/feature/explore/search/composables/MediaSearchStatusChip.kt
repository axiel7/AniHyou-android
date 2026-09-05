package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.chip.FilterChipWithMenu

@Composable
fun MediaSearchStatusChip(
    selectedMediaStatuses: List<MediaStatusLocalizable>,
    onMediaStatusesChanged: (List<MediaStatusLocalizable>) -> Unit,
) {
    FilterChipWithMenu(
        title = stringResource(R.string.media_status),
        values = MediaStatusLocalizable.entries,
        selectedValues = selectedMediaStatuses,
        onValuesChanged = { onMediaStatusesChanged(it) },
        valueString = { it.localized() },
    )
}