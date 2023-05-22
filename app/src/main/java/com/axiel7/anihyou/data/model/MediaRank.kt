package com.axiel7.anihyou.data.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.R
import com.axiel7.anihyou.type.MediaRankType
import com.axiel7.anihyou.ui.theme.stat_dark_red
import com.axiel7.anihyou.ui.theme.stat_dark_yellow
import com.axiel7.anihyou.ui.theme.stat_light_red
import com.axiel7.anihyou.ui.theme.stat_light_yellow

fun MediaRankType.icon() = when (this) {
    MediaRankType.RATED -> R.drawable.star_24
    MediaRankType.POPULAR -> R.drawable.favorite_20
    MediaRankType.UNKNOWN__ -> R.drawable.error_24
}

@Composable
fun MediaRankType.color(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        MediaRankType.RATED -> if (isDark) stat_dark_yellow else stat_light_yellow
        MediaRankType.POPULAR -> if (isDark) stat_dark_red else stat_light_red
        MediaRankType.UNKNOWN__ -> MaterialTheme.colorScheme.onSurface
    }
}