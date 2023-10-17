package com.axiel7.anihyou.ui.screens.explore.search.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.media.MediaSortSearch
import com.axiel7.anihyou.type.MediaSort
import com.axiel7.anihyou.ui.composables.DialogWithRadioSelection

@Composable
fun MediaSearchSortChip(
    mediaSortSearch: MediaSortSearch,
    onSortChanged: (MediaSort) -> Unit,
) {
    var openDialog by remember { mutableStateOf(false) }
    var isDescending by remember { mutableStateOf(true) }

    if (openDialog) {
        DialogWithRadioSelection(
            values = MediaSortSearch.entries.toTypedArray(),
            defaultValue = mediaSortSearch,
            title = stringResource(R.string.sort),
            isDeselectable = false,
            onConfirm = {
                onSortChanged(
                    (if (isDescending) it?.desc else it?.asc) ?: MediaSort.SEARCH_MATCH
                )
                openDialog = false
            },
            onDismiss = { openDialog = false }
        )
    }

    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(
            onClick = { openDialog = !openDialog },
            label = { Text(text = mediaSortSearch.localized()) },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.sort_24),
                    contentDescription = stringResource(R.string.sort)
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.arrow_drop_down_24),
                    contentDescription = "dropdown",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        )

        if (mediaSortSearch != MediaSortSearch.SEARCH_MATCH) {
            AssistChip(
                onClick = {
                    isDescending = !isDescending
                    onSortChanged(
                        if (isDescending) mediaSortSearch.desc else mediaSortSearch.asc
                    )
                },
                label = {
                    Text(
                        text = if (isDescending) stringResource(R.string.descending)
                        else stringResource(R.string.ascending)
                    )
                }
            )
        }
    }//: Row
}