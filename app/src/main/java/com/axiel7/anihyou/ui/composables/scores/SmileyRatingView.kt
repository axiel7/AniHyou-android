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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun SmileyRatingView(
    modifier: Modifier = Modifier,
    initialRating: Double = 0.0,
    onRatingChanged: (Double) -> Unit,
) {
    var rating by remember { mutableDoubleStateOf(initialRating) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                rating = if (rating == 1.0) 0.0 else 1.0
                onRatingChanged(rating)
            }
        ) {
            Icon(
                painter = painterResource(
                    if (rating == 1.0) R.drawable.mood_bad_filled_24
                    else R.drawable.mood_bad_24
                ),
                contentDescription = "smile1",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = {
                rating = if (rating == 2.0) 0.0 else 2.0
                onRatingChanged(rating)
            }
        ) {
            Icon(
                painter = painterResource(
                    if (rating == 2.0) R.drawable.sentiment_satisfied_filled_24
                    else R.drawable.sentiment_satisfied_24
                ),
                contentDescription = "smile2",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(
            onClick = {
                rating = if (rating == 3.0) 0.0 else 3.0
                onRatingChanged(rating)
            }
        ) {
            Icon(
                painter = painterResource(
                    if (rating == 3.0) R.drawable.sentiment_very_satisfied_filled_24
                    else R.drawable.sentiment_very_satisfied_24
                ),
                contentDescription = "smile3",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun SmileyRatingViewPreview() {
    AniHyouTheme {
        Surface {
            SmileyRatingView(
                onRatingChanged = {}
            )
        }
    }
}