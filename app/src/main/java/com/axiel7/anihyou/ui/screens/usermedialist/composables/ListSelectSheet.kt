package com.axiel7.anihyou.ui.screens.usermedialist.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.asMediaListStatus
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localizedListStatus
import com.axiel7.anihyou.ui.composables.sheet.SelectionSheet
import com.axiel7.anihyou.ui.composables.sheet.SelectionSheetItem
import com.axiel7.anihyou.ui.screens.usermedialist.UserMediaListUiState
import kotlinx.coroutines.CoroutineScope

@Composable
fun ListSelectSheet(
    uiState: UserMediaListUiState,
    scope: CoroutineScope,
    bottomPadding: Dp,
    onListChanged: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    SelectionSheet(
        scope = scope,
        bottomPadding = bottomPadding,
        onDismiss = onDismiss,
    ) {
        val totalCount = remember(uiState.lists) { uiState.lists.values.sumOf { it.size } }
        SelectionSheetItem(
            name = stringResource(R.string.all),
            icon = R.drawable.list_alt_24,
            count = totalCount,
            isSelected = uiState.selectedListName == null,
            onClick = {
                onListChanged(null)
                onDismiss()
            }
        )
        uiState.lists.keys.forEach { name ->
            val count = remember(name) { uiState.lists[name]?.size ?: 0 }
            SelectionSheetItem(
                name = name.localizedListStatus(),
                icon = name.asMediaListStatus()?.icon(),
                count = count,
                isSelected = uiState.selectedListName == name,
                onClick = {
                    onListChanged(name)
                    onDismiss()
                }
            )
        }
    }
}