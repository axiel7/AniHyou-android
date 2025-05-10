package com.axiel7.anihyou.core.ui.composables.scores

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun SmallScoreIndicator(
    score: Int,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.star_filled_20),
            contentDescription = stringResource(R.string.mean_score),
            modifier = Modifier.padding(bottom = 2.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "${score.format()}%",
            modifier = Modifier.padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.outline,
            fontSize = fontSize,
            lineHeight = fontSize,
        )
    }
}

@Preview
@Composable
fun SmallScoreIndicatorPreview() {
    AniHyouTheme {
        Surface {
            SmallScoreIndicator(score = 75)
        }
    }
}