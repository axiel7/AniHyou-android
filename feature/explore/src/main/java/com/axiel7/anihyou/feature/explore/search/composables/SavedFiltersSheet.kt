package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.sheet.ModalBottomSheet
import com.axiel7.anihyou.feature.explore.search.SearchEvent
import com.axiel7.anihyou.feature.explore.search.SearchUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SavedFiltersSheet(
    uiState: SearchUiState,
    event: SearchEvent?,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(uiState.savedFilters) {
        event?.onShowSavedFilters()
    }
    val listState = rememberLazyListState()
    var showNameDialog by remember { mutableStateOf(false) }

    if (showNameDialog) {
        val nameState = rememberTextFieldState()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(focusRequester) { focusRequester.requestFocus() }

        AlertDialog(
            onDismissRequest = {
                nameState.clearText()
                showNameDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        event?.saveCurrentFilter(nameState.text.toString())
                        showNameDialog = false
                        nameState.clearText()
                    }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            },
            title = { Text(text = stringResource(R.string.filter_name)) },
            text = {
                OutlinedTextField(
                    state = nameState,
                    modifier = Modifier.focusRequester(focusRequester),
                )
            }
        )
    }

    ModalBottomSheet(
        onDismissed = onDismissed,
        modifier = modifier,
        sheetGesturesEnabled = !listState.canScrollBackward,
    ) { dismiss ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
        ) {
            item {
                TextButton(
                    onClick = { showNameDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_24),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(R.string.save_current_filters))
                }
            }
            items(
                items = uiState.savedFilters,
                contentType = { it }
            ) { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.name,
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                event?.applySavedFilter(item)
                                dismiss()
                            }
                            .padding(vertical = 8.dp)
                    )

                    IconButton(
                        onClick = { event?.deleteFilter(item.id) }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.delete_24),
                            contentDescription = stringResource(R.string.delete),
                        )
                    }
                }
            }
        }
    }
}