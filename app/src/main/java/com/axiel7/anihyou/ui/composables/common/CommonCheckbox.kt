package com.axiel7.anihyou.ui.composables.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp

@Composable
fun TextCheckbox(
    text: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable {
            onCheckedChange(!checked)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(text = text)
    }
}

@Composable
fun TextTriCheckbox(
    text: String,
    state: ToggleableState,
    onStateChange: (ToggleableState) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable {
            onStateChange(state.toggle())
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        TriStateCheckbox(
            state = state,
            onClick = {
                onStateChange(state.toggle())
            },
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(text = text)
    }
}

fun ToggleableState.toggle() = when (this) {
    ToggleableState.On -> ToggleableState.Indeterminate
    ToggleableState.Off -> ToggleableState.On
    ToggleableState.Indeterminate -> ToggleableState.Off
}