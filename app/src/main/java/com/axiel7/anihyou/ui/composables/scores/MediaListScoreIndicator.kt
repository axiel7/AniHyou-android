package com.axiel7.anihyou.ui.composables.scores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.point10DecimalOnPrimaryColor
import com.axiel7.anihyou.data.model.point10DecimalPrimaryColor
import com.axiel7.anihyou.data.model.scoreOnPrimaryColor
import com.axiel7.anihyou.data.model.scorePrimaryColor
import com.axiel7.anihyou.data.model.smileyIcon
import com.axiel7.anihyou.data.model.smileyOnPrimaryColor
import com.axiel7.anihyou.data.model.smileyPrimaryColor
import com.axiel7.anihyou.type.ScoreFormat
import com.axiel7.anihyou.utils.NumberUtils.formatPositiveValueOrUnknown
import com.axiel7.anihyou.utils.UNKNOWN_CHAR
import java.util.Locale

@Composable
fun BadgeScoreIndicator(
    modifier: Modifier = Modifier,
    score: Double?,
    scoreFormat: ScoreFormat,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(topEnd = 16.dp, bottomStart = 8.dp))
            .background(score.scorePrimaryColor(format = scoreFormat))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (scoreFormat) {
            ScoreFormat.POINT_100, ScoreFormat.POINT_10, ScoreFormat.POINT_5 -> {
                Icon(
                    painter = painterResource(R.drawable.star_filled_20),
                    contentDescription = "star",
                    modifier = Modifier.size(18.dp),
                    tint = score.scoreOnPrimaryColor(format = scoreFormat)
                )
                Text(
                    text = if (score != null && score != 0.0)
                        String.format(Locale.getDefault(), "%.0f", score)
                    else UNKNOWN_CHAR,
                    color = score.scoreOnPrimaryColor(format = scoreFormat),
                    fontSize = 14.sp
                )
            }

            ScoreFormat.POINT_10_DECIMAL -> {
                Icon(
                    painter = painterResource(R.drawable.star_filled_20),
                    contentDescription = "star",
                    modifier = Modifier.size(18.dp),
                    tint = score?.point10DecimalOnPrimaryColor()
                        ?: MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = score.formatPositiveValueOrUnknown(),
                    color = score?.point10DecimalOnPrimaryColor()
                        ?: MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            }

            ScoreFormat.POINT_3 -> {
                if (score != null && score != 0.0) {
                    Icon(
                        painter = painterResource(score.toInt().smileyIcon(filled = true)),
                        contentDescription = "smiley",
                        modifier = Modifier.size(20.dp),
                        tint = score.toInt().smileyOnPrimaryColor()
                    )
                } else {
                    Text(
                        text = UNKNOWN_CHAR,
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ScoreFormat.UNKNOWN__ -> {}
        }
    }
}

@Composable
fun MinimalScoreIndicator(
    score: Double?,
    scoreFormat: ScoreFormat,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (scoreFormat) {
            ScoreFormat.POINT_100, ScoreFormat.POINT_10, ScoreFormat.POINT_5 -> {
                Icon(
                    painter = painterResource(R.drawable.star_filled_20),
                    contentDescription = "star",
                    modifier = Modifier.size(18.dp),
                    tint = score.scorePrimaryColor(format = scoreFormat)
                )
                Text(
                    text = if (score != null && score != 0.0) String.format(
                        Locale.getDefault(),
                        "%.0f",
                        score
                    ) else UNKNOWN_CHAR,
                    color = score.scorePrimaryColor(format = scoreFormat),
                    fontSize = 14.sp
                )
            }

            ScoreFormat.POINT_10_DECIMAL -> {
                Icon(
                    painter = painterResource(R.drawable.star_filled_20),
                    contentDescription = "star",
                    modifier = Modifier.size(18.dp),
                    tint = score?.point10DecimalPrimaryColor() ?: MaterialTheme.colorScheme.outline
                )
                Text(
                    text = score.formatPositiveValueOrUnknown(),
                    color = score?.point10DecimalPrimaryColor()
                        ?: MaterialTheme.colorScheme.outline,
                    fontSize = 14.sp
                )
            }

            ScoreFormat.POINT_3 -> {
                if (score != null && score != 0.0) {
                    Icon(
                        painter = painterResource(score.toInt().smileyIcon(filled = true)),
                        contentDescription = "smiley",
                        modifier = Modifier.size(18.dp),
                        tint = score.toInt().smileyPrimaryColor()
                    )
                } else {
                    Text(
                        text = UNKNOWN_CHAR,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 14.sp
                    )
                }
            }

            ScoreFormat.UNKNOWN__ -> {}
        }
    }
}