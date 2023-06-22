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
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.axiel7.anihyou.utils.NumberUtils.formatToDecimal

@Composable
fun SliderRatingView(
    maxValue: Double,
    modifier: Modifier = Modifier,
    initialRating: Double = 0.0,
    showAsDecimal: Boolean = false,
    onRatingChanged: (Double) -> Unit,
) {
    var ratingString by remember { mutableStateOf(initialRating.toString()) }
    var rating by remember { mutableDoubleStateOf(initialRating) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = ratingString,
            onValueChange = { value ->
                ratingString = value.formatToDecimal()
                ratingString.toDoubleOrNull().let {
                    if (it == null) rating = 0.0
                    else if (it <= maxValue) rating = it
                    onRatingChanged(rating)
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