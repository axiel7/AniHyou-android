package com.axiel7.anihyou.core.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.TabRowItem
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ConnectedButtonGroup(
    items: Array<TabRowItem<T>>,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onItemSelection: (Int) -> Unit,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        items.forEachIndexed { index, item ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { onItemSelection(index) },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    items.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
            ) {
                item.icon?.let { icon ->
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = item.title?.let { stringResource(it) }
                            ?: item.toString(),
                    )
                }

                item.title?.let { title ->
                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                    Text(text = stringResource(title))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConnectedButtonGroupPreview() {
    var selectedIndex by remember { mutableIntStateOf(0) }
    AniHyouTheme {
        Surface {
            ConnectedButtonGroup(
                items = arrayOf(
                    TabRowItem("about", icon = R.drawable.info_24),
                    TabRowItem("activity", icon = R.drawable.timeline_24),
                    TabRowItem("stats", icon = R.drawable.bar_chart_24),
                    TabRowItem("favorites", icon = R.drawable.star_24),
                    TabRowItem("social", icon = R.drawable.group_24)
                ),
                selectedIndex = selectedIndex,
                onItemSelection = { selectedIndex = it },
            )
        }
    }
}