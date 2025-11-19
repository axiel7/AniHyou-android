package com.axiel7.anihyou.core.ui.composables.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.base.state.UiState
import com.axiel7.anihyou.core.resources.R

@Composable
fun ErrorDialogHandler(
    uiState: UiState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            modifier = modifier,
            title = {
                Text(text = "Error")
            },
            text = {
                Text(text = error)
            },
        )
    }
}