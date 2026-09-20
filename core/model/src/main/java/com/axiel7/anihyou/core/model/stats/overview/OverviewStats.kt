package com.axiel7.anihyou.core.model.stats.overview

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.model.media.CountryOfOrigin
import com.axiel7.anihyou.core.model.stats.StatLocalizableAndColorable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class OverviewStats(
    val count: Int,
    val episodeOrChapterCount: Int,
    val daysOrVolumes: Int,
    val plannedCount: Int,
    val meanScore: Double,
    val scoreFormat: ScoreFormat,
    val standardDeviation: Double,
    val scoreCount: ImmutableList<StatLocalizableAndColorable<ScoreDistribution>>,
    val scoreTime: ImmutableList<StatLocalizableAndColorable<ScoreDistribution>>,
    val lengthCount: ImmutableList<StatLocalizableAndColorable<LengthDistribution>>,
    val lengthTime: ImmutableList<StatLocalizableAndColorable<LengthDistribution>>,
    val lengthScore: ImmutableList<StatLocalizableAndColorable<LengthDistribution>>,
    val statusDistribution: ImmutableList<StatLocalizableAndColorable<StatusDistribution>>,
    val formatDistribution: ImmutableList<StatLocalizableAndColorable<FormatDistribution>>,
    val countryDistribution: ImmutableList<StatLocalizableAndColorable<CountryOfOrigin>>,
    val releaseYearCount: ImmutableList<StatLocalizableAndColorable<YearDistribution>>,
    val releaseYearTime: ImmutableList<StatLocalizableAndColorable<YearDistribution>>,
    val releaseYearScore: ImmutableList<StatLocalizableAndColorable<YearDistribution>>,
    val startYearCount: ImmutableList<StatLocalizableAndColorable<YearDistribution>>,
    val startYearTime: ImmutableList<StatLocalizableAndColorable<YearDistribution>>,
    val startYearScore: ImmutableList<StatLocalizableAndColorable<YearDistribution>>,
)