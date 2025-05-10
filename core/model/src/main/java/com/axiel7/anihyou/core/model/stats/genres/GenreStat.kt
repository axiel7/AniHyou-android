package com.axiel7.anihyou.core.model.stats.genres

import com.axiel7.anihyou.core.network.fragment.GenreStat
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.model.stats.StatDistributionType

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