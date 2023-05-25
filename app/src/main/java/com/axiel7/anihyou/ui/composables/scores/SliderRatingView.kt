package com.axiel7.anihyou.ui.composables.scores

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun SliderRatingView(
    maxValue: Double,
    modifier: Modifier = Modifier,
    initialRating: Double = 0.0,
    showAsDecimal: Boolean = false,
    onRatingChanged: (Double) -> Unit,
) {
    var rating by remember { mutableStateOf(initialRating) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = if (rating == 0.0) ""
            else if (showAsDecimal) String.format("%.1f", rating)
            else String.format("%.0f", rating),
            onValueChange = { value ->
                value.toDoubleOrNull()?.let {
                    if (it <= maxValue) rating = it
                }
            },
            modifier = Modifier.width(128.dp),
            label = { Text(text = stringResource(R.string.score)) },
            suffix = { Text(text = "/${String.format("%.0f", maxValue)}") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toDouble() },
            valueRange = 0f..maxValue.toFloat(),
            steps = if (maxValue <= 10.0 && !showAsDecimal) maxValue.toInt() else 0,
            onValueChangeFinished = {
                onRatingChanged(rating)
            }
        )
    }
}

@Preview
@Composable
fun SliderRatingViewPreview() {
    AniHyouTheme {
        Surface {
            SliderRatingView(
                maxValue = 100.0,
                showAsDecimal = false,
                onRatingChanged = {}
            )
        }
    }
}