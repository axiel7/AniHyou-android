package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun SmallScoreIndicator(
    score: String,
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
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = score,
            modifier = Modifier.padding(horizontal = 4.dp),
            color = MaterialTheme.colorScheme.outline,
            fontSize = fontSize
        )
    }
}

@Preview
@Composable
fun SmallScoreIndicatorPreview() {
    AniHyouTheme {
        SmallScoreIndicator(score = "75%")
    }
}