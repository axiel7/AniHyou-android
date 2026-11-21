package com.axiel7.anihyou.core.ui.composables.common

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.dateToAbsoluteText
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.dateToRelativeText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimestampText(
    timestamp: Long,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    style: TextStyle = LocalTextStyle.current,
    fontSize: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val scope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState(isPersistent = true)
    val absoluteText = remember(timestamp) { timestamp.dateToAbsoluteText() }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above
        ),
        tooltip = {
            PlainTooltip {
                Text(text = absoluteText)
            }
        },
        focusable = false,
        state = tooltipState,
    ) {
        Text(
            text = timestamp.dateToRelativeText(),
            modifier = modifier
                .clickable {
                    scope.launch { tooltipState.show() }
                },
            color = color,
            style = style,
            fontSize = fontSize,
            maxLines = maxLines,
            overflow = overflow,
        )
    }
}
