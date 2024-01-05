package com.axiel7.anihyou.data.model.stats.overview

import androidx.compose.runtime.Composable
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.data.model.point100PrimaryColor
import com.axiel7.anihyou.data.model.stats.StatLocalizableAndColorable
import com.axiel7.anihyou.utils.NumberUtils.format

data class ScoreDistribution(
    val score: Int
) : Localizable, Colorable {

    @Composable
    override fun primaryColor() = score.point100PrimaryColor()

    @Composable
    override fun onPrimaryColor() = primaryColor()

    @Composable
    override fun localized(): String = score.format().orEmpty()

    companion object {
        fun MediaStatsQuery.ScoreDistribution.asStat() =
            StatLocalizableAndColorable(
                type = ScoreDistribution(score = score ?: 0),
                value = amount?.toFloat() ?: 0f
            )
    }
}