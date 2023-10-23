package com.axiel7.anihyou.ui.screens.explore.search.composables

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.composables.common.DialogWithCheckboxSelection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaSearchFormatChip(
    mediaType: MediaType,
    selectedMediaFormats: List<MediaFormatLocalizable>,
    onMediaFormatsChanged: (List<MediaFormatLocalizable>) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithCheckboxSelection(
            values = if (mediaType == MediaType.ANIME) MediaFormatLocalizable.animeEntries
            else MediaFormatLocalizable.mangaEntries,
            defaultValues = selectedMediaFormats.toTypedArray(),
            title = stringResource(R.string.format),
            onConfirm = {
                openDialog = false
                onMediaFormatsChanged(it)
            },
            onDismiss = { openDialog = false }
        )
    }

    FilterChip(
        selected = selectedMediaFormats.isNotEmpty(),
        onClick = { openDialog = true },
        label = { Text(text = stringResource(R.string.format)) },
    )
}