package com.axiel7.anihyou.data.model.stats

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import com.axiel7.anihyou.data.model.base.LocalizableAndColorable
import com.axiel7.anihyou.data.model.point100PrimaryColor
import com.axiel7.anihyou.utils.NumberUtils.format

data class ScoreDistribution(
    @IntRange(0, 100) val score: Int
) : LocalizableAndColorable {
    @Composable
    override fun primaryColor() = score.point100PrimaryColor()

    @Composable
    override fun onPrimaryColor() = primaryColor()

    @Composable
    override fun localized(): String = score.format()

}