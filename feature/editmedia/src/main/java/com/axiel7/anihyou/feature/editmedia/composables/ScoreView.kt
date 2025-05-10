package com.axiel7.anihyou.feature.editmedia.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.axiel7.anihyou.core.model.maxValue
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.ui.composables.scores.FiveStarRatingView
import com.axiel7.anihyou.core.ui.composables.scores.RatingView
import com.axiel7.anihyou.core.ui.composables.scores.SmileyRatingView

@Composable
fun ScoreView(
    format: ScoreFormat,
    rating: Double?,
    onRatingChanged: (Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    when (format) {
        ScoreFormat.POINT_10,
        ScoreFormat.POINT_10_DECIMAL,
        ScoreFormat.POINT_100 -> {
            RatingView(
                maxValue = format.maxValue(),
                modifier = modifier,
                showIcon = true,
                rating = rating,
                showAsDecimal = format == ScoreFormat.POINT_10_DECIMAL,
                onRatingChanged = onRatingChanged
            )
        }

        ScoreFormat.POINT_5 -> {
            FiveStarRatingView(
                modifier = modifier,
                rating = rating ?: 0.0,
                onRatingChanged = onRatingChanged
            )
        }

        ScoreFormat.POINT_3 -> {
            SmileyRatingView(
                modifier = modifier,
                rating = rating ?: 0.0,
                onRatingChanged = onRatingChanged
            )
        }

        else -> {}
    }
}