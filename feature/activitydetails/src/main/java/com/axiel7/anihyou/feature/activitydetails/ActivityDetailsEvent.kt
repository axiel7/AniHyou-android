package com.axiel7.anihyou.feature.activitydetails

interface ActivityDetailsEvent {
    fun toggleLikeActivity()
    fun toggleLikeReply(id: Int)
    fun refresh()
}