package com.axiel7.anihyou.data.model.stats.genres

import com.axiel7.anihyou.data.model.stats.StatDistributionType
import com.axiel7.anihyou.fragment.GenreStat
import com.axiel7.anihyou.type.MediaType

fun List<GenreStat>.sortedBy(
    type: StatDistributionType,
    mediaType: MediaType,
) = when (type) {
    StatDistributionType.TITLES -> sortedByDescending { it.count }

    StatDistributionType.TIME ->
        if (mediaType == MediaType.ANIME)
            sortedByDescending { it.minutesWatched }
        else
            sortedByDescending { it.chaptersRead }

    StatDistributionType.SCORE -> sortedByDescending { it.meanScore }
}