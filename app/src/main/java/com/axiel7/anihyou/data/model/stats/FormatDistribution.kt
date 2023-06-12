package com.axiel7.anihyou.data.model.stats

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.data.model.base.LocalizableAndColorable
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.type.MediaFormat
import com.axiel7.anihyou.ui.theme.stat_dark_10
import com.axiel7.anihyou.ui.theme.stat_dark_20
import com.axiel7.anihyou.ui.theme.stat_dark_40
import com.axiel7.anihyou.ui.theme.stat_dark_50
import com.axiel7.anihyou.ui.theme.stat_dark_60
import com.axiel7.anihyou.ui.theme.stat_dark_70
import com.axiel7.anihyou.ui.theme.stat_dark_80
import com.axiel7.anihyou.ui.theme.stat_dark_90
import com.axiel7.anihyou.ui.theme.stat_dark_blue
import com.axiel7.anihyou.ui.theme.stat_dark_on10
import com.axiel7.anihyou.ui.theme.stat_dark_on20
import com.axiel7.anihyou.ui.theme.stat_dark_on40
import com.axiel7.anihyou.ui.theme.stat_dark_on50
import com.axiel7.anihyou.ui.theme.stat_dark_on60
import com.axiel7.anihyou.ui.theme.stat_dark_on70
import com.axiel7.anihyou.ui.theme.stat_dark_on80
import com.axiel7.anihyou.ui.theme.stat_dark_on90
import com.axiel7.anihyou.ui.theme.stat_dark_onBlue
import com.axiel7.anihyou.ui.theme.stat_light_10
import com.axiel7.anihyou.ui.theme.stat_light_20
import com.axiel7.anihyou.ui.theme.stat_light_40
import com.axiel7.anihyou.ui.theme.stat_light_50
import com.axiel7.anihyou.ui.theme.stat_light_60
import com.axiel7.anihyou.ui.theme.stat_light_70
import com.axiel7.anihyou.ui.theme.stat_light_80
import com.axiel7.anihyou.ui.theme.stat_light_90
import com.axiel7.anihyou.ui.theme.stat_light_blue
import com.axiel7.anihyou.ui.theme.stat_light_on10
import com.axiel7.anihyou.ui.theme.stat_light_on20
import com.axiel7.anihyou.ui.theme.stat_light_on40
import com.axiel7.anihyou.ui.theme.stat_light_on50
import com.axiel7.anihyou.ui.theme.stat_light_on60
import com.axiel7.anihyou.ui.theme.stat_light_on70
import com.axiel7.anihyou.ui.theme.stat_light_on80
import com.axiel7.anihyou.ui.theme.stat_light_on90
import com.axiel7.anihyou.ui.theme.stat_light_onBlue

enum class FormatDistribution(
    val value: MediaFormat
) : LocalizableAndColorable {
    TV(MediaFormat.TV),
    TV_SHORT(MediaFormat.TV_SHORT),
    MOVIE(MediaFormat.MOVIE),
    SPECIAL(MediaFormat.SPECIAL),
    OVA(MediaFormat.OVA),
    ONA(MediaFormat.ONA),
    MUSIC(MediaFormat.MUSIC),
    MANGA(MediaFormat.MANGA),
    NOVEL(MediaFormat.NOVEL),
    ONE_SHOT(MediaFormat.ONE_SHOT);

    @Composable override fun primaryColor(): Color {
        val isDark = isSystemInDarkTheme()
        return when (value) {
            MediaFormat.TV -> if (isDark) stat_dark_blue else stat_light_blue
            MediaFormat.TV_SHORT -> if (isDark) stat_dark_10 else stat_light_10
            MediaFormat.MOVIE -> if (isDark) stat_dark_80 else stat_light_80
            MediaFormat.SPECIAL -> if (isDark) stat_dark_50 else stat_light_50
            MediaFormat.OVA -> if (isDark) stat_dark_40 else stat_light_40
            MediaFormat.ONA -> if (isDark) stat_dark_20 else stat_light_20
            MediaFormat.MUSIC -> if (isDark) stat_dark_60 else stat_light_60
            MediaFormat.MANGA -> if (isDark) stat_dark_blue else stat_light_blue
            MediaFormat.NOVEL -> if (isDark) stat_dark_90 else stat_light_90
            MediaFormat.ONE_SHOT -> if (isDark) stat_dark_70 else stat_light_70
            MediaFormat.UNKNOWN__ -> MaterialTheme.colorScheme.outline
        }
    }
    @Composable override fun onPrimaryColor(): Color {
        val isDark = isSystemInDarkTheme()
        return when (value) {
            MediaFormat.TV -> if (isDark) stat_dark_onBlue else stat_light_onBlue
            MediaFormat.TV_SHORT -> if (isDark) stat_dark_on10 else stat_light_on10
            MediaFormat.MOVIE -> if (isDark) stat_dark_on80 else stat_light_on80
            MediaFormat.SPECIAL -> if (isDark) stat_dark_on50 else stat_light_on50
            MediaFormat.OVA -> if (isDark) stat_dark_on40 else stat_light_on40
            MediaFormat.ONA -> if (isDark) stat_dark_on20 else stat_light_on20
            MediaFormat.MUSIC -> if (isDark) stat_dark_on60 else stat_light_on60
            MediaFormat.MANGA -> if (isDark) stat_dark_onBlue else stat_light_onBlue
            MediaFormat.NOVEL -> if (isDark) stat_dark_on90 else stat_light_on90
            MediaFormat.ONE_SHOT -> if (isDark) stat_dark_on70 else stat_light_on70
            MediaFormat.UNKNOWN__ -> MaterialTheme.colorScheme.onSurface
        }
    }

    @Composable override fun localized() = value.localized()

    companion object {
        fun valueOf(rawValue: String?) = FormatDistribution.values().find { it.value.rawValue == rawValue }
    }
}
