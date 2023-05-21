package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.theme.AniHyouTheme

// TODO: replace with official material3 component when google decides to release it
// https://issuetracker.google.com/issues/244185536
@Composable
fun <T> SegmentedButtons(
    items: Array<TabRowItem<T>>,
    modifier: Modifier = Modifier,
    defaultSelectedIndex: Int = 0,
    onItemSelection: (Int) -> Unit,
) {
    var selectedIndex by remember { mutableStateOf(defaultSelectedIndex) }

    Row(
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            OutlinedButton(
                modifier = Modifier.weight(1f).then(
                    if (index != 0) Modifier.offset((-1 * index).dp, 0.dp)
                    else Modifier
                ),
                onClick = {
                    selectedIndex = index
                    onItemSelection(selectedIndex)
                },
                shape = when (index) {
                    // leading button
                    0 -> RoundedCornerShape(
                        topStart = 100f,
                        bottomStart = 100f,
                    )
                    // trailing button
                    items.size - 1 -> RoundedCornerShape(
                        topEnd = 100f,
                        bottomEnd = 100f,
                    )
                    // middle button
                    else -> RoundedCornerShape(0f)
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.secondaryContainer
                    else Color.Transparent
                ),
            ) {
                item.icon?.let { icon ->
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "",
                        tint = if (selectedIndex == index)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
                item.title?.let { title ->
                    Text(
                        text = stringResource(title),
                        color = if (selectedIndex == index)
                            MaterialTheme.colorScheme.onSecondaryContainer
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SegmentedButtonsPreview() {
    AniHyouTheme {
        Surface {
            SegmentedButtons(
                items = arrayOf(
                    TabRowItem("about", icon = R.drawable.info_24),
                    TabRowItem("activity", icon = R.drawable.timeline_24),
                    TabRowItem("stats", icon = R.drawable.bar_chart_24),
                    TabRowItem("favorites", icon = R.drawable.star_24),
                    TabRowItem("social", icon = R.drawable.group_24)
                ),
                onItemSelection = {

                }
            )
        }
    }
}