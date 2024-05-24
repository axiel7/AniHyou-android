package com.axiel7.anihyou.ui.composables.scores

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.data.model.smileyIcon
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun SmileyRatingView(
    modifier: Modifier = Modifier,
    rating: Double,
    onRatingChanged: (Double) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (rateInt in 1..3) {
            val rate = rateInt.toDouble()
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRatingChanged(
                        if (rating == rate) 0.0 else rate
                    )
                }
            ) {
                Icon(
                    painter = painterResource(rateInt.smileyIcon(filled = rating == rate)),
                    contentDescription = "smile$rateInt",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun SmileyRatingViewPreview() {
    var rating by remember { mutableDoubleStateOf(0.0) }
    AniHyouTheme {
        Surface {
            SmileyRatingView(
                rating = rating,
                onRatingChanged = { rating = it }
            )
        }
    }
}