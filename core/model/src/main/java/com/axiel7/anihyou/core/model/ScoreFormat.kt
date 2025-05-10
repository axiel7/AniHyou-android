package com.axiel7.anihyou.core.model

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.resources.stat_dark_10
import com.axiel7.anihyou.core.resources.stat_dark_100
import com.axiel7.anihyou.core.resources.stat_dark_20
import com.axiel7.anihyou.core.resources.stat_dark_30
import com.axiel7.anihyou.core.resources.stat_dark_40
import com.axiel7.anihyou.core.resources.stat_dark_50
import com.axiel7.anihyou.core.resources.stat_dark_60
import com.axiel7.anihyou.core.resources.stat_dark_70
import com.axiel7.anihyou.core.resources.stat_dark_80
import com.axiel7.anihyou.core.resources.stat_dark_90
import com.axiel7.anihyou.core.resources.stat_dark_on10
import com.axiel7.anihyou.core.resources.stat_dark_on100
import com.axiel7.anihyou.core.resources.stat_dark_on20
import com.axiel7.anihyou.core.resources.stat_dark_on30
import com.axiel7.anihyou.core.resources.stat_dark_on40
import com.axiel7.anihyou.core.resources.stat_dark_on50
import com.axiel7.anihyou.core.resources.stat_dark_on60
import com.axiel7.anihyou.core.resources.stat_dark_on70
import com.axiel7.anihyou.core.resources.stat_dark_on80
import com.axiel7.anihyou.core.resources.stat_dark_on90
import com.axiel7.anihyou.core.resources.stat_light_10
import com.axiel7.anihyou.core.resources.stat_light_100
import com.axiel7.anihyou.core.resources.stat_light_20
import com.axiel7.anihyou.core.resources.stat_light_30
import com.axiel7.anihyou.core.resources.stat_light_40
import com.axiel7.anihyou.core.resources.stat_light_50
import com.axiel7.anihyou.core.resources.stat_light_60
import com.axiel7.anihyou.core.resources.stat_light_70
import com.axiel7.anihyou.core.resources.stat_light_80
import com.axiel7.anihyou.core.resources.stat_light_90
import com.axiel7.anihyou.core.resources.stat_light_on10
import com.axiel7.anihyou.core.resources.stat_light_on100
import com.axiel7.anihyou.core.resources.stat_light_on20
import com.axiel7.anihyou.core.resources.stat_light_on30
import com.axiel7.anihyou.core.resources.stat_light_on40
import com.axiel7.anihyou.core.resources.stat_light_on50
import com.axiel7.anihyou.core.resources.stat_light_on60
import com.axiel7.anihyou.core.resources.stat_light_on70
import com.axiel7.anihyou.core.resources.stat_light_on80
import com.axiel7.anihyou.core.resources.stat_light_on90
import kotlin.math.roundToInt

fun ScoreFormat.maxValue() = when (this) {
    ScoreFormat.POINT_100 -> 100.0
    ScoreFormat.POINT_10_DECIMAL -> 10.0
    ScoreFormat.POINT_10 -> 10.0
    ScoreFormat.POINT_5 -> 5.0
    ScoreFormat.POINT_3 -> 3.0
    ScoreFormat.UNKNOWN__ -> 0.0
}

fun ScoreFormat.canUseAdvancedScoring() = when (this) {
    ScoreFormat.POINT_100, ScoreFormat.POINT_10_DECIMAL -> true
    else -> false
}

fun ScoreFormat.stringRes() = when (this) {
    ScoreFormat.POINT_100 -> R.string.score_point_100
    ScoreFormat.POINT_10_DECIMAL -> R.string.score_point_10_decimal
    ScoreFormat.POINT_10 -> R.string.score_point_10
    ScoreFormat.POINT_5 -> R.string.score_point_5
    ScoreFormat.POINT_3 -> R.string.score_point_3
    ScoreFormat.UNKNOWN__ -> R.string.unknown
}

val ScoreFormat.Companion.entriesLocalized
    get() = knownEntries.associateWith { it.stringRes() }

@Composable
fun Double?.scorePrimaryColor(format: ScoreFormat): Color {
    if (this == null) return MaterialTheme.colorScheme.outline
    return when (format) {
        ScoreFormat.POINT_100 -> this.toInt().point100PrimaryColor()
        ScoreFormat.POINT_10_DECIMAL -> this.point10DecimalPrimaryColor()
        ScoreFormat.POINT_10 -> this.toInt().point10PrimaryColor()
        ScoreFormat.POINT_5 -> this.toInt().point5PrimaryColor()
        ScoreFormat.POINT_3 -> this.toInt().smileyPrimaryColor()
        ScoreFormat.UNKNOWN__ -> MaterialTheme.colorScheme.outline
    }
}

@Composable
fun Double?.scoreOnPrimaryColor(format: ScoreFormat): Color {
    if (this == null) return MaterialTheme.colorScheme.onSurface
    return when (format) {
        ScoreFormat.POINT_100 -> this.toInt().point100OnPrimaryColor()
        ScoreFormat.POINT_10_DECIMAL -> this.point10DecimalOnPrimaryColor()
        ScoreFormat.POINT_10 -> this.toInt().point10OnPrimaryColor()
        ScoreFormat.POINT_5 -> this.toInt().point5OnPrimaryColor()
        ScoreFormat.POINT_3 -> this.toInt().smileyOnPrimaryColor()
        ScoreFormat.UNKNOWN__ -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
fun Int.point100PrimaryColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when {
        this == 0 -> MaterialTheme.colorScheme.outline
        this < 20 -> if (isDark) stat_dark_10 else stat_light_10
        this < 30 -> if (isDark) stat_dark_20 else stat_light_20
        this < 40 -> if (isDark) stat_dark_30 else stat_light_30
        this < 50 -> if (isDark) stat_dark_40 else stat_light_40
        this < 60 -> if (isDark) stat_dark_50 else stat_light_50
        this < 70 -> if (isDark) stat_dark_60 else stat_light_60
        this < 80 -> if (isDark) stat_dark_70 else stat_light_70
        this < 90 -> if (isDark) stat_dark_80 else stat_light_80
        this < 100 -> if (isDark) stat_dark_90 else stat_light_90
        this == 100 -> if (isDark) stat_dark_100 else stat_light_100
        else -> MaterialTheme.colorScheme.outline
    }
}

@Composable
fun Int.point100OnPrimaryColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when {
        this == 0 -> MaterialTheme.colorScheme.onSurface
        this < 20 -> if (isDark) stat_dark_on10 else stat_light_on10
        this < 30 -> if (isDark) stat_dark_on20 else stat_light_on20
        this < 40 -> if (isDark) stat_dark_on30 else stat_light_on30
        this < 50 -> if (isDark) stat_dark_on40 else stat_light_on40
        this < 60 -> if (isDark) stat_dark_on50 else stat_light_on50
        this < 70 -> if (isDark) stat_dark_on60 else stat_light_on60
        this < 80 -> if (isDark) stat_dark_on70 else stat_light_on70
        this < 90 -> if (isDark) stat_dark_on80 else stat_light_on80
        this < 100 -> if (isDark) stat_dark_on90 else stat_light_on90
        this == 100 -> if (isDark) stat_dark_on100 else stat_light_on100
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
fun Double.point10DecimalPrimaryColor(): Color {
    return (this.roundToInt() * 10).point100PrimaryColor()
}

@Composable
fun Double.point10DecimalOnPrimaryColor(): Color {
    return (this.roundToInt() * 10).point100OnPrimaryColor()
}

@Composable
fun Int.point10PrimaryColor(): Color {
    return (this * 10).point100PrimaryColor()
}

@Composable
fun Int.point10OnPrimaryColor(): Color {
    return (this * 10).point100OnPrimaryColor()
}

@Composable
fun Int.point5PrimaryColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        1 -> if (isDark) stat_dark_10 else stat_light_10
        2 -> if (isDark) stat_dark_30 else stat_light_30
        3 -> if (isDark) stat_dark_50 else stat_light_50
        4 -> if (isDark) stat_dark_80 else stat_light_80
        5 -> if (isDark) stat_dark_100 else stat_light_100
        else -> MaterialTheme.colorScheme.outline
    }
}

@Composable
fun Int.point5OnPrimaryColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        1 -> if (isDark) stat_dark_on10 else stat_light_on10
        2 -> if (isDark) stat_dark_on30 else stat_light_on30
        3 -> if (isDark) stat_dark_on50 else stat_light_on50
        4 -> if (isDark) stat_dark_on80 else stat_light_on80
        5 -> if (isDark) stat_dark_on100 else stat_light_on100
        else -> MaterialTheme.colorScheme.onSurface
    }
}

@Composable
fun Int.smileyPrimaryColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        1 -> if (isDark) stat_dark_10 else stat_light_10
        2 -> if (isDark) stat_dark_50 else stat_light_50
        3 -> if (isDark) stat_dark_100 else stat_light_100
        else -> MaterialTheme.colorScheme.outline
    }
}

@Composable
fun Int.smileyOnPrimaryColor(): Color {
    val isDark = isSystemInDarkTheme()
    return when (this) {
        1 -> if (isDark) stat_dark_on10 else stat_light_on10
        2 -> if (isDark) stat_dark_on50 else stat_light_on50
        3 -> if (isDark) stat_dark_on100 else stat_light_on100
        else -> MaterialTheme.colorScheme.onSurface
    }
}

fun Int.smileyIcon(filled: Boolean) = when (this) {
    1 -> if (filled) R.drawable.sentiment_dissatisfied_filled_24 else R.drawable.sentiment_dissatisfied_24
    2 -> if (filled) R.drawable.sentiment_neutral_filled_24 else R.drawable.sentiment_neutral_24
    3 -> if (filled) R.drawable.sentiment_satisfied_filled_24 else R.drawable.sentiment_satisfied_24
    else -> R.drawable.error_24
}