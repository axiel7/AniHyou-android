package com.axiel7.anihyou.data.model

import com.axiel7.anihyou.UserMediaListQuery

fun UserMediaListQuery.MediaList.calculateProgressBarValue(): Float {
    val total = media?.basicMediaDetails?.duration() ?: 0
    return if (total == 0) 0f
    //TODO: volume progress
    else (basicMediaListEntry.progress ?: 0).div(total.toFloat())
}