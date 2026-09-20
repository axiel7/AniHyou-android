package com.axiel7.anihyou.core.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PercentageSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String
) {
    val sliderState = rememberSliderState(
        value = value.toFloat(),
        trackRange = 0f..100f
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Slider(
                state = sliderState,
                modifier = Modifier.weight(1f),
                onValueChangeFinished = {
                    onValueChange(sliderState.value.toInt().coerceIn(0, 100))
                },
            )
            Text(
                text = "${value}%",
                textAlign = TextAlign.End,
                modifier = Modifier
                    .widthIn(min = 48.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}
