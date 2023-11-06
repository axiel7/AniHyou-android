package com.axiel7.anihyou.data.model.stats

import com.axiel7.anihyou.data.model.media.CountryOfOrigin

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
)