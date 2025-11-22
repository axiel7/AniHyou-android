package com.axiel7.anihyou.core.ui.composables.chip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEachIndexed
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> ChipWithMenu(
    title: String,
    values: List<T>,
    selectedValue: T?,
    onValueSelected: (T?) -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    valueString: @Composable (T) -> String = { it.toString() },
    valueIcon: (T) -> Int? = { null },
) {
    val windowHeight = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }
    var menuOpened by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .wrapContentSize(Alignment.TopStart)
    ) {
        FilterChip(
            selected = selectedValue != null,
            onClick = { menuOpened = true },
            label = {
                Text(text = selectedValue?.let { valueString(it) } ?: title)
            },
            trailingIcon = trailingIcon,
        )
        DropdownMenuPopup(
            expanded = menuOpened,
            onDismissRequest = { menuOpened = false },
            modifier = Modifier.requiredSizeIn(maxHeight = windowHeight / 2)
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShapes(),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                values.fastForEachIndexed { index, item ->
                    DropdownMenuItem(
                        checked = selectedValue == item,
                        onCheckedChange = {
                            onValueSelected(item.takeIf { it != selectedValue })
                            menuOpened = false
                        },
                        text = { Text(text = valueString(item)) },
                        shapes = MenuDefaults.itemShape(index, values.size),
                        checkedLeadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.check_24),
                                contentDescription = null
                            )
                        },
                        leadingIcon = {
                            valueIcon(item)?.let { iconRes ->
                                Icon(
                                    painter = painterResource(iconRes),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChipWithMenuPreview() {
    AniHyouTheme {
        ChipWithMenu(
            title = stringResource(R.string.from_year),
            values = listOf("2000", "2001"),
            selectedValue = null,
            onValueSelected = {},
        )
    }
}