package com.axiel7.anihyou.feature.profile.stats.composables

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.model.stats.StatDistributionType
import com.axiel7.anihyou.core.ui.composables.common.FilterSelectionChip

@Composable
fun DistributionTypeChips(
    value: StatDistributionType,
    onValueChanged: (StatDistributionType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        StatDistributionType.entries.forEach {
            FilterSelectionChip(
                selected = value == it,
                text = it.localized(),
                onClick = { onValueChanged(it) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}