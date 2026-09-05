package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.media.MediaSourceLocalizable
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.chip.FilterChipWithMenu

@Composable
fun MediaSearchSourcesChip(
    selectedSources: List<MediaSourceLocalizable>,
    onSourcesChanged: (List<MediaSourceLocalizable>) -> Unit,
) {
    FilterChipWithMenu(
        title = stringResource(R.string.source),
        values = MediaSourceLocalizable.entries,
        selectedValues = selectedSources,
        onValuesChanged = { onSourcesChanged(it) },
        valueString = { it.localized() },
    )
}