package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

@Composable
fun RoundedTabRowIndicator(currentTabPosition: TabPosition) {
    TabRowDefaults.SecondaryIndicator(
        modifier = Modifier
            .tabIndicatorOffset(currentTabPosition)
            .clip(RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}