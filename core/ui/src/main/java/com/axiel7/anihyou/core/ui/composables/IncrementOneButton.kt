package com.axiel7.anihyou.core.ui.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IncrementOneButton(
    onClickPlus: (Int) -> Unit,
    blockPlus: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val scope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState(isPersistent = true)

    val delaySeconds = 2f
    var clickCount by remember { mutableIntStateOf(0) }

    var remainingTime by remember { mutableFloatStateOf(delaySeconds) }
    var isCountingDown by remember { mutableStateOf(false) }

    val transition = updateTransition(remainingTime)
    val containerColor by transition.animateColor {
        if (isCountingDown) {
            if (it == 0f) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
        } else MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor by transition.animateColor {
        if (isCountingDown) {
            if (it == 0f) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onTertiaryContainer
        } else MaterialTheme.colorScheme.onPrimaryContainer
    }

    LaunchedEffect(isCountingDown, remainingTime) {
        if (isCountingDown && remainingTime > 0) {
            delay(400)
            remainingTime -= 1f
        } else if (remainingTime <= 0 && isCountingDown) {
            isCountingDown = false
            remainingTime = delaySeconds
            onClickPlus(clickCount)
            clickCount = 0
            tooltipState.dismiss()
        }
    }
    val number = if (clickCount == 0) 1 else clickCount

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            positioning = TooltipAnchorPosition.Above
        ),
        tooltip = {
            PlainTooltip {
                Text(
                    text = "+$number",
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        },
        focusable = false,
        state = tooltipState,
        modifier = modifier,
        onDismissRequest = {}
    ) {
        FilledTonalButton(
            onClick = {
                clickCount++
                remainingTime = delaySeconds
                if (!isCountingDown) {
                    isCountingDown = true
                    scope.launch { tooltipState.show() }
                    blockPlus()
                }
            },
            enabled = enabled || isCountingDown,
            shapes = ButtonDefaults.shapes(),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            )
        ) {
            Text(text = stringResource(R.string.plus_one))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IncrementOneButtonPreview() {
    AniHyouTheme {
        IncrementOneButton(
            onClickPlus = {},
            blockPlus = {},
        )
    }
}