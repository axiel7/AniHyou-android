package com.axiel7.anihyou.feature.usermedialist.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.axiel7.anihyou.core.model.user.UserMediaListSort
import com.axiel7.anihyou.core.network.type.MediaListSort
import com.axiel7.anihyou.core.resources.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SortMenu(
    expanded: Boolean,
    sort: MediaListSort,
    onDismiss: (MediaListSort) -> Unit,
) {
    DropdownMenuPopup(
        expanded = expanded,
        onDismissRequest = { onDismiss(sort) }
    ) {
        DropdownMenuGroup(
            shapes = MenuDefaults.groupShapes()
        ) {
            UserMediaListSort.entries.fastForEachIndexed { index, item ->
                DropdownMenuItem(
                    checked = sort == item.asc || sort == item.desc,
                    onCheckedChange = {
                        onDismiss(if (sort == item.desc) item.asc else item.desc)
                    },
                    text = { Text(text = item.localized()) },
                    shapes = MenuDefaults.itemShape(index, UserMediaListSort.entries.size),
                    modifier = Modifier.padding(end = 8.dp),
                    leadingIcon = {
                        if (sort == item.asc) {
                            Icon(
                                painter = painterResource(R.drawable.arrow_upward_24),
                                contentDescription = stringResource(R.string.ascending),
                            )
                        } else if (sort == item.desc) {
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
}