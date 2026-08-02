package com.axiel7.anihyou.feature.explore.search.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
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
import com.axiel7.anihyou.core.model.media.MediaSortSearch
import com.axiel7.anihyou.core.network.type.MediaSort
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.chip.AssistChipWithMenu

@Composable
fun MediaSearchSortChip(
    mediaSortSearch: MediaSortSearch,
    onSortChanged: (MediaSort) -> Unit,
) {
    var isDescending by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChipWithMenu(
            values = MediaSortSearch.entries,
            selectedValue = mediaSortSearch,
            onValueSelected = {
                onSortChanged((if (isDescending) it.desc else it.asc))
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.sort_20),
                    contentDescription = stringResource(R.string.sort),
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            },
            valueString = { it.localized() },
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