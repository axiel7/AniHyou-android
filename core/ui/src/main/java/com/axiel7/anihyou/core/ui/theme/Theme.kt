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
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme
import com.materialkolor.dynamiccolor.ColorSpec

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
            dynamicColorScheme(
                seedColor = appColor ?: seed,
                isDark = darkTheme,
                isAmoled = blackColors,
                style = if (isMonochrome) PaletteStyle.Monochrome else PaletteStyle.Expressive,
                specVersion = ColorSpec.SpecVersion.SPEC_2025,
            )
        }

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val colors = if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

            dynamicColorScheme(
                primary = colors.primary,
                isDark = darkTheme,
                isAmoled = blackColors,
                style = PaletteStyle.Expressive,
                specVersion = ColorSpec.SpecVersion.SPEC_2025,
            )
        }

        else -> dynamicColorScheme(
            seedColor = seed,
            isDark = darkTheme,
            isAmoled = blackColors,
            style = PaletteStyle.Expressive,
            specVersion = ColorSpec.SpecVersion.SPEC_2025
        )
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        content = content
    )
}