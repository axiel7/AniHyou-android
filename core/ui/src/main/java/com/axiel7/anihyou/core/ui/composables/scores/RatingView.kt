package com.axiel7.anihyou.core.ui.composables.scores

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.common.utils.NumberUtils.toDoubleLocaleInvariant
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RatingView(
    maxValue: Double,
    modifier: Modifier = Modifier,
    label: String = "",
    showIcon: Boolean = false,
    rating: Double? = null,
    showAsDecimal: Boolean = false,
    decimalLength: Int = if (showAsDecimal) 1 else 0,
    increments: Double = if (showAsDecimal) 0.5 else 1.0,
    onRatingChanged: (Double?) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var ratingString by remember(rating) {
        mutableStateOf(rating?.format(decimalLength).orEmpty())
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            Icon(
                painter = painterResource(R.drawable.star_24),
                contentDescription = stringResource(R.string.score),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(24.dp)
            )
        }
        BasicTextField(
            value = ratingString,
            onValueChange = { value ->
                if (value.isEmpty()) {
                    ratingString = value
                    onRatingChanged(null)
                } else if (value.endsWith('.') || value.endsWith(',')) {
                    ratingString = value
                } else {
                    val doubleValue = value.toDoubleLocaleInvariant()
                    if (doubleValue != null) {
                        val doubleValueRoundedString = doubleValue.format(decimalLength)
                        val doubleValueRounded = doubleValueRoundedString?.toDoubleLocaleInvariant()
                        if (doubleValueRounded != null && doubleValueRounded <= maxValue) {
                            ratingString = doubleValueRoundedString
                            onRatingChanged(doubleValueRounded)
                        }
                    }
                }
            },
            modifier = Modifier.width(IntrinsicSize.Min),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textIndent = TextIndent(firstLine = 2.sp)
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (ratingString.isEmpty()) {
                        Text(
                            text = UNKNOWN_CHAR,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            }
        )
        Text(
            text = "/${maxValue.format(decimalLength = 0)} $label",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.weight(1f))
        FilledTonalIconButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                val newValue = (rating ?: 0.0) - increments
                if (newValue >= 0) onRatingChanged(newValue)
            },
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                painter = painterResource(R.drawable.remove_24),
                contentDescription = stringResource(R.string.minus_one)
            )
        }
        FilledTonalIconButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                val newValue = (rating ?: 0.0) + increments
                if (newValue <= maxValue) onRatingChanged(newValue)
            },
            shapes = IconButtonDefaults.shapes()
        ) {
            Icon(
                painter = painterResource(R.drawable.add_24),
                contentDescription = stringResource(R.string.plus_one)
            )
        }
    }
}

@Preview
@Composable
fun SliderRatingViewPreview() {
    AniHyouTheme {
        Surface {
            RatingView(
                maxValue = 100.0,
                label = "Score",
                showIcon = true,
                showAsDecimal = false,
                onRatingChanged = {}
            )
        }
    }
}