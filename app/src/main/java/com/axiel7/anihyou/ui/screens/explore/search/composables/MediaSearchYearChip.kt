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
import com.axiel7.anihyou.data.model.base.GenericLocalizable
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection
import com.axiel7.anihyou.ui.screens.explore.search.SearchViewModel
import com.axiel7.anihyou.utils.DateUtils

@Composable
fun MediaSearchYearChip(
    viewModel: SearchViewModel
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithRadioSelection(
            values = DateUtils.seasonYears.map { GenericLocalizable(it) }.toTypedArray(),
            defaultValue = GenericLocalizable(viewModel.selectedYear),
            title = stringResource(R.string.year),
            isDeselectable = true,
            onConfirm = {
                openDialog = false
                viewModel.onYearChanged(it?.value)
            },
            onDismiss = { openDialog = false }
        )
    }

    AssistChip(
        onClick = { openDialog = true },
        label = { Text(text = stringResource(R.string.year)) }
    )
}