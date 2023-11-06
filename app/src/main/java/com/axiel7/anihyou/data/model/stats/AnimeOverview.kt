package com.axiel7.anihyou.data.model.stats

import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserStatsAnimeOverviewQuery
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.utils.NumberUtils.format
import kotlin.math.roundToInt

fun UserStatsAnimeOverviewQuery.Anime.planned() =
    statuses?.find { it?.status == MediaListStatus.PLANNING }

fun UserStatsAnimeOverviewQuery.Anime.scoreStatsCount() =
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

fun UserStatsAnimeOverviewQuery.Anime.scoreStatsTime() =
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

fun UserStatsAnimeOverviewQuery.Anime.statusDistribution() =
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

fun UserStatsAnimeOverviewQuery.Anime.formatDistribution() =
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


