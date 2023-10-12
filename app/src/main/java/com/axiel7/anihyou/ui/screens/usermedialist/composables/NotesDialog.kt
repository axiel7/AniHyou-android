package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R

@Composable
fun NotesDialog(
    note: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.ok))
            }
        },
        title = {
            Text(text = stringResource(R.string.notes))
        },
        text = {
            Text(text = note)
        }
    )
}