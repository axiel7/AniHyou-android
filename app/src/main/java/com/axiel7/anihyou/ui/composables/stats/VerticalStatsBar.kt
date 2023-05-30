package com.axiel7.anihyou.ui.composables.stats

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.data.model.base.LocalizableAndColorable
import com.axiel7.anihyou.data.model.stats.ScoreDistribution
import com.axiel7.anihyou.data.model.stats.Stat
import com.axiel7.anihyou.data.model.stats.StatColorable
import com.axiel7.anihyou.ui.composables.Rectangle
import com.axiel7.anihyou.ui.composables.RoundedRectangle
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format

const val MAX_VERTICAL_STAT_HEIGHT = 124

@Composable
fun <T: LocalizableAndColorable> VerticalStatsBar(
    stats: List<Stat<T>>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val maxValue = stats.maxOfOrNull { it.value } ?: 0f

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        if (isLoading) {
            for (i in 1..10) {
                Rectangle(
                    width = 25.dp,
                    height = (MAX_VERTICAL_STAT_HEIGHT - (10/i + i*2)).dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.defaultPlaceholder(visible = true)
                )
            }
        }
        else stats.forEach {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = it.value.toInt().format(),
                    modifier = Modifier.padding(bottom = 4.dp),
                    fontSize = 13.sp
                )
                RoundedRectangle(
                    width = 25.dp,
                    height = (it.value / maxValue * MAX_VERTICAL_STAT_HEIGHT).dp,
                    color = it.type.primaryColor(),
                    cornerRadius = CornerRadius(x = 16f, y = 16f)
                )
                Text(
                    text = it.type.localized(),
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }//: Row
}

@Preview
@Composable
fun VerticalStatsBarPreview() {
    val stats by remember {
        mutableStateOf(listOf(
            StatColorable(type = ScoreDistribution(10), value = 12f),
            StatColorable(type = ScoreDistribution(20), value = 10f),
            StatColorable(type = ScoreDistribution(30), value = 5f),
            StatColorable(type = ScoreDistribution(40), value = 3f),
            StatColorable(type = ScoreDistribution(50), value = 30f),
            StatColorable(type = ScoreDistribution(60), value = 40f),
            StatColorable(type = ScoreDistribution(70), value = 60f),
            StatColorable(type = ScoreDistribution(80), value = 90f),
            StatColorable(type = ScoreDistribution(90), value = 120f),
            StatColorable(type = ScoreDistribution(100), value = 230f),
        ))
    }
    AniHyouTheme {
        Surface {
            VerticalStatsBar(
                stats = stats,
                modifier = Modifier.padding(8.dp),
                isLoading = true
            )
        }
    }
}