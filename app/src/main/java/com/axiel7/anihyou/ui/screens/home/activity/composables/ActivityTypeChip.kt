package com.axiel7.anihyou.ui.screens.home.activity.composables

import androidx.compose.material3.AssistChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.axiel7.anihyou.data.model.activity.ActivityTypeGrouped
import com.axiel7.anihyou.ui.composables.common.DialogWithRadioSelection

@Composable
fun ActivityTypeChip(
    value: ActivityTypeGrouped,
    onValueChanged: (ActivityTypeGrouped) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }

    if (openDialog) {
        DialogWithRadioSelection(
            values = ActivityTypeGrouped.entries.toTypedArray(),
            defaultValue = value,
            isDeselectable = false,
            onConfirm = {
                openDialog = false
                onValueChanged(it ?: value)
            },
            onDismiss = { openDialog = false }
        )
    }

    AssistChip(
        onClick = { openDialog = true },
        label = { Text(text = value.localized()) },
    )
}