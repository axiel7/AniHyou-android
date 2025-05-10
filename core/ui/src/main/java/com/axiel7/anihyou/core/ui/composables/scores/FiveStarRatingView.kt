package com.axiel7.anihyou.core.ui.composables.scores

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun FiveStarRatingView(
    modifier: Modifier = Modifier,
    rating: Double = 0.0,
    onRatingChanged: (Double) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..5) {
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRatingChanged(
                        if (rating.toInt() == i) 0.0 else i.toDouble()
                    )
                }
            ) {
                Icon(
                    painter = painterResource(
                        if (rating >= i) R.drawable.star_filled_24
                        else R.drawable.star_24
                    ),
                    contentDescription = "star$i",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview
@Composable
fun FiveStarRatingViewPreview() {
    AniHyouTheme {
        Surface {
            FiveStarRatingView(
                onRatingChanged = {}
            )
        }
    }
}