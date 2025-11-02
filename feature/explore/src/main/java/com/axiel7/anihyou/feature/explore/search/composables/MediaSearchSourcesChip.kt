package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.media.MediaSourceLocalizable
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.common.DialogWithCheckboxSelection

@Composable
fun MediaSearchSourcesChip(
    selectedSources: List<MediaSourceLocalizable>,
    onSourcesChanged: (List<MediaSourceLocalizable>) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithCheckboxSelection(
            values = MediaSourceLocalizable.entries,
            defaultValues = selectedSources,
            title = stringResource(R.string.source),
            onConfirm = {
                openDialog = false
                onSourcesChanged(it)
            },
            onDismiss = { openDialog = false }
        )
    }

    FilterChip(
        selected = selectedSources.isNotEmpty(),
        onClick = { openDialog = true },
        label = { Text(text = stringResource(R.string.source)) },
    )
}