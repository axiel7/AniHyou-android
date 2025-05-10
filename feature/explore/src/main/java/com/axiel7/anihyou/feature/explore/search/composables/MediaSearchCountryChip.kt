package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.common.DialogWithRadioSelection

@Composable
fun MediaSearchCountryChip(
    value: CountryOfOrigin?,
    onValueChanged: (CountryOfOrigin?) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithRadioSelection(
            values = CountryOfOrigin.entries.toTypedArray(),
            defaultValue = value,
            title = stringResource(R.string.country),
            showAllCasesOption = true,
            onConfirm = {
                openDialog = false
                onValueChanged(it)
            },
            onDismiss = { openDialog = false }
        )
    }

    FilterChip(
        selected = value != null,
        onClick = { openDialog = true },
        label = { Text(text = value?.localized() ?: stringResource(R.string.country)) },
    )
}