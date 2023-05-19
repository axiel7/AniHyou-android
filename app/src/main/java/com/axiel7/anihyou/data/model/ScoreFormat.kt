package com.axiel7.anihyou.data.model

import com.axiel7.anihyou.type.ScoreFormat

fun ScoreFormat.maxValue() = when (this) {
    ScoreFormat.POINT_100 -> 100.0
    ScoreFormat.POINT_10_DECIMAL -> 10.0
    ScoreFormat.POINT_10 -> 10.0
    ScoreFormat.POINT_5 -> 5.0
    ScoreFormat.POINT_3 -> 3.0
    ScoreFormat.UNKNOWN__ -> 0.0
}