package com.axiel7.anihyou.core.ui.composables.stats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.model.base.Colorable
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.model.stats.Stat
import com.axiel7.anihyou.core.model.stats.StatColorable
import com.axiel7.anihyou.core.model.stats.overview.ScoreDistribution
import com.axiel7.anihyou.core.ui.composables.Rectangle
import com.axiel7.anihyou.core.ui.composables.RoundedRectangle
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

const val MAX_VERTICAL_STAT_HEIGHT = 124

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> VerticalStatsBar(
    stats: List<Stat<T>>,
    modifier: Modifier = Modifier,
    mapColorTo: @Composable (T) -> Color = { it.primaryColor() },
    isLoading: Boolean = false,
) where T : Localizable, T : Colorable {
    val maxValue = remember(stats) { stats.maxOfOrNull { it.value } ?: 0f }

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .then(modifier),
        verticalAlignment = Alignment.Bottom,
    ) {
        if (isLoading) {
            for (i in 1..10) {
                Rectangle(
                    width = 25.dp,
                    height = (MAX_VERTICAL_STAT_HEIGHT - (10 / i + i * 2)).dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .defaultPlaceholder(visible = true)
                )
            }
        } else stats.forEach { stat ->
            val scope = rememberCoroutineScope()
            val tooltipState = rememberTooltipState(isPersistent = true)
            TooltipBox(
                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                tooltip = {
                    RichTooltip {
                        Column {
                            stat.details?.forEach {
                                Text(text = it.text())
                            }
                        }
                    }
                },
                state = tooltipState,
                modifier = modifier,
                enableUserInput = stat.details != null
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stat.value.toInt().format().orEmpty(),
                        modifier = Modifier.padding(bottom = 4.dp),
                        fontSize = 13.sp
                    )
                    RoundedRectangle(
                        width = 25.dp,
                        height = (stat.value / maxValue * MAX_VERTICAL_STAT_HEIGHT).dp,
                        color = mapColorTo(stat.type),
                        cornerRadius = CornerRadius(x = 16f, y = 16f),
                        modifier = Modifier.clickable {
                            if (stat.details != null) scope.launch { tooltipState.show() }
                        }
                    )
                    Text(
                        text = stat.type.localized(),
                        modifier = Modifier.padding(top = 4.dp),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }//:Column
            }//:TooltipBox
        }
    }//: Row
}

@Preview
@Composable
fun VerticalStatsBarPreview() {
    val stats by remember {
        mutableStateOf(
            listOf(
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
            )
        )
    }
    AniHyouTheme {
        Surface {
            VerticalStatsBar(
                stats = stats,
                modifier = Modifier.padding(8.dp),
                isLoading = false
            )
        }
    }
}