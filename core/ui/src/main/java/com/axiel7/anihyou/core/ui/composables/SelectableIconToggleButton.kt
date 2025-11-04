package com.axiel7.anihyou.core.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> SelectableIconToggleButton(
    @DrawableRes icon: Int,
    tooltipText: String,
    value: T,
    selectedValue: T,
    onClick: (Boolean) -> Unit
) {
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Above
        ),
        tooltip = {
            PlainTooltip {
                Text(tooltipText)
            }
        },
        focusable = false,
        state = tooltipState,
    ) {
        FilledIconToggleButton(
            checked = value == selectedValue,
            onCheckedChange = {
                scope.launch { tooltipState.show() }
                onClick(it)
            },
            shapes = IconButtonDefaults.toggleableShapes(),
        ) {
            Icon(painter = painterResource(icon), contentDescription = tooltipText)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SelectableIconToggleButtonPreview() {
    AniHyouTheme {
        SelectableIconToggleButton(
            icon = R.drawable.play_circle_24,
            tooltipText = "SelectableIconToggleButton",
            value = "",
            selectedValue = "0",
            onClick = {},
        )
    }
}