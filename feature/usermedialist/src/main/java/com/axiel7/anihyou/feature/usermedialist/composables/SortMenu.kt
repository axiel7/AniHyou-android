package com.axiel7.anihyou.feature.usermedialist.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.model.user.UserMediaListSort
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.resources.R

@Composable
fun SortMenu(
    expanded: Boolean,
    sort: MediaListSort,
    onDismiss: (MediaListSort) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismiss(sort) }
    ) {
        UserMediaListSort.entries.forEach {
            DropdownMenuItem(
                text = { Text(text = it.localized()) },
                onClick = {
                    onDismiss(if (sort == it.desc) it.asc else it.desc)
                },
                modifier = Modifier.padding(end = 8.dp),
                leadingIcon = {
                    if (sort == it.asc) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_upward_24),
                            contentDescription = stringResource(R.string.ascending),
                        )
                    } else if (sort == it.desc) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_downward_24),
                            contentDescription = stringResource(R.string.descending),
                        )
                    }
                }
            )
        }
    }
}