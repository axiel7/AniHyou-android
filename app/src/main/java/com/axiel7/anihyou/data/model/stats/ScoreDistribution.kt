package com.axiel7.anihyou.data.model.stats

import androidx.annotation.IntRange
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.data.model.base.LocalizableAndColorable
import com.axiel7.anihyou.ui.theme.stat_dark_10
import com.axiel7.anihyou.ui.theme.stat_dark_100
import com.axiel7.anihyou.ui.theme.stat_dark_20
import com.axiel7.anihyou.ui.theme.stat_dark_30
import com.axiel7.anihyou.ui.theme.stat_dark_40
import com.axiel7.anihyou.ui.theme.stat_dark_50
import com.axiel7.anihyou.ui.theme.stat_dark_60
import com.axiel7.anihyou.ui.theme.stat_dark_70
import com.axiel7.anihyou.ui.theme.stat_dark_80
import com.axiel7.anihyou.ui.theme.stat_dark_90
import com.axiel7.anihyou.ui.theme.stat_light_10
import com.axiel7.anihyou.ui.theme.stat_light_100
import com.axiel7.anihyou.ui.theme.stat_light_20
import com.axiel7.anihyou.ui.theme.stat_light_30
import com.axiel7.anihyou.ui.theme.stat_light_40
import com.axiel7.anihyou.ui.theme.stat_light_50
import com.axiel7.anihyou.ui.theme.stat_light_60
import com.axiel7.anihyou.ui.theme.stat_light_70
import com.axiel7.anihyou.ui.theme.stat_light_80
import com.axiel7.anihyou.ui.theme.stat_light_90
import com.axiel7.anihyou.utils.NumberUtils.format

data class ScoreDistribution(
    @IntRange(0, 100) val score: Int
) : LocalizableAndColorable {
    @Composable
    override fun primaryColor(): Color {
        val isDark = isSystemInDarkTheme()
        return when {
            score < 20 -> if (isDark) stat_dark_10 else stat_light_10
            score < 30 -> if (isDark) stat_dark_20 else stat_light_20
            score < 40 -> if (isDark) stat_dark_30 else stat_light_30
            score < 50 -> if (isDark) stat_dark_40 else stat_light_40
            score < 60 -> if (isDark) stat_dark_50 else stat_light_50
            score < 70 -> if (isDark) stat_dark_60 else stat_light_60
            score < 80 -> if (isDark) stat_dark_70 else stat_light_70
            score < 90 -> if (isDark) stat_dark_80 else stat_light_80
            score < 100 -> if (isDark) stat_dark_90 else stat_light_90
            score == 100 -> if (isDark) stat_dark_100 else stat_light_100
            else -> MaterialTheme.colorScheme.outline
        }
    }

    @Composable
    override fun onPrimaryColor(): Color {
        return primaryColor()
    }

    @Composable
    override fun localized(): String = score.format()

}