package com.axiel7.anihyou.core.model.media

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.resources.md_theme_dark_outline
import com.axiel7.anihyou.core.resources.md_theme_light_outline
import com.axiel7.anihyou.core.resources.stat_dark_10
import com.axiel7.anihyou.core.resources.stat_dark_30
import com.axiel7.anihyou.core.resources.stat_dark_50
import com.axiel7.anihyou.core.resources.stat_light_10
import com.axiel7.anihyou.core.resources.stat_light_30
import com.axiel7.anihyou.core.resources.stat_light_50

// don't know if this is still used with the new approach
@Composable
fun Int.priorityColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        0 -> if (isDark) stat_dark_10 else stat_light_10
        1 -> if (isDark) stat_dark_30 else stat_light_30
        2 -> if (isDark) stat_dark_50 else stat_light_50
        else -> if (isDark) md_theme_dark_outline else md_theme_light_outline
    }
}

@Composable
fun Int.priorityIcon(): Int = when (this) {
    0 -> R.drawable.counter_0_24
    1 -> R.drawable.counter_1_24
    2 -> R.drawable.counter_2_24
    else -> R.drawable.cancel_24 // invalid priority was set
}
