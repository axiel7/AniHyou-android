package com.axiel7.anihyou.core.model.stats.overview

import androidx.compose.runtime.Composable
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.network.MediaStatsQuery
import com.axiel7.anihyou.core.model.base.Colorable
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.model.point100PrimaryColor
import com.axiel7.anihyou.core.model.stats.StatLocalizableAndColorable

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