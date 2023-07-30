package com.axiel7.anihyou.ui.screens.mediadetails.edit.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.screens.mediadetails.edit.EditMediaViewModel
import kotlinx.coroutines.launch

@Composable
fun DeleteMediaEntryDialog(viewModel: EditMediaViewModel) {
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = { viewModel.openDeleteDialog = false },
        confirmButton = {
            TextButton(
                onClick = {
                    scope.launch { viewModel.deleteListEntry() }
                    viewModel.openDeleteDialog = false
                }
            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.openDeleteDialog = false }) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = { Text(text = stringResource(R.string.delete)) },
        text = { Text(text = stringResource(R.string.delete_confirmation)) }
    )
}