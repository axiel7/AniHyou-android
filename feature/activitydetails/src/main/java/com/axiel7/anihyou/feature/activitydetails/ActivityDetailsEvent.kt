package com.axiel7.anihyou.feature.activitydetails

import androidx.compose.runtime.Immutable

@Immutable
interface ActivityDetailsEvent {
    fun toggleLikeActivity()
    fun toggleLikeReply(id: Int)
    fun refresh()
}