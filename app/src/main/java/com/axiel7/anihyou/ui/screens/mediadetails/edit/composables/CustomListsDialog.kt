package com.axiel7.anihyou.ui.screens.mediadetails.edit.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.common.SmallCircularProgressIndicator

@Composable
fun CustomListsDialog(
    lists: LinkedHashMap<String, Boolean>,
    isLoading: Boolean,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val selectedLists = remember {
        lists.filterValues { it }.keys.toMutableStateList()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedLists) }
            ) {
                if (isLoading) {
                    SmallCircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = stringResource(R.string.save))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
        title = { Text(text = stringResource(R.string.custom_lists)) },
        text = {
            LazyColumn(
                modifier = Modifier.sizeIn(
                    maxHeight = (configuration.screenHeightDp - 48).dp
                )
            ) {
                items(lists.keys.toTypedArray()) { name ->
                    val isChecked = selectedLists.contains(name)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) selectedLists.remove(name)
                                else selectedLists.add(name)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) selectedLists.add(name)
                                else selectedLists.remove(name)
                            }
                        )
                        Text(text = name)
                    }
                }
            }
            if (lists.isEmpty()) {
                Text(text = stringResource(R.string.no_custom_lists))
            }
        }
    )
}