package com.axiel7.anihyou.data.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.data.model.base.LocalizableAndColorable
import com.axiel7.anihyou.ui.theme.stat_dark_blue
import com.axiel7.anihyou.ui.theme.stat_dark_green
import com.axiel7.anihyou.ui.theme.stat_dark_onBlue
import com.axiel7.anihyou.ui.theme.stat_dark_onGreen
import com.axiel7.anihyou.ui.theme.stat_dark_onRed
import com.axiel7.anihyou.ui.theme.stat_dark_onYellow
import com.axiel7.anihyou.ui.theme.stat_dark_red
import com.axiel7.anihyou.ui.theme.stat_dark_yellow
import com.axiel7.anihyou.ui.theme.stat_light_blue
import com.axiel7.anihyou.ui.theme.stat_light_green
import com.axiel7.anihyou.ui.theme.stat_light_onBlue
import com.axiel7.anihyou.ui.theme.stat_light_onGreen
import com.axiel7.anihyou.ui.theme.stat_light_onRed
import com.axiel7.anihyou.ui.theme.stat_light_onYellow
import com.axiel7.anihyou.ui.theme.stat_light_red
import com.axiel7.anihyou.ui.theme.stat_light_yellow

data class Stat<T: LocalizableAndColorable>(
    val type: T,
    val value: Float,
)

data class StatColors(
    val primary: Color,
    val onPrimary: Color,
) {
    companion object {
        @Composable
        fun blueScheme() = if (isSystemInDarkTheme())
            StatColors(
                primary = stat_dark_blue,
                onPrimary = stat_dark_onBlue,
            )
        else StatColors(
            primary = stat_light_blue,
            onPrimary = stat_light_onBlue,
        )

        @Composable
        fun greenScheme() = if (isSystemInDarkTheme())
            StatColors(
                primary = stat_dark_green,
                onPrimary = stat_dark_onGreen,
            )
        else StatColors(
            primary = stat_light_green,
            onPrimary = stat_light_onGreen,
        )

        @Composable
        fun redScheme() = if (isSystemInDarkTheme())
            StatColors(
                primary = stat_dark_red,
                onPrimary = stat_dark_onRed,
            )
        else StatColors(
            primary = stat_light_red,
            onPrimary = stat_light_onRed,
        )

        @Composable
        fun yellowScheme() = if (isSystemInDarkTheme())
            StatColors(
                primary = stat_dark_yellow,
                onPrimary = stat_dark_onYellow,
            )
        else StatColors(
            primary = stat_light_yellow,
            onPrimary = stat_light_onYellow,
        )

        @Composable
        fun grayScheme() = StatColors(
                primary = MaterialTheme.colorScheme.outline,
                onPrimary = MaterialTheme.colorScheme.onSurface,
            )
    }
}


