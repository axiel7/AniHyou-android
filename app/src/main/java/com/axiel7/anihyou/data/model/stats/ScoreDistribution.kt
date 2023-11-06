package com.axiel7.anihyou.data.model.stats

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.MediaStatsQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Colorable
import com.axiel7.anihyou.data.model.base.Localizable
import com.axiel7.anihyou.data.model.point100PrimaryColor
import com.axiel7.anihyou.utils.NumberUtils.format

data class ScoreDistribution(
    @IntRange(0, 100) val score: Int
) : Localizable, Colorable {
    @Composable
    override fun primaryColor() = score.point100PrimaryColor()

    @Composable
    override fun onPrimaryColor() = primaryColor()

    @Composable
    override fun localized(): String = score.format()

    enum class Type : Localizable {
        TITLES, TIME;

        @Composable
        override fun localized() = when (this) {
            TITLES -> stringResource(R.string.title_count)
            TIME -> stringResource(R.string.time_spent)
        }
    }

    companion object {
        fun MediaStatsQuery.ScoreDistribution.asStat() =
            StatLocalizableAndColorable(
                type = ScoreDistribution(score = score ?: 0),
                value = amount?.toFloat() ?: 0f
            )
    }
}