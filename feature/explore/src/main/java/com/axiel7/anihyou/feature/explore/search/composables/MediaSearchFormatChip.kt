package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.chip.FilterChipWithMenu

@Composable
fun MediaSearchFormatChip(
    mediaType: MediaType,
    selectedMediaFormats: List<MediaFormatLocalizable>,
    onMediaFormatsChanged: (List<MediaFormatLocalizable>) -> Unit,
) {
    FilterChipWithMenu(
        title = stringResource(R.string.format),
        values = if (mediaType == MediaType.ANIME) MediaFormatLocalizable.animeEntries
        else MediaFormatLocalizable.mangaEntries,
        selectedValues = selectedMediaFormats,
        onValuesChanged = { onMediaFormatsChanged(it) },
        valueString = { it.localized() },
    )
}