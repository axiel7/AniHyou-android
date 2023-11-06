package com.axiel7.anihyou.data.model.stats

import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserStatsAnimeOverviewQuery
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.DateUtils.minutesToDays
import com.axiel7.anihyou.utils.NumberUtils.format
import kotlin.math.roundToInt

fun UserStatsAnimeOverviewQuery.Anime.toOverviewStats() =
    OverviewStats(
        count = count,
        episodeOrChapterCount = episodesWatched,
        daysOrVolumes = minutesWatched.minutesToDays(),
        plannedCount = planned()?.minutesWatched?.minutesToDays() ?: 0,
        meanScore = meanScore,
        standardDeviation = standardDeviation,
        scoreCount = scoreStatsCount().orEmpty(),
        scoreTime = scoreStatsTime().orEmpty(),
        lengthCount = lengthStatsCount().orEmpty(),
        lengthTime = lengthStatsTime().orEmpty(),
        lengthScore = lengthStatsScore().orEmpty(),
        statusDistribution = statusDistribution().orEmpty(),
        formatDistribution = formatDistribution().orEmpty(),
        countryDistribution = countryDistribution().orEmpty(),
    )

private fun UserStatsAnimeOverviewQuery.Anime.planned() =
    statuses?.find { it?.status == MediaListStatus.PLANNING }

private fun UserStatsAnimeOverviewQuery.Anime.scoreStatsCount() =
    scores?.filterNotNull()?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.meanScore.roundToInt()),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.scoreStatsTime() =
    scores?.filterNotNull()?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.meanScore.roundToInt()),
            value = it.minutesWatched.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.lengthStatsCount() =
    lengths?.filterNotNull()?.sortedBy { LengthDistribution.lengthComparator(it.length) }?.map {
        StatLocalizableAndColorable(
            type = LengthDistribution(length = it.length),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.lengthStatsTime() =
    lengths?.filterNotNull()?.sortedBy { LengthDistribution.lengthComparator(it.length) }?.map {
        StatLocalizableAndColorable(
            type = LengthDistribution(length = it.length),
            value = it.minutesWatched.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.lengthStatsScore() =
    lengths?.filterNotNull()?.sortedBy { LengthDistribution.lengthComparator(it.length) }?.map {
        StatLocalizableAndColorable(
            type = LengthDistribution(length = it.length),
            value = it.meanScore.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.statusDistribution() =
    statuses?.filterNotNull()?.filter { it.status != null }?.map {
        StatLocalizableAndColorable(
            type = StatusDistribution.valueOf(
                rawValue = it.status?.rawValue,
                mediaType = MediaType.ANIME
            ) ?: StatusDistribution.WATCHING,
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.formatDistribution() =
    formats?.filterNotNull()?.filter { it.format != null }?.map {
        StatLocalizableAndColorable(
            type = FormatDistribution.valueOf(it.format!!.rawValue),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.countryDistribution() =
    countries?.filterNotNull()?.filter { it.country != null }?.map {
        StatLocalizableAndColorable(
            type = it.country!!,
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }


