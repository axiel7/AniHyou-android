package com.axiel7.anihyou.data.model.stats

import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserStatsMangaOverviewQuery
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.NumberUtils.format
import kotlin.math.roundToInt

fun UserStatsMangaOverviewQuery.Manga.planned() =
    statuses?.find { it?.status == MediaListStatus.PLANNING }

fun UserStatsMangaOverviewQuery.Manga.scoreStatsCount() =
    scores?.filterNotNull()?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.meanScore.roundToInt()),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.chapters_read_format,
                    value = it.chaptersRead.format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

fun UserStatsMangaOverviewQuery.Manga.scoreStatsTime() =
    scores?.filterNotNull()?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.meanScore.roundToInt()),
            value = it.chaptersRead.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.chapters_read_format,
                    value = it.chaptersRead.format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

fun UserStatsMangaOverviewQuery.Manga.lengthStatsCount() =
    lengths?.filterNotNull()?.sortedBy { LengthDistribution.lengthComparator(it.length) }?.map {
        StatLocalizableAndColorable(
            type = LengthDistribution(length = it.length),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = chaptersRead.format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

fun UserStatsMangaOverviewQuery.Manga.lengthStatsTime() =
    lengths?.filterNotNull()?.sortedBy { LengthDistribution.lengthComparator(it.length) }?.map {
        StatLocalizableAndColorable(
            type = LengthDistribution(length = it.length),
            value = it.chaptersRead.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = chaptersRead.format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

fun UserStatsMangaOverviewQuery.Manga.lengthStatsScore() =
    lengths?.filterNotNull()?.sortedBy { LengthDistribution.lengthComparator(it.length) }?.map {
        StatLocalizableAndColorable(
            type = LengthDistribution(length = it.length),
            value = it.meanScore.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.hours_watched_format,
                    value = chaptersRead.format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

fun UserStatsMangaOverviewQuery.Manga.statusDistribution() =
    statuses?.filterNotNull()?.filter { it.status != null }?.map {
        StatLocalizableAndColorable(
            type = StatusDistribution.valueOf(
                rawValue = it.status?.rawValue,
                mediaType = MediaType.MANGA
            ) ?: StatusDistribution.READING,
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.chapters_read_format,
                    value = it.chaptersRead.format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }

fun UserStatsMangaOverviewQuery.Manga.formatDistribution() =
    formats?.filterNotNull()?.filter { it.format != null }?.map {
        StatLocalizableAndColorable(
            type = FormatDistribution.valueOf(it.format!!.rawValue),
            value = it.count.toFloat(),
            details = listOf(
                Stat.Detail(
                    name = R.string.chapters_read_format,
                    value = it.chaptersRead.format()
                ),
                Stat.Detail(
                    name = R.string.mean_score_format,
                    value = it.meanScore.format()
                )
            )
        )
    }


