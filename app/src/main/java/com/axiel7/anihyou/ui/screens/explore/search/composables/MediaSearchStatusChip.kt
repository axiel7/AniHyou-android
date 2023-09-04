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
import com.axiel7.anihyou.ui.composables.DialogWithCheckboxSelection
import com.axiel7.anihyou.ui.screens.explore.search.SearchViewModel

@Composable
fun MediaSearchStatusChip(
    viewModel: SearchViewModel
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithCheckboxSelection(
            values = MediaStatusLocalizable.entries.toTypedArray(),
            defaultValues = viewModel.selectedMediaStatuses.toTypedArray(),
            title = stringResource(R.string.media_status),
            onConfirm = {
                openDialog = false
                viewModel.onMediaStatusChanged(it)
            },
            onDismiss = { openDialog = false }
        )
    }

    AssistChip(
        onClick = { openDialog = true },
        label = { Text(text = stringResource(R.string.media_status)) },
    )
}