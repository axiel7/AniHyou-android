package com.axiel7.anihyou.ui.screens.explore.search.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.axiel7.anihyou.ui.screens.explore.search.SearchViewModel

@Composable
fun MediaSearchSortChip(
    viewModel: SearchViewModel,
    performSearch: MutableState<Boolean>,
) {
    var openDialog by remember { mutableStateOf(false) }
    var selectedSort by remember {
        mutableStateOf(MediaSortSearch.valueOf(viewModel.mediaSort) ?: MediaSortSearch.SEARCH_MATCH)
    }
    var isDescending by remember { mutableStateOf(true) }

    if (openDialog) {
        DialogWithRadioSelection(
            values = MediaSortSearch.values(),
            defaultValue = selectedSort,
            title = stringResource(R.string.sort),
            isDeselectable = false,
            onConfirm = {
                selectedSort = it!!
                viewModel.onMediaSortChanged(if (isDescending) it.desc else it.asc)
                openDialog = false
                performSearch.value = true
            },
            onDismiss = { openDialog = false }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            onClick = { openDialog = !openDialog },
            label = { Text(text = selectedSort.localized()) },
            modifier = Modifier.padding(8.dp),
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

        if (viewModel.mediaSort != MediaSort.SEARCH_MATCH) {
            AssistChip(
                onClick = {
                    isDescending = !isDescending
                    viewModel.onMediaSortChanged(if (isDescending) selectedSort.desc else selectedSort.asc)
                    performSearch.value = true
                },
                label = {
                    Text(
                        text = if (isDescending) stringResource(R.string.descending)
                        else stringResource(R.string.ascending)
                    )
                },
                modifier = Modifier.padding(8.dp),
            )
        }
    }//: Row
}