package com.axiel7.anihyou.ui.composables.stats

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.data.model.stats.Stat
import com.axiel7.anihyou.data.model.stats.StatLocalizableAndColorable
import com.axiel7.anihyou.data.model.stats.StatusDistribution
import com.axiel7.anihyou.ui.composables.Rectangle
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.NumberUtils.format

@Composable
fun <T> HorizontalStatsBar(
    stats: List<Stat<T>>,
    horizontalPadding: Dp = 8.dp,
    verticalPadding: Dp = 0.dp,
    showTotal: Boolean = true,
    isLoading: Boolean = false,
) where T : Localizable, T : Colorable {
    val totalValue = stats.map { it.value }.sum()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp

    Column(modifier = Modifier.padding(vertical = verticalPadding)) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                for (i in 1..5) {
                    Text(
                        text = "Loading",
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .defaultPlaceholder(visible = true)
                    )
                }
            } else stats.forEach {
                AssistChip(
                    onClick = { },
                    label = { Text(text = it.type.localized()) },
                    leadingIcon = { Text(text = it.value.toInt().format()) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = it.type.primaryColor(),
                        labelColor = it.type.onPrimaryColor(),
                        leadingIconContentColor = it.type.onPrimaryColor()
                    ),
                    border = null
                )
            }
        }

        Row {
            stats.forEach {
                Rectangle(
                    width = (it.value / totalValue * screenWidth).dp,
                    height = 16.dp,
                    color = it.type.primaryColor()
                )
            }
        }

        if (showTotal) {
            Text(
                text = stringResource(R.string.total_entries).format(totalValue.toInt().format()),
                modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun HorizontalStatsBarPreview() {
    val stats by remember {
        mutableStateOf(
            listOf(
                StatLocalizableAndColorable(type = StatusDistribution.CURRENT, value = 12f),
                StatLocalizableAndColorable(type = StatusDistribution.COMPLETED, value = 420f),
                StatLocalizableAndColorable(type = StatusDistribution.PAUSED, value = 5f),
                StatLocalizableAndColorable(type = StatusDistribution.DROPPED, value = 3f),
                StatLocalizableAndColorable(type = StatusDistribution.PLANNING, value = 30f),
            )
        )
    }
    AniHyouTheme {
        Surface {
            HorizontalStatsBar(
                stats = stats
            )
        }
    }
}