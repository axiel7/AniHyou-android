package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.chip.FilterChipWithMenu
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MediaSearchStatusChip(
    selectedMediaStatuses: ImmutableList<MediaStatusLocalizable>,
    onMediaStatusesChanged: (List<MediaStatusLocalizable>) -> Unit,
) {
    FilterChipWithMenu(
        title = stringResource(R.string.media_status),
        values = MediaStatusLocalizable.entries.toImmutableList(),
        selectedValues = selectedMediaStatuses,
        onValuesChanged = { onMediaStatusesChanged(it) },
        valueString = { it.localized() },
    )
}