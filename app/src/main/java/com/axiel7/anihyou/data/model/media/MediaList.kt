package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.fragment.BasicMediaListEntry

fun UserMediaListQuery.MediaList.calculateProgressBarValue(): Float {
    val total = media?.basicMediaDetails?.duration() ?: 0
    return if (total == 0) 0f
    //TODO: volume progress
    else (basicMediaListEntry.progress ?: 0).div(total.toFloat())
}

fun BasicMediaListEntry.isBehind(nextAiringEpisode: Int) = (progress ?: 0) < (nextAiringEpisode - 1)