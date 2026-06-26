package com.axiel7.anihyou.core.ui.common

import androidx.compose.runtime.staticCompositionLocalOf
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.ui.utils.LocaleUtils.getCurrentLanguageTag

val LocalIsLanguageEn = staticCompositionLocalOf {
    getCurrentLanguageTag()?.startsWith("en") == true
}

val LocalBlurAdult = staticCompositionLocalOf { true }

val LocalScoreFormat = staticCompositionLocalOf { ScoreFormat.POINT_10_DECIMAL }

val LocalHideScores = staticCompositionLocalOf { false }