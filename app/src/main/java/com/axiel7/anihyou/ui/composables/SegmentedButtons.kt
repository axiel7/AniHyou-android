package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.common.TabRowItem
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun <T> SegmentedButtons(
    items: Array<TabRowItem<T>>,
    modifier: Modifier = Modifier,
    selectedIndex: Int = 0,
    onItemSelection: (Int) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {
        items.forEachIndexed { index, item ->
            SegmentedButton(
                selected = selectedIndex == index,
                onClick = {
                    onItemSelection(index)
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = items.size),
                icon = {
                    if (item.title != null) {
                        item.icon?.let { icon ->
                            Icon(
                                painter = painterResource(icon),
                                contentDescription = item.toString(),
                            )
                        }
                    }
                },
                label = {
                    if (item.title != null) {
                        Text(text = stringResource(item.title))
                    } else if (item.icon != null) {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.toString(),
                        )
                    }
                }
            )
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