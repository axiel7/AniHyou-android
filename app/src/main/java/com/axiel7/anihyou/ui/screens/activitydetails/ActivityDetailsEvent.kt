package com.axiel7.anihyou.ui.screens.activitydetails

interface ActivityDetailsEvent {
    fun toggleLikeActivity()
    fun toggleLikeReply(id: Int)
    fun refresh()
}