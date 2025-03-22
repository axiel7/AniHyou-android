package com.axiel7.anihyou.core.model.stats.overview

import com.axiel7.anihyou.core.common.utils.DateUtils.minutesToDays
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.network.UserStatsAnimeOverviewQuery
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.model.media.CountryOfOrigin.Companion.toBo
import com.axiel7.anihyou.core.model.stats.Stat
import com.axiel7.anihyou.core.model.stats.StatLocalizableAndColorable
import com.axiel7.anihyou.core.resources.R

fun UserStatsAnimeOverviewQuery.Anime.toOverviewStats(scoreFormat: ScoreFormat) =
    OverviewStats(
        count = count,
        episodeOrChapterCount = episodesWatched,
        daysOrVolumes = minutesWatched.minutesToDays(),
        plannedCount = planned()?.minutesWatched?.minutesToDays() ?: 0,
        meanScore = meanScore,
        scoreFormat = scoreFormat,
        standardDeviation = standardDeviation,
        scoreCount = scoreStatsCount().orEmpty(),
        scoreTime = scoreStatsTime().orEmpty(),
        lengthCount = lengthStatsCount().orEmpty(),
        lengthTime = lengthStatsTime().orEmpty(),
        lengthScore = lengthStatsScore().orEmpty(),
        statusDistribution = statusDistribution().orEmpty(),
        formatDistribution = formatDistribution().orEmpty(),
        countryDistribution = countryDistribution().orEmpty(),
        releaseYearCount = releaseYearCount().orEmpty(),
        releaseYearTime = releaseYearTime().orEmpty(),
        releaseYearScore = releaseYearScore().orEmpty(),
        startYearCount = startYearCount().orEmpty(),
        startYearTime = startYearTime().orEmpty(),
        startYearScore = startYearScore().orEmpty(),
    )

private fun UserStatsAnimeOverviewQuery.Anime.planned() =
    statuses?.find { it?.status == MediaListStatus.PLANNING }

private fun UserStatsAnimeOverviewQuery.Anime.scoreStatsCount() =
    scores?.filterNotNull()?.filter { it.score != null }?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.score!!),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.scoreStatsTime() =
    scores?.filterNotNull()?.filter { it.score != null }?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.score!!),
            value = it.minutesWatched / 60f,
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.lengthStatsCount() =
    lengths?.filterNotNull()
        ?.sortedBy { LengthDistribution.lengthComparator(it.length) }
        ?.map {
            StatLocalizableAndColorable(
                type = LengthDistribution(length = it.length),
                value = it.count.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.hours_watched_format,
                        value = (it.minutesWatched / 60).format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsAnimeOverviewQuery.Anime.lengthStatsTime() =
    lengths?.filterNotNull()
        ?.sortedBy { LengthDistribution.lengthComparator(it.length) }
        ?.map {
            StatLocalizableAndColorable(
                type = LengthDistribution(length = it.length),
                value = it.minutesWatched / 60f,
                details = listOf(
                    Stat.Detail(
                        name = R.string.hours_watched_format,
                        value = (it.minutesWatched / 60).format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsAnimeOverviewQuery.Anime.lengthStatsScore() =
    lengths?.filterNotNull()
        ?.sortedBy { LengthDistribution.lengthComparator(it.length) }
        ?.map {
            StatLocalizableAndColorable(
                type = LengthDistribution(length = it.length),
                value = it.meanScore.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.hours_watched_format,
                        value = (it.minutesWatched / 60).format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsAnimeOverviewQuery.Anime.statusDistribution() =
    statuses?.filterNotNull()?.filter { it.status != null }?.map {
        StatLocalizableAndColorable(
            type = StatusDistribution.valueOf(rawValue = it.status?.rawValue)
                ?: StatusDistribution.CURRENT,
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
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
                    value = (it.minutesWatched / 60).format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.countryDistribution() =
    countries?.filterNotNull()?.filter { it.country != null }?.map {
        StatLocalizableAndColorable(
            type = it.country!!.toBo(),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = (it.minutesWatched / 60).format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsAnimeOverviewQuery.Anime.releaseYearCount() =
    releaseYears?.filterNotNull()
        ?.filter { it.releaseYear != null }
        ?.sortedByDescending { it.releaseYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.releaseYear!!),
                value = it.count.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.hours_watched_format,
                        value = (it.minutesWatched / 60).format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsAnimeOverviewQuery.Anime.releaseYearTime() =
    releaseYears?.filterNotNull()
        ?.filter { it.releaseYear != null }
        ?.sortedByDescending { it.releaseYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.releaseYear!!),
                value = it.minutesWatched / 60f,
                details = listOf(
                    Stat.Detail(
                        name = R.string.hours_watched_format,
                        value = (it.minutesWatched / 60).format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsAnimeOverviewQuery.Anime.releaseYearScore() =
    releaseYears?.filterNotNull()
        ?.filter { it.releaseYear != null }
        ?.sortedByDescending { it.releaseYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.releaseYear!!),
                value = it.meanScore.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.hours_watched_format,
                        value = (it.minutesWatched / 60).format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsAnimeOverviewQuery.Anime.startYearCount() =
    startYears?.filterNotNull()
        ?.filter { it.startYear != null }
        ?.sortedByDescending { it.startYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.startYear!!),
                value = it.count.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.hours_watched_format,
                        value = (it.minutesWatched / 60).format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsAnimeOverviewQuery.Anime.startYearTime() =
    startYears?.filterNotNull()
        ?.filter { it.startYear != null }
        ?.sortedByDescending { it.startYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.startYear!!),
                value = it.minutesWatched.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.hours_watched_format,
                        value = (it.minutesWatched / 60).format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsAnimeOverviewQuery.Anime.startYearScore() =
    startYears?.filterNotNull()
        ?.filter { it.startYear != null }
        ?.sortedByDescending { it.startYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.startYear!!),
                value = it.meanScore.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.hours_watched_format,
                        value = (it.minutesWatched / 60).format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }
