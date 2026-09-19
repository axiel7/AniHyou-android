package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.media.MediaSourceLocalizable
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.chip.FilterChipWithMenu
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MediaSearchSourcesChip(
    selectedSources: ImmutableList<MediaSourceLocalizable>,
    onSourcesChanged: (List<MediaSourceLocalizable>) -> Unit,
) {
    FilterChipWithMenu(
        title = stringResource(R.string.source),
        values = MediaSourceLocalizable.entries.toImmutableList(),
        selectedValues = selectedSources,
        onValuesChanged = { onSourcesChanged(it) },
        valueString = { it.localized() },
    )
}