package com.axiel7.anihyou.core.ui.composables.scores

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
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import com.axiel7.anihyou.core.common.utils.NumberUtils.formatPositiveValueOrUnknown
import com.axiel7.anihyou.core.model.maxValue
import com.axiel7.anihyou.core.model.point10DecimalOnPrimaryColor
import com.axiel7.anihyou.core.model.point10DecimalPrimaryColor
import com.axiel7.anihyou.core.model.scoreOnPrimaryColor
import com.axiel7.anihyou.core.model.scorePrimaryColor
import com.axiel7.anihyou.core.model.smileyIcon
import com.axiel7.anihyou.core.model.smileyOnPrimaryColor
import com.axiel7.anihyou.core.model.smileyPrimaryColor
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.resources.R

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
            .padding(
                start = 4.dp,
                end = 8.dp,
                top = 4.dp,
                bottom = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (scoreFormat) {
            ScoreFormat.POINT_100, ScoreFormat.POINT_10, ScoreFormat.POINT_5 -> {
                Icon(
                    painter = painterResource(R.drawable.star_filled_20),
                    contentDescription = "star",
                    modifier = Modifier
                        .padding(bottom = 1.dp)
                        .size(18.dp),
                    tint = score.scoreOnPrimaryColor(format = scoreFormat)
                )
                Text(
                    text = if (score != null && score != 0.0)
                        String.format(LocalLocale.current.platformLocale, "%.0f", score)
                    else UNKNOWN_CHAR,
                    color = score.scoreOnPrimaryColor(format = scoreFormat),
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            ScoreFormat.POINT_10_DECIMAL -> {
                Icon(
                    painter = painterResource(R.drawable.star_filled_20),
                    contentDescription = "star",
                    modifier = Modifier
                        .padding(bottom = 1.dp)
                        .size(18.dp),
                    tint = score?.point10DecimalOnPrimaryColor()
                        ?: MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = score.formatPositiveValueOrUnknown(),
                    color = score?.point10DecimalOnPrimaryColor()
                        ?: MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium,
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
                        style = MaterialTheme.typography.labelMedium,
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
    showTotal: Boolean = false,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (scoreFormat) {
            ScoreFormat.POINT_100, ScoreFormat.POINT_10, ScoreFormat.POINT_5 -> {
                val color = score.scorePrimaryColor(format = scoreFormat)
                Icon(
                    painter = painterResource(R.drawable.star_filled_20),
                    contentDescription = "star",
                    modifier = Modifier.size(18.dp),
                    tint = color,
                )
                Text(
                    text = if (score != null && score != 0.0) String.format(
                        LocalLocale.current.platformLocale,
                        "%.0f",
                        score
                    ) else UNKNOWN_CHAR,
                    color = color,
                    fontSize = 14.sp
                )
                if (showTotal) {
                    Text(
                        text = "/${scoreFormat.maxValue().toInt()}",
                        color = color,
                        fontSize = 14.sp
                    )
                }
            }

            ScoreFormat.POINT_10_DECIMAL -> {
                val color = score?.point10DecimalPrimaryColor() ?: MaterialTheme.colorScheme.outline
                Icon(
                    painter = painterResource(R.drawable.star_filled_20),
                    contentDescription = "star",
                    modifier = Modifier.size(18.dp),
                    tint = color,
                )
                Text(
                    text = score.formatPositiveValueOrUnknown(),
                    color = color,
                    fontSize = 14.sp
                )
                if (showTotal) {
                    Text(
                        text = "/10",
                        color = color,
                        fontSize = 14.sp
                    )
                }
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