package com.axiel7.anihyou.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.axiel7.anihyou.core.model.AppColorMode
import com.axiel7.anihyou.core.resources.ColorUtils.isBlack
import com.axiel7.anihyou.core.resources.ColorUtils.isWhite
import com.axiel7.anihyou.core.resources.seed
import com.materialkolor.dynamicColorScheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AniHyouTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    blackColors: Boolean = false,
    appColor: Color? = null,
    appColorMode: AppColorMode = AppColorMode.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        appColorMode == AppColorMode.PROFILE || appColorMode == AppColorMode.CUSTOM -> {
            val isMonochrome = appColor != null && (appColor.isBlack || appColor.isWhite)
            if (isMonochrome) {
                if (darkTheme) LightAccentColorScheme else BlackAccentColorScheme
            } else {
                val scheme = dynamicColorScheme(
                    seedColor = appColor ?: seed,
                    isDark = darkTheme,
                    isAmoled = false
                )
                if (blackColors) scheme.toBlackScheme()
                else scheme
            }
        }

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context).let {
                return@let if (blackColors) it.toBlackScheme() else it
            }
            else dynamicLightColorScheme(context)
        }

        darkTheme -> if (blackColors) DarkColorScheme.toBlackScheme() else DarkColorScheme
        else -> LightColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        content = content
    )
}