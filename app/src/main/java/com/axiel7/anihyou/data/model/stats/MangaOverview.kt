package com.axiel7.anihyou.data.model.stats

import com.axiel7.anihyou.UserStatsMangaOverviewQuery
import com.axiel7.anihyou.type.MediaListStatus
import com.axiel7.anihyou.type.MediaType
import kotlin.math.roundToInt

fun UserStatsMangaOverviewQuery.Manga.planned() =
    statuses?.find { it?.status == MediaListStatus.PLANNING }

fun UserStatsMangaOverviewQuery.Manga.scoreStatsCount() =
    scores?.filterNotNull()?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.meanScore.roundToInt()),
            value = it.count.toFloat()
        )
    }

fun UserStatsMangaOverviewQuery.Manga.scoreStatsTime() =
    scores?.filterNotNull()?.map {
        StatLocalizableAndColorable(
            type = ScoreDistribution(score = it.meanScore.roundToInt()),
            value = it.chaptersRead.toFloat()
        )
    }

fun UserStatsMangaOverviewQuery.Manga.statusDistribution() =
    statuses?.filterNotNull()?.filter { it.status != null }?.map {
        StatLocalizableAndColorable(
            type = StatusDistribution.valueOf(
                rawValue = it.status?.rawValue,
                mediaType = MediaType.MANGA
            ) ?: StatusDistribution.READING,
            value = it.count.toFloat()
        )
    }

fun UserStatsMangaOverviewQuery.Manga.formatDistribution() =
    formats?.filterNotNull()?.filter { it.format != null }?.map {
        StatLocalizableAndColorable(
            type = FormatDistribution.valueOf(it.format!!.rawValue),
            value = it.count.toFloat()
        )
    }


