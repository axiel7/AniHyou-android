package com.axiel7.anihyou.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import com.axiel7.anihyou.core.resources.md_theme_dark_background
import com.axiel7.anihyou.core.resources.md_theme_dark_error
import com.axiel7.anihyou.core.resources.md_theme_dark_errorContainer
import com.axiel7.anihyou.core.resources.md_theme_dark_inverseOnSurface
import com.axiel7.anihyou.core.resources.md_theme_dark_inversePrimary
import com.axiel7.anihyou.core.resources.md_theme_dark_inverseSurface
import com.axiel7.anihyou.core.resources.md_theme_dark_onBackground
import com.axiel7.anihyou.core.resources.md_theme_dark_onError
import com.axiel7.anihyou.core.resources.md_theme_dark_onErrorContainer
import com.axiel7.anihyou.core.resources.md_theme_dark_onPrimary
import com.axiel7.anihyou.core.resources.md_theme_dark_onPrimaryContainer
import com.axiel7.anihyou.core.resources.md_theme_dark_onSecondary
import com.axiel7.anihyou.core.resources.md_theme_dark_onSecondaryContainer
import com.axiel7.anihyou.core.resources.md_theme_dark_onSurface
import com.axiel7.anihyou.core.resources.md_theme_dark_onSurfaceVariant
import com.axiel7.anihyou.core.resources.md_theme_dark_onTertiary
import com.axiel7.anihyou.core.resources.md_theme_dark_onTertiaryContainer
import com.axiel7.anihyou.core.resources.md_theme_dark_outline
import com.axiel7.anihyou.core.resources.md_theme_dark_outlineVariant
import com.axiel7.anihyou.core.resources.md_theme_dark_primary
import com.axiel7.anihyou.core.resources.md_theme_dark_primaryContainer
import com.axiel7.anihyou.core.resources.md_theme_dark_scrim
import com.axiel7.anihyou.core.resources.md_theme_dark_secondary
import com.axiel7.anihyou.core.resources.md_theme_dark_secondaryContainer
import com.axiel7.anihyou.core.resources.md_theme_dark_surface
import com.axiel7.anihyou.core.resources.md_theme_dark_surfaceTint
import com.axiel7.anihyou.core.resources.md_theme_dark_surfaceVariant
import com.axiel7.anihyou.core.resources.md_theme_dark_tertiary
import com.axiel7.anihyou.core.resources.md_theme_dark_tertiaryContainer
import com.axiel7.anihyou.core.resources.md_theme_light_background
import com.axiel7.anihyou.core.resources.md_theme_light_error
import com.axiel7.anihyou.core.resources.md_theme_light_errorContainer
import com.axiel7.anihyou.core.resources.md_theme_light_inverseOnSurface
import com.axiel7.anihyou.core.resources.md_theme_light_inversePrimary
import com.axiel7.anihyou.core.resources.md_theme_light_inverseSurface
import com.axiel7.anihyou.core.resources.md_theme_light_onBackground
import com.axiel7.anihyou.core.resources.md_theme_light_onError
import com.axiel7.anihyou.core.resources.md_theme_light_onErrorContainer
import com.axiel7.anihyou.core.resources.md_theme_light_onPrimary
import com.axiel7.anihyou.core.resources.md_theme_light_onPrimaryContainer
import com.axiel7.anihyou.core.resources.md_theme_light_onSecondary
import com.axiel7.anihyou.core.resources.md_theme_light_onSecondaryContainer
import com.axiel7.anihyou.core.resources.md_theme_light_onSurface
import com.axiel7.anihyou.core.resources.md_theme_light_onSurfaceVariant
import com.axiel7.anihyou.core.resources.md_theme_light_onTertiary
import com.axiel7.anihyou.core.resources.md_theme_light_onTertiaryContainer
import com.axiel7.anihyou.core.resources.md_theme_light_outline
import com.axiel7.anihyou.core.resources.md_theme_light_outlineVariant
import com.axiel7.anihyou.core.resources.md_theme_light_primary
import com.axiel7.anihyou.core.resources.md_theme_light_primaryContainer
import com.axiel7.anihyou.core.resources.md_theme_light_scrim
import com.axiel7.anihyou.core.resources.md_theme_light_secondary
import com.axiel7.anihyou.core.resources.md_theme_light_secondaryContainer
import com.axiel7.anihyou.core.resources.md_theme_light_surface
import com.axiel7.anihyou.core.resources.md_theme_light_surfaceTint
import com.axiel7.anihyou.core.resources.md_theme_light_surfaceVariant
import com.axiel7.anihyou.core.resources.md_theme_light_tertiary
import com.axiel7.anihyou.core.resources.md_theme_light_tertiaryContainer
import com.axiel7.anihyou.core.resources.seed
import com.axiel7.anihyou.core.model.AppColorMode
import com.axiel7.anihyou.core.resources.ColorUtils.isBlack
import com.axiel7.anihyou.core.resources.ColorUtils.isWhite
import com.materialkolor.dynamicColorScheme

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

private val BlackAccentColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = Color.Black,
    secondary = Color.Black,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E0E0),
    onSecondaryContainer = Color.Black,
    tertiary = Color.Black,
    onTertiary = Color.White,
    tertiaryContainer = Color.White,
    onTertiaryContainer = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color.White,
    onSurfaceVariant = Color.Black,
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
    surfaceContainer = Color(0xFFEEEEEE),
    surfaceContainerHigh = Color(0xFFEEEEEE),
    surfaceContainerHighest = Color(0xFFF5F5F5),
    surfaceContainerLow = Color(0xFFE0E0E0),
    surfaceContainerLowest = Color(0xFFBDBDBD),
)

private val LightAccentColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color.Black,
    onPrimaryContainer = Color.White,
    secondary = Color.White,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF262626),
    onSecondaryContainer = Color.White,
    tertiary = Color.White,
    onTertiary = Color.Black,
    tertiaryContainer = Color.Black,
    onTertiaryContainer = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color.Black,
    onSurfaceVariant = Color.White,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
    surfaceContainer = Color(0xFF0D0D0D),
    surfaceContainerHigh = Color(0xFF1A1A1A),
    surfaceContainerHighest = Color(0xFF262626),
    surfaceContainerLow = Color(0xFF2B2B2B),
    surfaceContainerLowest = Color(0xFF333333),
)

private fun ColorScheme.toBlackScheme() = this.copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = surfaceVariant.copy(alpha = 0.4f).compositeOver(Color.Black),
    surfaceContainer = Color.Black,
    surfaceContainerHigh = surfaceContainerHigh.copy(alpha = 0.5f).compositeOver(Color.Black),
    surfaceContainerHighest = surfaceContainerHighest.copy(alpha = 0.6f).compositeOver(Color.Black)
)

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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}