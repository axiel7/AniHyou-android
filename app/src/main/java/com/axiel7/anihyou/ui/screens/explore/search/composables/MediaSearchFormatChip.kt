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
import com.axiel7.anihyou.data.model.media.MediaFormatLocalizable
import com.axiel7.anihyou.ui.composables.DialogWithCheckboxSelection
import com.axiel7.anihyou.ui.screens.explore.search.SearchViewModel

@Composable
fun MediaSearchFormatChip(
    viewModel: SearchViewModel
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithCheckboxSelection(
            values = MediaFormatLocalizable.values(),
            defaultValues = viewModel.selectedMediaFormats.toTypedArray(),
            title = stringResource(R.string.format),
            onConfirm = {
                openDialog = false
                viewModel.onMediaFormatChanged(it)
            },
            onDismiss = { openDialog = false }
        )
    }

    AssistChip(
        onClick = { openDialog = true },
        label = { Text(text = stringResource(R.string.format)) },
    )
}