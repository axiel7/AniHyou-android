package com.axiel7.anihyou.core.ui.composables.chip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberRangeSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.sheet.ModalBottomSheet
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipWithRange(
    title: String,
    startValue: Float?,
    endValue: Float?,
    modifier: Modifier = Modifier,
    minValue: Float = 0f,
    maxValue: Float,
    onValueChanged: (IntRange?) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val hasValue = startValue != null || endValue != null

    var rangeStart by remember { mutableIntStateOf((startValue ?: minValue).roundToInt()) }
    var rangeEnd by remember { mutableIntStateOf((endValue ?: maxValue).roundToInt()) }

    var sheetOpened by remember { mutableStateOf(false) }

    val rangeSliderState = rememberRangeSliderState(
        activeRangeStart = startValue ?: minValue,
        activeRangeEnd = endValue ?: maxValue,
        valueRange = minValue..maxValue,
        onValueChangeFinished = {
            onValueChanged(rangeStart..rangeEnd)
        }
    )

    LaunchedEffect(rangeSliderState.activeRangeStart, rangeSliderState.activeRangeEnd) {
        rangeStart = rangeSliderState.activeRangeStart.roundToInt()
        rangeEnd = rangeSliderState.activeRangeEnd.roundToInt()
    }

    FilterChip(
        selected = hasValue,
        onClick = { sheetOpened = true },
        label = {
            val label = if (hasValue) "$rangeStart - $rangeEnd" else title

            Text(text = label)
        },
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (hasValue) {
                Icon(
                    painter = painterResource(R.drawable.close_20),
                    contentDescription = stringResource(R.string.clear),
                    modifier = Modifier
                        .size(FilterChipDefaults.IconSize)
                        .clickable {
                            onValueChanged(null)
                            rangeSliderState.activeRangeStart = minValue
                            rangeSliderState.activeRangeEnd = maxValue
                        },
                )
            }
        },
    )

    if (sheetOpened) {
        ModalBottomSheet(
            onDismissed = { sheetOpened = false }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = title)

                Text(text = "$rangeStart - $rangeEnd")

                RangeSlider(
                    state = rangeSliderState,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun ChipWithRangePreview() {
    AniHyouTheme {
        ChipWithRange(
            title = "Episodes",
            startValue = null,
            endValue = null,
            maxValue = 150f,
            onValueChanged = {}
        )
    }
}


