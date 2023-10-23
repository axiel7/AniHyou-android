package com.axiel7.anihyou.ui.screens.explore.search.composables

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.MediaStatusLocalizable
import com.axiel7.anihyou.ui.composables.common.DialogWithCheckboxSelection

@Composable
fun MediaSearchStatusChip(
    selectedMediaStatuses: List<MediaStatusLocalizable>,
    onMediaStatusesChanged: (List<MediaStatusLocalizable>) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithCheckboxSelection(
            values = MediaStatusLocalizable.entries.toTypedArray(),
            defaultValues = selectedMediaStatuses.toTypedArray(),
            title = stringResource(R.string.media_status),
            onConfirm = {
                openDialog = false
                onMediaStatusesChanged(it)
            },
            onDismiss = { openDialog = false }
        )
    }

    AssistChip(
        onClick = { openDialog = true },
        label = { Text(text = stringResource(R.string.media_status)) },
    )
}