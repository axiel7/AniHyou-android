package com.axiel7.anihyou.data.model

import com.axiel7.anihyou.UserMediaListQuery
import com.axiel7.anihyou.type.MediaType

fun UserMediaListQuery.MediaList.calculateProgressBarValue(): Float {
    val total = when (media?.type) {
        MediaType.ANIME -> media.episodes ?: 0
        MediaType.MANGA -> media.chapters ?: 0
        else -> 0
    }
    return if (total == 0) 0f
    //TODO: volume progress
    else (basicMediaListEntry.progress ?: 0).div(total.toFloat())
}