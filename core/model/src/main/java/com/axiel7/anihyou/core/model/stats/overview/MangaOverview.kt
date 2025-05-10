package com.axiel7.anihyou.core.model.stats.overview

import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.network.UserStatsMangaOverviewQuery
import com.axiel7.anihyou.core.network.type.MediaListStatus
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.model.media.CountryOfOrigin.Companion.toBo
import com.axiel7.anihyou.core.model.stats.Stat
import com.axiel7.anihyou.core.model.stats.StatLocalizableAndColorable
import com.axiel7.anihyou.core.resources.R

fun UserStatsMangaOverviewQuery.Manga.toOverviewStats(scoreFormat: ScoreFormat) =
    OverviewStats(
        count = count,
        episodeOrChapterCount = chaptersRead,
        daysOrVolumes = volumesRead,
        plannedCount = planned()?.chaptersRead ?: 0,
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

private fun UserStatsMangaOverviewQuery.Manga.planned() =
    statuses?.find { it?.status == MediaListStatus.PLANNING }

private fun UserStatsMangaOverviewQuery.Manga.scoreStatsCount() =
    scores?.filterNotNull()?.filter { it.score != null }?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.score!!),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.chapters_read_format,
                    value = it.chaptersRead.format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsMangaOverviewQuery.Manga.scoreStatsTime() =
    scores?.filterNotNull()?.filter { it.score != null }?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.score!!),
            value = it.chaptersRead.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.chapters_read_format,
                    value = it.chaptersRead.format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsMangaOverviewQuery.Manga.lengthStatsCount() =
    lengths?.filterNotNull()?.sortedBy { LengthDistribution.lengthComparator(it.length) }?.map {
        StatLocalizableAndColorable(
            type = LengthDistribution(length = it.length),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = chaptersRead.format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsMangaOverviewQuery.Manga.lengthStatsTime() =
    lengths?.filterNotNull()?.sortedBy { LengthDistribution.lengthComparator(it.length) }?.map {
        StatLocalizableAndColorable(
            type = LengthDistribution(length = it.length),
            value = it.chaptersRead.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = chaptersRead.format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsMangaOverviewQuery.Manga.lengthStatsScore() =
    lengths?.filterNotNull()?.sortedBy { LengthDistribution.lengthComparator(it.length) }?.map {
        StatLocalizableAndColorable(
            type = LengthDistribution(length = it.length),
            value = it.meanScore.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = chaptersRead.format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsMangaOverviewQuery.Manga.statusDistribution() =
    statuses?.filterNotNull()?.filter { it.status != null }?.map {
        StatLocalizableAndColorable(
            type = StatusDistribution.valueOf(
                rawValue = it.status?.rawValue,
            ) ?: StatusDistribution.CURRENT,
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.chapters_read_format,
                    value = it.chaptersRead.format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsMangaOverviewQuery.Manga.formatDistribution() =
    formats?.filterNotNull()?.filter { it.format != null }?.map {
        StatLocalizableAndColorable(
            type = FormatDistribution.valueOf(it.format!!.rawValue),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.chapters_read_format,
                    value = it.chaptersRead.format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsMangaOverviewQuery.Manga.countryDistribution() =
    countries?.filterNotNull()?.filter { it.country != null }?.map {
        StatLocalizableAndColorable(
            type = it.country!!.toBo(),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.chapters_read_format,
                    value = chaptersRead.format().orEmpty()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format().orEmpty()
                )
            )
        )
    }

private fun UserStatsMangaOverviewQuery.Manga.releaseYearCount() =
    releaseYears?.filterNotNull()
        ?.filter { it.releaseYear != null }
        ?.sortedByDescending { it.releaseYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.releaseYear!!),
                value = it.count.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.chapters_read_format,
                        value = chaptersRead.format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsMangaOverviewQuery.Manga.releaseYearTime() =
    releaseYears?.filterNotNull()
        ?.filter { it.releaseYear != null }
        ?.sortedByDescending { it.releaseYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.releaseYear!!),
                value = it.chaptersRead.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.chapters_read_format,
                        value = chaptersRead.format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsMangaOverviewQuery.Manga.releaseYearScore() =
    releaseYears?.filterNotNull()
        ?.filter { it.releaseYear != null }
        ?.sortedByDescending { it.releaseYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.releaseYear!!),
                value = it.meanScore.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.chapters_read_format,
                        value = chaptersRead.format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsMangaOverviewQuery.Manga.startYearCount() =
    startYears?.filterNotNull()
        ?.filter { it.startYear != null }
        ?.sortedByDescending { it.startYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.startYear!!),
                value = it.count.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.chapters_read_format,
                        value = chaptersRead.format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsMangaOverviewQuery.Manga.startYearTime() =
    startYears?.filterNotNull()
        ?.filter { it.startYear != null }
        ?.sortedByDescending { it.startYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.startYear!!),
                value = it.chaptersRead.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.chapters_read_format,
                        value = chaptersRead.format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }

private fun UserStatsMangaOverviewQuery.Manga.startYearScore() =
    startYears?.filterNotNull()
        ?.filter { it.startYear != null }
        ?.sortedByDescending { it.startYear }
        ?.map {
            StatLocalizableAndColorable(
                type = YearDistribution(it.startYear!!),
                value = it.meanScore.toFloat(),
                details = listOf(
                    Stat.Detail(
                        name = R.string.chapters_read_format,
                        value = chaptersRead.format().orEmpty()
                    ),
                    Stat.Detail(
                        name = R.string.mean_score_format,
                        value = it.meanScore.format().orEmpty()
                    )
                )
            )
        }
