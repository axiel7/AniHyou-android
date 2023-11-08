package com.axiel7.anihyou.data.model.stats.overview

import com.axiel7.anihyou.data.model.media.CountryOfOrigin
import com.axiel7.anihyou.data.model.stats.StatLocalizableAndColorable

data class OverviewStats(
    val count: Int,
    val episodeOrChapterCount: Int,
    val daysOrVolumes: Int,
    val plannedCount: Int,
    val meanScore: Double,
    val standardDeviation: Double,
    val scoreCount: List<StatLocalizableAndColorable<ScoreDistribution>>,
    val scoreTime: List<StatLocalizableAndColorable<ScoreDistribution>>,
    val lengthCount: List<StatLocalizableAndColorable<LengthDistribution>>,
    val lengthTime: List<StatLocalizableAndColorable<LengthDistribution>>,
    val lengthScore: List<StatLocalizableAndColorable<LengthDistribution>>,
    val statusDistribution: List<StatLocalizableAndColorable<StatusDistribution>>,
    val formatDistribution: List<StatLocalizableAndColorable<FormatDistribution>>,
    val countryDistribution: List<StatLocalizableAndColorable<CountryOfOrigin>>,
    val releaseYearCount: List<StatLocalizableAndColorable<YearDistribution>>,
    val releaseYearTime: List<StatLocalizableAndColorable<YearDistribution>>,
    val releaseYearScore: List<StatLocalizableAndColorable<YearDistribution>>,
    val startYearCount: List<StatLocalizableAndColorable<YearDistribution>>,
    val startYearTime: List<StatLocalizableAndColorable<YearDistribution>>,
    val startYearScore: List<StatLocalizableAndColorable<YearDistribution>>,
)