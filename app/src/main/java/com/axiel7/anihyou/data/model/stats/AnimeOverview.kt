package com.axiel7.anihyou.data.model.stats

import com.axiel7.anihyou.UserStatsAnimeOverviewQuery
import com.axiel7.anihyou.type.MediaListStatus
import kotlin.math.roundToInt

fun UserStatsAnimeOverviewQuery.Anime.planned() =
    statuses?.find { it?.status == MediaListStatus.PLANNING }

fun UserStatsAnimeOverviewQuery.Anime.scoreStatsCount() =
    scores?.filterNotNull()?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.meanScore.roundToInt()),
            value = it.count.toFloat()
        )
    }

fun UserStatsAnimeOverviewQuery.Anime.scoreStatsTime() =
    scores?.filterNotNull()?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.meanScore.roundToInt()),
            value = it.minutesWatched.toFloat()
        )
    }

fun UserStatsAnimeOverviewQuery.Anime.statusDistribution() =
    statuses?.filterNotNull()?.filter { it.status != null }?.map {
        StatLocalizableAndColorable(
            type = StatusDistribution.valueOf(it.status!!.rawValue),
            value = it.count.toFloat()
        )
    }

fun UserStatsAnimeOverviewQuery.Anime.formatDistribution() =
    formats?.filterNotNull()?.filter { it.format != null }?.map {
        StatLocalizableAndColorable(
            type = FormatDistribution.valueOf(it.format!!.rawValue),
            value = it.count.toFloat()
        )
    }


